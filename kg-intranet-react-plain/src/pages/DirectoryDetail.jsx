import { useEffect, useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import * as Emp from '../services/employees.js'
import { useAuth } from '../context/AuthContext.jsx'
import AttendanceCard from '../components/AttendanceCard.jsx'
import * as Attendance from '../services/attendance.api.js'
import { getDeptColor } from '../utils/avatarColors.js'

export default function DirectoryDetail() {
  const { id } = useParams()
  const nav = useNavigate()
  const { user } = useAuth()
  const [record, setRecord] = useState(() => Emp.getById(id))
  const [loading, setLoading] = useState(!record)
  const [error, setError] = useState('')
  const [showMsg, setShowMsg] = useState(false)
  const [reason, setReason] = useState('')
  const [showTerminate, setShowTerminate] = useState(false)
  const [terminateReason, setTerminateReason] = useState('')
  const [attRefresh, setAttRefresh] = useState(0)
  const [editDate, setEditDate] = useState(() => new Date().toISOString().slice(0, 10))
  const [editInTime, setEditInTime] = useState('')
  const [editOutTime, setEditOutTime] = useState('')
  const [editInStatus, setEditInStatus] = useState('출근')
  const [editOutStatus, setEditOutStatus] = useState('퇴근')
  const [editMemo, setEditMemo] = useState('')

  const isHr = String(user?.depId ?? user?.department?.depId ?? '') === '1'
  const isAdmin = isHr || (user?.roleId === 5) || (user?.role?.roleId === 5)
  const isSelf = record && (String(record.empNo ?? record.depno ?? record.id) === String(user?.empNo ?? user?.depno ?? user?.id))
  const attendanceVisible = isAdmin || isSelf
  const initials = (record?.name || record?.email || 'U').trim().slice(0, 2).toUpperCase()
  const deptColor = getDeptColor(record?.dept)
  const avatarColor = deptColor.text
  const avatar = record?.avatar || ''

  useEffect(() => {
    let mounted = true

    const load = async () => {
      setLoading(true)
      setError('')
      try {
        await Emp.ensureLoaded()
        if (!mounted) return
        setRecord(Emp.getById(id))
      } catch (err) {
        if (!mounted) return
        setError(err?.message || '직원 정보를 불러오지 못했습니다.')
      } finally {
        if (mounted) setLoading(false)
      }
    }

    load()
    const unsub = Emp.subscribe(() => setRecord(Emp.getById(id)))
    return () => { mounted = false; unsub && unsub() }
  }, [id])

  const notFound = !record && !loading

  return (
    <section className="page">
      <div className="page-title-row" style={{ display: 'flex', alignItems: 'center', gap: 12, flexWrap: 'wrap' }}>
        <h1 className="page-title">직원 정보</h1>
      </div>

      {loading && <p>불러오는 중...</p>}
      {error && <div className="card" style={{ color: 'red', marginBottom: 12 }}>{error}</div>}

      {notFound ? (
        <>
          <p>직원 정보를 찾을 수 없습니다.</p>
          <button className="btn" onClick={() => nav('/directory')}>목록</button>
        </>
      ) : record && (
        <>
          <article className="card border-blue" style={{ maxWidth: 840 }}>
            <div className="grid-2">
              <div className="form">
                <div className="form-row"><span className="form-label">이름</span><input className="input" value={record.name} disabled /></div>
                <div className="form-row"><span className="form-label">이메일</span><input className="input" value={record.email} disabled /></div>
                <div className="form-row"><span className="form-label">부서</span><input className="input" value={record.dept || ''} disabled /></div>
                <div className="form-row"><span className="form-label">직급</span><input className="input" value={formatRole(record.title)} disabled /></div>
                <div className="form-row"><span className="form-label">연락처</span><input className="input" value={formatPhone(record.phone) || ''} disabled /></div>
              </div>
              <div style={{ display: 'grid', placeItems: 'center' }}>
                <div style={{ width: 140, height: 140, borderRadius: 16, display: 'grid', placeItems: 'center', overflow: 'hidden', color: avatarColor }}>
                  <AvatarPreview avatar={avatar} initials={initials} color={avatarColor} />
                </div>
              </div>
            </div>
          </article>

          {attendanceVisible && (
            <div style={{ marginTop: 16 }}>
              <AttendanceCard adminView={isAdmin} empNo={record.empNo} refresh={attRefresh} />
            </div>
          )}

          <div className="toolbar" style={{ marginTop: 16, justifyContent: 'flex-start', flexWrap: 'wrap', gap: 8 }}>
            <Link
              to="/directory"
              className="btn gray"
            >
              목록
            </Link>
            {isHr && (
              <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
                <Link
                  to={`/directory/${record.empNo || record.id}/edit`}
                  className="btn sm blue"
                >
                  수정
                </Link>
                <button
                  className="btn sm red"
                  type="button"
                  onClick={() => setShowTerminate(true)}
                >
                  퇴사
                </button>
                <button
                  className="btn sm green"
                  type="button"
                  onClick={() => setShowMsg(true)}
                >
                  근태 사유/수정
                </button>
              </div>
            )}
          </div>
        </>
      )}

      {showMsg && record && (
        <div className="modal-backdrop" style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.4)', display: 'grid', placeItems: 'center', zIndex: 999 }}>
          <div className="card border-gray" style={{ width: 'min(560px, 92vw)' }}>
            <h3 style={{ marginTop: 0 }}>기존 근태 수정 (HR/관리자)</h3>
            <p style={{ marginTop: 4, color: 'var(--fg-muted)' }}>사번 {record.empNo} {record.name ? `· ${record.name}` : ''}</p>
            <div style={{ borderTop: '1px solid var(--line)', margin: '12px 0 0', paddingTop: 12 }}>
              <div style={{ display: 'grid', gap: 10, gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))' }}>
                <label className="auth-help" style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
                  기준 날짜
                  <input type="date" className="input" value={editDate} onChange={e => setEditDate(e.target.value)} />
                </label>
                <label className="auth-help" style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
                  출근 시간
                  <input type="time" className="input" value={editInTime} onChange={e => setEditInTime(e.target.value)} />
                </label>
                <label className="auth-help" style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
                  퇴근 시간
                  <input type="time" className="input" value={editOutTime} onChange={e => setEditOutTime(e.target.value)} />
                </label>
                <label className="auth-help" style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
                  출근 상태
                  <input type="text" className="input" value={editInStatus} onChange={e => setEditInStatus(e.target.value)} />
                </label>
                <label className="auth-help" style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
                  퇴근 상태
                  <input type="text" className="input" value={editOutStatus} onChange={e => setEditOutStatus(e.target.value)} />
                </label>
              </div>
              <label className="auth-help" style={{ display: 'flex', flexDirection: 'column', gap: 4, marginTop: 10 }}>
                메모
                <textarea className="input" style={{ minHeight: 80 }} value={editMemo} onChange={e => setEditMemo(e.target.value)} placeholder="메모(선택)" />
              </label>
              <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end', marginTop: 10, flexWrap: 'wrap' }}>
                <button
                  className="btn"
                  type="button"
                  onClick={() => setShowMsg(false)}
                >
                  취소
                </button>
                <button
                  className="btn primary"
                  type="button"
                  onClick={async () => {
                    try {
                      const payload = buildAttendanceUpdate(record.empNo, editDate, editInTime, editOutTime, editInStatus, editOutStatus, editMemo)
                      await Attendance.modify(payload)
                      alert('근태 기록이 수정되었습니다.')
                      setAttRefresh(r => r + 1)
                    } catch (err) {
                      alert(err?.message || '근태 수정에 실패했습니다.')
                    } finally {
                      setShowMsg(false)
                    }
                  }}
                >
                  근태 수정
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {showTerminate && record && (
        <div className="modal-backdrop" style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.4)', display: 'grid', placeItems: 'center', zIndex: 999 }}>
          <div className="card border-red" style={{ width: 'min(520px, 90vw)' }}>
            <h3 style={{ marginTop: 0 }}>퇴사 처리</h3>
            <p style={{ marginTop: 4, color: 'var(--fg-muted)' }}>사번 {record.empNo} {record.name ? `· ${record.name}` : ''}</p>
            <textarea
              className="input"
              style={{ minHeight: 120, marginTop: 8 }}
              placeholder="퇴사 사유를 입력하세요"
              value={terminateReason}
              onChange={e => setTerminateReason(e.target.value)}
            />
            <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end', marginTop: 12 }}>
              <button className="btn" type="button" onClick={() => { setShowTerminate(false); setTerminateReason('') }}>취소</button>
              <button
                className="btn danger"
                type="button"
                onClick={async () => {
                  try {
                    await Emp.terminate(record.empNo, terminateReason || '')
                    alert('퇴사 처리되었습니다 (목록에서 제외)')
                    nav('/directory')
                  } catch (err) {
                    alert(err?.message || '퇴사 처리에 실패했습니다.')
                  } finally {
                    setShowTerminate(false)
                    setTerminateReason('')
                  }
                }}
              >
                처리
              </button>
            </div>
          </div>
        </div>
      )}
    </section>
  )
}

function formatPhone(phone) {
  if (!phone) return ''
  const digits = String(phone).replace(/\D/g, '')
  if (digits.length === 11) return digits.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3')
  if (digits.length === 10) return digits.replace(/(\d{3})(\d{3})(\d{4})/, '$1-$2-$3')
  return digits
}

function formatRole(title = '') {
  if (!title) return '-'
  const t = title.toUpperCase()
  if (['GM', 'MANAGER', 'BUJANG', 'BUJANG.', 'GM/BUJANG'].includes(t)) return '부장'
  if (['MGR', 'MGR.', 'MANAGER2', 'GWAZANG', 'GWAJANG'].includes(t)) return '과장'
  if (['AC', 'ASSISTANT', 'ASSISTANT_MANAGER', 'DAERI'].includes(t)) return '대리'
  if (['AS', 'JUIB', 'JOOIM', 'JOO-IM'].includes(t)) return '주임'
  if (['ST', 'STAFF', 'SAWON', 'EMPLOYEE'].includes(t)) return '사원'
  return title
}

function AvatarPreview({ avatar, initials, color }) {
  if (avatar) {
    return <img src={avatar} alt="avatar" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
  }
  return (
    <svg
      aria-hidden="true"
      width="100%"
      height="100%"
      viewBox="0 0 64 64"
      fill="none"
      stroke="none"
      preserveAspectRatio="xMidYMid meet"
    >
      <circle cx="32" cy="20" r="12" fill={color} opacity="0.9" />
      <path d="M16 54c0-8.84 7.16-16 16-16s16 7.16 16 16" fill={color} opacity="0.85" />
    </svg>
  )
}

function buildAttendanceUpdate(empNo, dateStr, inTime, outTime, inStatus, outStatus, memo) {
  if (!empNo) throw new Error('empNo가 없습니다.')
  if (!dateStr) throw new Error('날짜를 선택하세요.')

  // 서버가 Date(자정 포함)로 비교하므로 타임존 없이 로컬 자정으로 고정
  const workdate = `${dateStr}T00:00:00`
  const clockIn = combineLocalDateTime(dateStr, inTime)
  const clockOut = combineLocalDateTime(dateStr, outTime)

  return {
    empNo,
    workdate,
    ...(clockIn ? { clockIn } : {}),
    ...(clockOut ? { clockOut } : {}),
    ...(inStatus ? { inStatus } : {}),
    ...(outStatus ? { outStatus } : {}),
    ...(memo ? { memo } : {}),
  }
}

function combineLocalDateTime(dateStr, timeStr) {
  if (!dateStr || !timeStr) return null
  // time input은 HH:mm 형태이므로 초를 붙여서 서버로 그대로 전달(타임존 미표기)
  const hhmmss = timeStr.length === 5 ? `${timeStr}:00` : timeStr
  return `${dateStr}T${hhmmss}`
}
