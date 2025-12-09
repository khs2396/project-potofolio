import { useEffect, useRef, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import * as Ann from '../services/announcements.js'
import '../styles/detail-pages.css'

export default function AnnouncementDetail() {
  const { id } = useParams()
  const nav = useNavigate()
  const [item, setItem] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const hitSentRef = useRef(false)

  useEffect(() => {
    let mounted = true
    const load = async () => {
      setLoading(true)
      setError('')
      try {
        const existing = Ann.getById(id)
        const data = existing || await Ann.fetchById(id)
        if (mounted) setItem(data)
        // 조회수 증가 한 번만
        if (!hitSentRef.current) {
          hitSentRef.current = true
          try { await Ann.bumpHit(id) } catch { /* ignore */ }
        }
      } catch (err) {
        if (mounted) setError(err?.message || '공지사항을 불러오지 못했습니다.')
      } finally {
        if (mounted) setLoading(false)
      }
    }
    load()
    const unsub = Ann.subscribe((items) => {
      if (!mounted) return
      const found = (items || []).find(a => String(a.id) === String(id))
      if (found) setItem(found)
    })
    return () => { mounted = false; unsub && unsub() }
  }, [id])

  if (loading) {
    return (
      <section className="page">
        <h1 className="page-title">공지사항</h1>
        <p>불러오는 중...</p>
      </section>
    )
  }

  if (error) {
    return (
      <section className="page">
        <h1 className="page-title">공지사항</h1>
        <p style={{ color: 'red' }}>{error}</p>
        <button className="btn" onClick={() => nav('/announcements')}>목록</button>
      </section>
    )
  }

  if (!item) {
    return (
      <section className="page">
        <h1 className="page-title">공지사항</h1>
        <p>공지사항을 찾을 수 없습니다.</p>
        <button className="btn" onClick={() => nav('/announcements')}>목록</button>
      </section>
    )
  }

  const bodyText = item.body?.trim() ? item.body : '내용이 없습니다.'
  const info = [
    { label: '부서', value: item.dept || '전체' },
    { label: '작성자', value: item.author || '관리자' },
    { label: '작성일', value: item.date || '-' },
    { label: '조회', value: item.views ?? 0 }
  ]

  return (
    <section className="page announcement-detail-page">
      <div className="detail-head">
        <span className="detail-chip">공지</span>
        <h1 className="page-title">{item.title}</h1>
        <div className="detail-meta">
          <span>{item.dept || '전체'}</span>
          <span>작성자 {item.author || '관리자'}</span>
          <span>작성일 {item.date || '-'}</span>
          <span>조회 {item.views ?? 0}</span>
        </div>
      </div>

      <div className="detail-grid">
        <article className="card">
          <h2 className="detail-section-title">내용</h2>
          <div className="detail-body" style={{ whiteSpace: 'pre-wrap' }}>{bodyText}</div>
        </article>

        <div className="card">
          <h3 className="detail-section-title">글 정보</h3>
          <div className="detail-meta-list">
            {info.map(field => (
              <div key={field.label} className="detail-meta-item">
                <span className="label">{field.label}</span>
                <span className="value">{field.value}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="detail-actions" style={{ justifyContent: 'flex-end', marginTop: 16 }}>
        <button className="btn" type="button" onClick={() => nav(-1)}>뒤로</button>
        <Link className="btn" to="/announcements">목록</Link>
      </div>
    </section>
  )
}
