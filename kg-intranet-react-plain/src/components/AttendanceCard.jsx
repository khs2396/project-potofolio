import { useEffect, useState } from 'react'
import * as AttendanceApi from '../services/attendance.api.js'
import { useAuth } from '../context/AuthContext.jsx'
import * as Employees from '../services/employees.js'

export default function AttendanceCard({ adminView = false, empNo, refresh = 0 }) {
  const { user } = useAuth()
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [nameMap, setNameMap] = useState({})

  useEffect(() => {
    // 직원 이름 매핑 로드
    Employees.ensureLoaded()
      .then(list => {
        const map = {}
        list.forEach(e => {
          const key = e.empNo ?? e.depno ?? e.id
          if (key) map[key] = e.name || ''
        })
        setNameMap(map)
      })
      .catch(() => { /* ignore */ })
  }, [])

  const load = async () => {
    setLoading(true)
    setError('')
    try {
      let res
      if (empNo) {
        // 우선 대상자 기준 조회 시도
        try {
          res = await AttendanceApi.listMy(1, 5, empNo)
        } catch (err) {
          // 일부 환경에서 empNo 파라미터 무시/권한 거부 시 전체 조회 후 필터
          if (adminView && (err?.status === 401 || err?.status === 403)) {
            const allRes = await AttendanceApi.listAll(1, 500)
            const filtered = (Array.isArray(allRes?.items) ? allRes.items : []).filter(it => String(it.empNo) === String(empNo))
            res = { items: filtered }
          } else {
            throw err
          }
        }
      } else {
        res = adminView ? await AttendanceApi.listAll(1, 5) : await AttendanceApi.listMy(1, 5)
      }
      const list = Array.isArray(res?.items) ? res.items : []
      setItems(list)
    } catch (err) {
      // 권한 없으면 관리자 모드만 제한
      if (adminView && (err?.status === 401 || err?.status === 403)) {
        setItems([])
        setError('근태 조회 권한이 없습니다.')
      } else {
        setError(err?.message || '근태 정보를 불러오지 못했습니다.')
      }
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [refresh, empNo, adminView])

  const adminLabel = adminView
    ? `관리자 ${user?.empNo || user?.depno || ''}${user?.name ? ` · ${user.name}` : ''}`.trim()
    : null

  const doCheckIn = async () => {
    setError('')
    try {
      await AttendanceApi.checkIn()
      await load()
    } catch (err) {
      setError(err?.message || '출근 처리에 실패했습니다.')
    }
  }

  const doCheckOut = async () => {
    setError('')
    try {
      await AttendanceApi.checkOut()
      await load()
    } catch (err) {
      setError(err?.message || '퇴근 처리에 실패했습니다.')
    }
  }

  return (
    <div
      className="card card--employee border-gray"
      style={{ maxWidth: 840 }}
    >
      <div className="card-head" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ margin: 0, display: 'flex', gap: 10, alignItems: 'center', flexWrap: 'wrap' }}>
            <span>{adminView ? '근태 현황' : '나의 근태'}</span>
            {adminView && adminLabel && <span style={{ color: '#3b82f6', fontWeight: 600 }}>{adminLabel}</span>}
          </h2>
        </div>
        {!adminView && (
          <div style={{ display: 'flex', gap: 8 }}>
            <button className="btn sm primary" onClick={doCheckIn}>출근</button>
            <button className="btn sm" onClick={doCheckOut}>퇴근</button>
          </div>
        )}
      </div>
      {loading && <p>불러오는 중...</p>}
      {error && <div style={{ color: '#b91c1c', marginBottom: 8 }}>{error}</div>}
      {!loading && !items.length && <p>근태 기록이 없습니다.</p>}
      {items.length > 0 && (
        <>
          {adminView && <div style={{ borderTop: '1px dashed #cbd5e1', margin: '8px 0' }} />}
          <table className="table modern" style={{ width: '100%', marginTop: 8, textAlign: 'center' }}>
            <thead>
              <tr>
                <th>사번</th>
                <th>이름</th>
                <th>날짜</th>
                <th>출근</th>
                <th>퇴근</th>
                <th>상태</th>
              </tr>
            </thead>
            <tbody>
              {items.map((it, idx) => (
                <tr key={it.attSeq || idx}>
                  <td>{it.empNo}</td>
                  <td>{nameMap[it.empNo] || '-'}</td>
                  <td>{formatDate(it.workdate)}</td>
                  <td>{formatTime(it.clockIn)}</td>
                  <td>{formatTime(it.clockOut)}</td>
                  <td>
                    <span className="badge">{renderStatus(it)}</span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </div>
  )
}

function formatDate(d) {
  if (!d) return '-'
  const date = new Date(d)
  return date.toISOString().slice(0, 10)
}

function formatTime(t) {
  if (!t) return '-'
  const d = new Date(t)
  return d.toTimeString().slice(0, 5)
}

function renderStatus(it) {
  const hasServer = it.inStatus || it.outStatus
  if (hasServer) {
    const parts = []
    if (it.inStatus) parts.push(it.inStatus)
    if (it.outStatus) parts.push(it.outStatus)
    return parts.join(' / ')
  }
  // 프런트 기준 계산: 09:00 이후 출근 -> 지각, 18:00 이전 퇴근 -> 조퇴
  const parts = []
  if (isLate(it.clockIn)) parts.push('지각')
  if (isEarlyLeave(it.clockOut)) parts.push('조퇴')
  if (parts.length === 0) return '정상'
  return parts.join(' / ')
}

function isLate(clockIn) {
  if (!clockIn) return false
  const d = new Date(clockIn)
  const h = d.getHours()
  const m = d.getMinutes()
  return h > 9 || (h === 9 && m > 0)
}

function isEarlyLeave(clockOut) {
  if (!clockOut) return false
  const d = new Date(clockOut)
  const h = d.getHours()
  if (h < 18) return true
  return false
}
