import { useEffect, useMemo, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import Table from '../components/Table.jsx'
import SearchBar from '../components/SearchBar.jsx'
import * as Ann from '../services/announcements.js'

export default function Announcements() {
  const { user } = useAuth()
  const [params, setParams] = useSearchParams()
  const qParam = params.get('q') || ''
  const pgParam = Number(params.get('pg') || '1')
  const PAGE_SIZE = 10
  const [q, setQ] = useState(qParam)
  const [pg, setPg] = useState(pgParam)
  const [data, setData] = useState(() => Ann.list())
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  // 인사부인지 확인
  const userDept = (user?.dept || user?.department?.depName || '').toUpperCase()
  const isHR = userDept === '인사' || userDept === 'HR' || userDept === '인사부' || userDept.includes('인사')

  useEffect(() => {
    let mounted = true

    const load = async () => {
      setLoading(true)
      setError('')
      try {
        await Ann.ensureLoaded()
        if (mounted) setData(Ann.list())
      } catch (err) {
        if (mounted) setError(err?.message || '공지 목록을 불러오지 못했습니다.')
      } finally {
        if (mounted) setLoading(false)
      }
    }

    load()
    const unsub = Ann.subscribe((items) => mounted && setData(items || []))
    return () => { mounted = false; unsub && unsub() }
  }, [])

  useEffect(() => {
    setQ(qParam)
    setPg(pgParam || 1)
  }, [qParam, pgParam])

  const filtered = useMemo(() => {
    const term = q.toLowerCase()
    return data.filter(a =>
      (a.title || '').toLowerCase().includes(term) ||
      (a.author || '').toLowerCase().includes(term)
    )
  }, [data, q])

  useEffect(() => {
    const totalP = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE))
    if (pg > totalP) setPg(totalP)
  }, [filtered.length, pg])

  const paged = useMemo(() => {
    const start = (pg - 1) * PAGE_SIZE
    return filtered.slice(start, start + PAGE_SIZE)
  }, [filtered, pg])

  const onSearch = (text) => { setQ(text); setPg(1); setParams({ q: text, pg: '1' }) }
  const onPage = (next) => {
    const totalP = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE))
    const clamped = Math.min(Math.max(next, 1), totalP)
    setPg(clamped)
    setParams({ q, pg: String(clamped) })
  }

  const mapDept = (dep = '') => {
    if (!dep) return '-'
    const d = dep.trim()
    const upper = d.toUpperCase()
    let name = d
    if (upper === 'HR') name = '인사부'
    else if (upper === 'GA') name = '총무부'
    else if (upper === 'DEV') name = '개발부'
    else if (upper === 'FID') name = '재무부'
    else if (upper === 'SD') name = '영업부'
    if (!/부$/.test(name) && /[가-힣]/.test(name)) name = `${name}부`
    return name
  }

  return (
    <section className="page">
      <div style={{ marginBottom: '24px' }}>
        <h1 className="page-title" style={{ marginBottom: '8px' }}>공지사항</h1>
        <p style={{ color: 'var(--fg-muted)', fontSize: '14px' }}>중요한 공지사항을 확인하세요</p>
      </div>

      <div className="toolbar" style={{ marginBottom: '16px' }}>
        <SearchBar onSearch={onSearch} defaultValue={q} placeholder="제목·작성자 검색" />
        {isHR && <Link to="/announcements/write" className="btn sm primary">글 작성</Link>}
      </div>

      {error && <div className="card" style={{ marginBottom: 16, padding: '12px 16px', color: '#d32f2f', backgroundColor: '#ffebee', borderRadius: '8px' }}>{error}</div>}

      <div className="card" style={{ padding: 0, overflow: 'hidden', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' }}>
        <Table
          className="modern"
          wrapClassName="modern"
          cols={[80, null, 140, 140, 140, 100, 120]}
        >
          <thead>
            <tr style={{ backgroundColor: 'var(--bg-secondary, #f8f9fa)' }}>
              <th style={{ textAlign: 'center', padding: '12px 8px', fontWeight: 600 }}>번호</th>
              <th style={{ textAlign: 'left', padding: '12px 16px', fontWeight: 600 }}>제목</th>
              <th style={{ textAlign: 'center', padding: '12px 8px', fontWeight: 600 }}>부서</th>
              <th style={{ textAlign: 'center', padding: '12px 8px', fontWeight: 600 }}>작성자</th>
              <th style={{ textAlign: 'center', padding: '12px 8px', fontWeight: 600 }}>등록일</th>
              <th style={{ textAlign: 'center', padding: '12px 8px', fontWeight: 600 }}>조회</th>
              <th style={{ textAlign: 'center', padding: '12px 8px', fontWeight: 600 }}>상세</th>
            </tr>
          </thead>
          <tbody>
            {loading
              ? <tr><td colSpan="7" style={{ textAlign: 'center', padding: '48px 16px', color: 'var(--fg-muted)' }}>불러오는 중...</td></tr>
              : paged.length === 0
                ? <tr><td colSpan="7" style={{ textAlign: 'center', padding: '48px 16px', color: 'var(--fg-muted)' }}>결과가 없습니다.</td></tr>
                : paged.map(a => (
                  <tr key={a.id} style={{ borderBottom: '1px solid var(--line, #e0e0e0)', transition: 'background-color 0.2s' }}>
                    <td style={{ textAlign: 'center', padding: '14px 8px', color: 'var(--fg-muted)' }}>{a.id}</td>
                    <td style={{ padding: '14px 16px' }}>
                      <span style={{ fontWeight: 500, fontSize: '15px' }}>{a.title}</span>
                    </td>
                    <td style={{ textAlign: 'center', padding: '14px 8px' }}>
                      <span style={{
                        display: 'inline-block',
                        padding: '4px 12px',
                        backgroundColor: '#e3f2fd',
                        color: '#1976d2',
                        borderRadius: '12px',
                        fontSize: '13px',
                        fontWeight: 500
                      }}>
                        {mapDept(a.dept)}
                      </span>
                    </td>
                    <td style={{ textAlign: 'center', padding: '14px 8px' }}>{a.author || '관리자'}</td>
                    <td style={{ textAlign: 'center', padding: '14px 8px', color: 'var(--fg-muted)', fontSize: '14px' }}>{a.date || '-'}</td>
                    <td style={{ textAlign: 'center', padding: '14px 8px', color: 'var(--fg-muted)', fontSize: '14px' }}>{a.views ?? '-'}</td>
                    <td style={{ textAlign: 'center', padding: '14px 8px' }}>
                      <Link className="btn sm primary" to={`/announcements/${a.id}`}>상세</Link>
                    </td>
                  </tr>
                ))
            }
          </tbody>
        </Table>
      </div>

      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', marginTop: 24, gap: 16 }}>
        <button
          className="btn sm"
          type="button"
          disabled={pg <= 1}
          onClick={() => onPage(pg - 1)}
          style={{ minWidth: '80px' }}
        >
          이전
        </button>
        <span style={{ fontSize: '15px', fontWeight: 500, color: 'var(--fg)' }}>
          {pg} / {Math.max(1, Math.ceil(filtered.length / PAGE_SIZE))}
        </span>
        <button
          className="btn sm"
          type="button"
          disabled={pg >= Math.max(1, Math.ceil(filtered.length / PAGE_SIZE))}
          onClick={() => onPage(pg + 1)}
          style={{ minWidth: '80px' }}
        >
          다음
        </button>
      </div>
    </section>
  )
}
