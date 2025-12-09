import { useEffect, useMemo, useState } from 'react'
import * as Employees from '../services/employees.js'

export default function ApproverPicker({ value = [], onChange, filterRole = null, maxCount = null }) {
  const [q, setQ] = useState('')
  const [employees, setEmployees] = useState(Employees.list())

  useEffect(() => {
    let mounted = true
    const load = async () => {
      try {
        const list = await Employees.ensureLoaded()
        if (mounted) setEmployees(list || [])
      } catch {
        if (mounted) setEmployees([])
      }
    }
    load()
    const unsub = Employees.subscribe((list) => { if (mounted) setEmployees(list || []) })
    return () => { mounted = false; unsub && unsub() }
  }, [])

  const selectedIds = new Set(value.map(v => v.id))
  const candidates = useMemo(() => {
    const term = q.toLowerCase()
    let filtered = employees.filter(e =>
      !selectedIds.has(e.id) &&
      (e.name.toLowerCase().includes(term) || e.email.toLowerCase().includes(term) || e.dept.includes(q))
    )

    // 부장만 필터링
    if (filterRole === 'manager') {
      filtered = filtered.filter(e => {
        if (e.roleId === 5) return true
        const title = (e.title || '').toUpperCase()
        const role = (e.role || '').toUpperCase()
        return title.includes('부장') ||
               title.includes('GM') ||
               title.includes('MANAGER') ||
               title.includes('BUJANG') ||
               role.includes('부장') ||
               role.includes('GM')
      })
    }

    return filtered
  }, [q, value, employees, filterRole])

  const add = (e) => {
    // 최대 인원 체크
    if (maxCount && value.length >= maxCount) {
      alert(`결재자는 ${maxCount}명 이상 선택할 수 없습니다.`)
      return
    }
    onChange?.([...value, { id: e.id, name: e.name, email: e.email, dept: e.dept, status: 'PENDING' }])
  }
  const remove = (id) => onChange?.(value.filter(v => v.id !== id))
  const move = (idx, dir) => {
    const next = value.slice()
    const j = idx + dir
    if (j < 0 || j >= next.length) return
    const [it] = next.splice(idx, 1)
    next.splice(j, 0, it)
    onChange?.(next)
  }

  return (
    <div className="card stack">
      <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
        <input className="input" placeholder="결재자 검색: 이름/이메일/부서" value={q} onChange={e => setQ(e.target.value)} />
      </div>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
        <div className="card" style={{ padding: 12 }}>
          <strong>목록</strong>
          {maxCount && value.length >= maxCount ? (
            <div style={{ padding: '20px 0', textAlign: 'center', color: 'var(--fg-muted)' }}>
              결재자는 {maxCount}명까지만 선택할 수 있습니다.
            </div>
          ) : (
            <ul style={{ listStyle: 'none', padding: 0, margin: '8px 0', maxHeight: 220, overflow: 'auto' }}>
              {candidates.map(e => (
                <li key={e.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '6px 0', borderBottom: '1px solid var(--line)' }}>
                  <span>{e.name} <span style={{ color: 'var(--fg-muted)' }}>({e.dept})</span></span>
                  <button type="button" className="btn" onClick={() => add(e)}>추가</button>
                </li>
              ))}
              {candidates.length === 0 && <li style={{ color: 'var(--fg-muted)' }}>검색 결과가 없습니다.</li>}
            </ul>
          )}
        </div>
        <div className="card" style={{ padding: 12 }}>
          <strong>결재선</strong>
          <ul style={{ listStyle: 'none', padding: 0, margin: '8px 0', maxHeight: 220, overflow: 'auto' }}>
            {value.map((a, i) => (
              <li key={a.id} style={{ display: 'grid', gridTemplateColumns: '1fr auto', alignItems: 'center', gap: 8, padding: '6px 0', borderBottom: '1px solid var(--line)' }}>
                <span>{i + 1}. {a.name} <span style={{ color: 'var(--fg-muted)' }}>({a.dept})</span></span>
                <button
                  type="button"
                  className="btn"
                  onClick={() => remove(a.id)}
                  style={{
                    backgroundColor: '#ffcdd2',
                    color: '#c62828',
                    border: '1px solid #ef9a9a'
                  }}
                >
                  제거
                </button>
              </li>
            ))}
            {value.length === 0 && <li style={{ color: 'var(--fg-muted)' }}>결재자를 추가해 주세요.</li>}
          </ul>
        </div>
      </div>
    </div>
  )
}
