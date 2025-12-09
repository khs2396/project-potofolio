import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import * as Boards from '../services/boards.api.js'
import '../styles/boards.css'

export default function BoardWrite() {
  const { id } = useParams()
  const [form, setForm] = useState({ subject: '', content: '' })
  const [loading, setLoading] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const nav = useNavigate()
  const isEdit = Boolean(id)

  useEffect(() => {
    if (!isEdit) return
    setLoading(true)
    Boards.getBoard(id)
      .then(res => {
        if (res?.item) {
          setForm({ subject: res.item.subject || '', content: res.item.content || '' })
        }
      })
      .finally(() => setLoading(false))
  }, [id, isEdit])

  const onChange = key => e => setForm(prev => ({ ...prev, [key]: e.target.value }))

  const onSubmit = async e => {
    e.preventDefault()
    if (!form.subject.trim() || !form.content.trim()) {
      alert('제목과 내용을 입력해 주세요')
      return
    }
    try {
      setSubmitting(true)
      if (isEdit) {
        const res = await Boards.updateBoard(id, { subject: form.subject, content: form.content })
        if (res?.rt !== 'modifyOK') {
          alert(res?.msg || '수정 권한이 없거나 실패했습니다.')
          return
        }
        alert('게시글이 수정되었습니다.')
        nav(`/boards/${id}`)
      } else {
        const res = await Boards.createBoard({ subject: form.subject, content: form.content })
        if (res?.rt !== 'writeOK') {
          alert(res?.msg || '로그인 후 작성해 주세요.')
          return
        }
        const boardId = res?.item?.boardseq || res?.item?.id
        alert('게시글이 추가되었습니다.')
        if (boardId) {
          nav(`/boards/${boardId}`)
        } else {
          nav('/boards')
        }
      }
    } catch (err) {
      alert(err?.message || '요청 중 오류가 발생했습니다.')
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return <section className="page"><p>불러오는 중...</p></section>
  }

  return (
    <section className="page">
      <h1 className="page-title">{isEdit ? '게시글 수정' : '게시글 작성'}</h1>
      <form className="form board-editor" onSubmit={onSubmit}>
        <label className="form-row">
          <span className="form-label">제목</span>
          <input className="input" value={form.subject} onChange={onChange('subject')} placeholder="제목" required />
        </label>
        <label className="form-row">
          <span className="form-label">내용</span>
          <textarea className="input" rows="12" value={form.content} onChange={onChange('content')} placeholder="내용을 입력하세요" required />
        </label>
        <div className="form-row" style={{ display: 'flex', gap: 8 }}>
          <button type="submit" className="btn primary" disabled={submitting}>
            {submitting ? '처리 중...' : '저장'}
          </button>
          <button type="button" className="btn" onClick={() => nav(-1)} disabled={submitting}>취소</button>
        </div>
      </form>
    </section>
  )
}
