import { useEffect, useMemo, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import Table from '../components/Table.jsx'
import SearchBar from '../components/SearchBar.jsx'
import * as Employees from '../services/employees.js'
import * as Attendance from '../services/attendance.api.js'
import { useAuth } from '../context/AuthContext.jsx'

export default function Directory() {
  const { user } = useAuth()
  const isAdmin = (user?.roleId === 5) || (user?.depId === 1) || (user?.role?.roleId === 5) || (user?.department?.depId === 1)

  const [params, setParams] = useSearchParams()
  const qParam = params.get('q') || ''
  const deptParam = params.get('dept') || 'ALL'
  const [q, setQ] = useState(qParam)
  const [dept, setDept] = useState(deptParam)
  const [base, setBase] = useState(() => Employees.list())
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [attMap, setAttMap] = useState({})

  useEffect(() => {
    let mounted = true
    setLoading(true)
    Employees.ensureLoaded()
      .then(items => { if (mounted) setBase(items) })
      .catch(err => { if (mounted) setError(err?.message || '직원 데이터를 불러오지 못했습니다. 로그인 상태를 확인해주세요.') })
      .finally(() => mounted && setLoading(false))
    const unsub = Employees.subscribe(items => setBase(items || []))
    return () => { mounted = false; unsub && unsub() }
  }, [])

  useEffect(() => {
    setQ(qParam)
    setDept(deptParam)
  }, [qParam, deptParam])

  useEffect(() => {
    if (!isAdmin) return
    let mounted = true
    const loadAttendance = async () => {
      try {
        const res = await Attendance.listAll(1, 500)
        const items = Array.isArray(res?.items) ? res.items : []
        const map = {}
        items.forEach(it => {
          const key = it.empNo
          const current = map[key]
          if (!current || new Date(it.workdate) > new Date(current.workdate)) {
            map[key] = it
          }
        })
        if (mounted) setAttMap(map)
      } catch {
        /* ignore */
      }
    }
    loadAttendance()
    return () => { mounted = false }
  }, [isAdmin])

  const deptOptions = useMemo(() => {
    const set = new Set(base.map(e => e.dept).filter(Boolean))
    return ['ALL', ...Array.from(set)]
  }, [base])

  const filtered = useMemo(() => {
    const term = (q || '').toLowerCase()
    return base.filter(e =>
      (dept === 'ALL' || e.dept === dept) &&
      (
        (e.name || '').toLowerCase().includes(term) ||
        (e.email || '').toLowerCase().includes(term) ||
        (e.title || '').toLowerCase().includes(term)
      )
    )
  }, [base, q, dept])

  const formatRole = (title = '') => {
    if (!title) return '-'
    if (/[가-힣]/.test(title)) return title
    const t = title.toUpperCase()
    if (['GM', 'MANAGER', 'BUJANG', 'BUJANG.', 'GM/BUJANG'].includes(t)) return '부장'
    if (['MGR', 'MGR.', 'MANAGER2', 'GWAZANG', 'GWAJANG'].includes(t)) return '과장'
    if (['AC', 'ASSISTANT', 'ASSISTANT_MANAGER', 'DAERI'].includes(t)) return '대리'
    if (['AS', 'JUIB', 'JOOIM', 'JOO-IM'].includes(t)) return '주임'
    if (['ST', 'STAFF', 'SAWON', 'EMPLOYEE'].includes(t)) return '사원'
    return title
  }

  const onSearch = (text) => { setQ(text); setParams({ q: text, dept }) }
  const onDept = (val) => { setDept(val); setParams({ q, dept: val }) }

  return (
    <section className="page">
      <h1 className="page-title">직원 디렉터리</h1>
      {error && <div className="card" style={{ marginBottom: 12, color: 'red' }}>{error}</div>}

      <div className="toolbar">
        <SearchBar onSearch={onSearch} defaultValue={q} />
        <select className="input sm" value={dept} onChange={e => onDept(e.target.value)} style={{ maxWidth: 180 }}>
          {deptOptions.map(opt => <option key={opt} value={opt}>{opt === 'ALL' ? '전체 부서' : opt}</option>)}
        </select>
      </div>

      <Table className="modern" wrapClassName="modern" cols={[200, 240, 260, 200, 160, 120]}>
        <thead>
          <tr>
            <th className="col-emp-name" style={{ textAlign: 'center' }}>이름</th>
            <th className="col-emp-dept" style={{ textAlign: 'center' }}>부서/직급</th>
            <th className="col-emp-email" style={{ textAlign: 'center' }}>이메일</th>
            <th className="col-emp-phone" style={{ textAlign: 'center' }}>연락처</th>
            <th className="col-emp-att" style={{ textAlign: 'center' }}>근태</th>
            <th style={{ textAlign: 'center' }}>상세</th>
          </tr>
        </thead>
        <tbody>
          {loading
            ? <tr><td colSpan="6" style={{ textAlign: 'center' }}>불러오는 중...</td></tr>
            : filtered.length === 0
              ? <tr><td colSpan="6" style={{ textAlign: 'center' }}>직원이 없습니다.</td></tr>
              : filtered.map((e, idx) => {
                const key = e.empNo || e.id || e.email || `row-${idx}`
                const att = attMap[e.empNo]
                const attText = att ? `${att.inStatus || ''}${att.outStatus ? ` / ${att.outStatus}` : ''}` : (isAdmin ? '-' : '권한없음')
                return (
                  <tr key={key}>
                    <td className="col-emp-name" style={{ textAlign: 'center' }}>
                      <span className="cell-ellipsis">{e.name}</span>
                    </td>
                    <td className="col-emp-dept" style={{ textAlign: 'center' }}>{e.dept} / {formatRole(e.title)}</td>
                    <td className="col-emp-email" style={{ textAlign: 'center' }}><span className="cell-ellipsis">{e.email}</span></td>
                    <td className="col-emp-phone" style={{ textAlign: 'center' }}>{e.phone || '-'}</td>
                    <td className="col-emp-att" style={{ textAlign: 'center' }}>{attText}</td>
                    <td style={{ textAlign: 'center' }}><Link className="btn sm primary" to={`/directory/${e.empNo || e.id}`}>상세</Link></td>
                  </tr>
                )
              })
          }
        </tbody>
      </Table>
    </section>
  )
}
