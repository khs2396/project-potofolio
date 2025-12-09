import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import * as Ann from '../services/announcements.js'

export default function AnnouncementWrite() {
  const nav = useNavigate()
  const [form, setForm] = useState({ title: '', body: '' })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const onSubmit = async (e) => {
    e.preventDefault()
    if (!form.title || !form.body) { setError('제목/내용을 입력하세요.'); return }
    setError('')
    setLoading(true)
    try {
      // 부서는 자동으로 "인사부"로 설정
      const created = await Ann.create({ title: form.title, body: form.body, dept: '인사부' })
      alert('등록되었습니다.')
      nav(`/announcements/${created?.id ?? ''}`)
    } catch (err) {
      setError(err?.message || '등록에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="page">
      <h1 className="page-title">공지 등록</h1>
      {error && <div className="card" style={{ color: 'red', marginBottom: 12 }}>{error}</div>}
      <form className="form" style={{ maxWidth: 720 }} onSubmit={onSubmit}>
        <label className="form-row">
          <span className="form-label">제목</span>
          <input className="input" name="title" value={form.title} onChange={onChange} placeholder="제목" required />
        </label>
        <label className="form-row">
          <span className="form-label">내용</span>
          <textarea className="input" name="body" rows="10" value={form.body} onChange={onChange} placeholder="내용" />
        </label>
        <div className="form-row" style={{ display: 'flex', gap: 8 }}>
          <button className="btn" type="button" onClick={() => nav('/announcements')}>취소</button>
          <button className="btn primary" type="submit" disabled={loading}>{loading ? '등록 중...' : '등록'}</button>
        </div>
      </form>
    </section>
  )
}
