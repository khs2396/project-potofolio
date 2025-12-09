import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import * as Emp from '../services/employees.js'
import { useAuth } from '../context/AuthContext.jsx'

export default function DirectoryEdit() {
  const { id } = useParams()
  const nav = useNavigate()
  const { user } = useAuth()
  const [record, setRecord] = useState(() => Emp.getById(id))
  const [loading, setLoading] = useState(!record)
  const [error, setError] = useState('')
  const [formDept, setFormDept] = useState('')
  const [formRole, setFormRole] = useState('')
  const [formPhone, setFormPhone] = useState('')

  const isHr = String(user?.depId ?? user?.department?.depId ?? '') === '1'

  useEffect(() => {
    let mounted = true
    const load = async () => {
      setLoading(true)
      setError('')
      try {
        await Emp.ensureLoaded()
        if (!mounted) return
        const found = Emp.getById(id)
        setRecord(found)
        if (found) {
          setFormDept(found.dept || '')
          setFormRole(formatRole(found.title))
          setFormPhone(formatPhone(found.phone) || '')
        }
      } catch (err) {
        if (!mounted) return
        setError(err?.message || '직원 정보를 불러오지 못했습니다.')
      } finally {
        if (mounted) setLoading(false)
      }
    }
    load()
    const unsub = Emp.subscribe(() => {
      const found = Emp.getById(id)
      setRecord(found)
      if (found) {
        setFormDept(found.dept || '')
        setFormRole(formatRole(found.title))
        setFormPhone(formatPhone(found.phone) || '')
      }
    })
    return () => { mounted = false; unsub && unsub() }
  }, [id])

  const notFound = !record && !loading

  const handleSave = async () => {
    if (!record) return
    if (!isHr) {
      alert('인사부만 수정할 수 있습니다.')
      return
    }
    try {
      const cleanPhone = (formPhone || '').replace(/\D/g, '')
      const depIdMapped = mapDeptToId(formDept) ?? record.depId ?? record.department?.depId
      const roleIdMapped = mapRoleToId(formRole) ?? record.roleId ?? record.role?.roleId
      await Emp.update({
        empNo: record.empNo,
        phone: cleanPhone,
        tel: cleanPhone,
        depId: depIdMapped,
        roleId: roleIdMapped,
        name: record.name,
      })
      alert('저장되었습니다.')
      nav(`/directory/${record.empNo || record.id}`)
    } catch (err) {
      alert(err?.message || '저장 중 오류가 발생했습니다.')
    }
  }

  const handleCancel = () => {
    nav(`/directory/${record?.empNo || record?.id || ''}`)
  }

  return (
    <section className="page">
      <div className="page-title-row" style={{ display: 'flex', alignItems: 'center', gap: 12, flexWrap: 'wrap' }}>
        <h1 className="page-title">직원 정보 수정</h1>
      </div>

      {loading && <p>불러오는 중...</p>}
      {error && <div className="card" style={{ color: 'red', marginBottom: 12 }}>{error}</div>}

      {notFound ? (
        <>
          <p>직원 정보를 찾을 수 없습니다.</p>
          <button className="btn" onClick={() => nav('/directory')}>목록</button>
        </>
      ) : record && (
        <article className="card" style={{ maxWidth: 720 }}>
          <div className="form">
            <div className="form-row"><span className="form-label">이름</span><input className="input" value={record.name} disabled /></div>
            <div className="form-row"><span className="form-label">이메일</span><input className="input" value={record.email} disabled /></div>
            <div className="form-row">
              <span className="form-label">부서</span>
              <input
                className="input"
                value={formDept}
                onChange={e => setFormDept(e.target.value)}
              />
            </div>
            <div className="form-row">
              <span className="form-label">직급</span>
              <input
                className="input"
                value={formRole}
                onChange={e => setFormRole(e.target.value)}
              />
            </div>
            <div className="form-row">
              <span className="form-label">연락처</span>
              <input
                className="input"
                value={formPhone}
                onChange={e => setFormPhone(limitPhone(e.target.value))}
              />
            </div>
          </div>

          <div
            className="toolbar"
            style={{ marginTop: 16, justifyContent: 'flex-start', gap: 8, position: 'relative', zIndex: 1, flexWrap: 'wrap' }}
          >
            <button className="btn sm primary" type="button" onClick={handleSave}>저장</button>
            <button className="btn sm outline" type="button" onClick={handleCancel}>취소</button>
          </div>
        </article>
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

function limitPhone(val) {
  const digits = String(val || '').replace(/\D/g, '').slice(0, 11)
  return formatPhone(digits)
}

function formatRole(title = '') {
  if (!title) return ''
  const t = title.toUpperCase()
  if (['GM', 'MANAGER', 'BUJANG', 'BUJANG.', 'GM/BUJANG'].includes(t)) return '부장'
  if (['MGR', 'MGR.', 'MANAGER2', 'GWAZANG', 'GWAJANG'].includes(t)) return '과장'
  if (['AC', 'ASSISTANT', 'ASSISTANT_MANAGER', 'DAERI'].includes(t)) return '대리'
  if (['AS', 'JUIB', 'JOOIM', 'JOO-IM'].includes(t)) return '주임'
  if (['ST', 'STAFF', 'SAWON', 'EMPLOYEE'].includes(t)) return '사원'
  return title
}

function mapDeptToId(name) {
  if (!name) return null
  const key = name.trim().toLowerCase()
  const map = {
    'hr': 1, '인사부': 1, '인사': 1,
    '총무부': 2, '총무': 2, 'ga': 2,
    '개발부': 3, '개발': 3, 'dev': 3,
    '재무부': 4, '재무': 4, 'fid': 4,
    '영업부': 5, '영업': 5, 'sd': 5,
  }
  return map[key] ?? null
}

function mapRoleToId(name) {
  if (!name) return null
  const key = name.trim().toLowerCase()
  const map = {
    '사원': 1, 'st': 1, 'staff': 1,
    '주임': 2, 'as': 2,
    '대리': 3, 'ac': 3,
    '과장': 4, 'mgr': 4, 'manager': 4,
    '부장': 5, 'gm': 5,
  }
  return map[key] ?? null
}
