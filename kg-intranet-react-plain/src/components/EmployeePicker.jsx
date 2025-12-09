import { useEffect, useMemo, useState } from 'react'
import * as Employees from '../services/employees.js'

export default function EmployeePicker({ value, onChange, label = '담당자' }) {
  const [q, setQ] = useState('')
  const [employees, setEmployees] = useState(Employees.list())
  const [showDropdown, setShowDropdown] = useState(false)

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

  const candidates = useMemo(() => {
    const term = q.toLowerCase()
    return employees.filter(e =>
      e.name.toLowerCase().includes(term) ||
      e.email.toLowerCase().includes(term) ||
      e.dept.includes(q)
    ).slice(0, 10)
  }, [q, employees])

  const select = (e) => {
    onChange?.(e)
    setQ('')
    setShowDropdown(false)
  }

  const displayText = value ? `${value.name} (${value.dept})` : ''

  return (
    <div style={{ position: 'relative' }}>
      <input
        className="input"
        placeholder={`${label} 검색: 이름/이메일/부서`}
        value={showDropdown ? q : displayText}
        onChange={e => {
          setQ(e.target.value)
          setShowDropdown(true)
        }}
        onFocus={() => setShowDropdown(true)}
      />
      {showDropdown && (
        <>
          <div
            style={{
              position: 'fixed',
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              zIndex: 10
            }}
            onClick={() => setShowDropdown(false)}
          />
          <div
            className="card"
            style={{
              position: 'absolute',
              top: '100%',
              left: 0,
              right: 0,
              marginTop: 4,
              padding: 8,
              maxHeight: 240,
              overflow: 'auto',
              zIndex: 20,
              boxShadow: '0 4px 12px rgba(0,0,0,0.15)'
            }}
          >
            <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
              {candidates.map(e => (
                <li
                  key={e.id}
                  style={{
                    padding: '8px 12px',
                    cursor: 'pointer',
                    borderRadius: 4,
                    marginBottom: 2
                  }}
                  className="hover-bg"
                  onClick={() => select(e)}
                >
                  <strong>{e.name}</strong>
                  <span style={{ color: 'var(--fg-muted)', marginLeft: 8 }}>
                    {e.dept} · {e.email}
                  </span>
                </li>
              ))}
              {candidates.length === 0 && (
                <li style={{ color: 'var(--fg-muted)', padding: 8 }}>
                  검색 결과가 없습니다.
                </li>
              )}
            </ul>
          </div>
        </>
      )}
      {value && !showDropdown && (
        <button
          className="btn sm"
          style={{ position: 'absolute', right: 8, top: '50%', transform: 'translateY(-50%)' }}
          onClick={() => onChange?.(null)}
          type="button"
        >
          ✕
        </button>
      )}
    </div>
  )
}
