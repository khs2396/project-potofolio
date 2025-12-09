import { useEffect, useRef, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import * as Boards from '../services/boards.api.js'
import '../styles/boards.css'

export default function BoardDetail() {
  const roleToKorean = (roleName, roleId) => {
    const byId = { 1: '사원', 2: '주임', 3: '대리', 4: '과장', 5: '부장' }
    const byName = { EMP: '사원', JR: '주임', AM: '대리', MGR: '과장', GM: '부장' }
    return byId[roleId] || byName[roleName] || roleName || ''
  }

  const depToKorean = (depName) => {
    if (!depName) return ''
    const map = {
      HR: '인사부',
      HUMANRESOURCES: '인사부',
      GA: '총무부',
      GENERALADMIN: '총무부',
      IT: '전산',
      DEV: '개발부',
      DEVELOPMENT: '개발부',
      ENGINEERING: '엔지니어링',
      SALES: '영업부',
      SD: '영업부',
      MARKETING: '마케팅',
      FINANCE: '재무부',
      FID: '재무부',
      ACCOUNTING: '회계',
      ADMIN: '총무부',
      GENERAL: '총무부',
      SUPPORT: '고객지원',
      CS: '고객지원',
      RND: '연구개발',
      QA: '품질',
      OPERATIONS: '운영',
      OPERATION: '운영',
      BIZ: '사업',
    }
    const key = String(depName).replace(/\s+/g, '').toUpperCase()
    return map[key] || depName
  }

  const { id } = useParams()
  const { user } = useAuth()
  const [board, setBoard] = useState(null)
  const [loading, setLoading] = useState(true)
  const [comments, setComments] = useState([])
  const [commentPage, setCommentPage] = useState(1)
  const [commentInput, setCommentInput] = useState('')
  const [commentMeta, setCommentMeta] = useState({ totalP: 1 })
  const [sending, setSending] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [editingText, setEditingText] = useState('')
  const [savingEdit, setSavingEdit] = useState(false)
  const loadedRef = useRef(false)
  const lastIdRef = useRef(null)
  const nav = useNavigate()

  useEffect(() => {
    loadBoard()
  }, [id])

  useEffect(() => {
    loadComments(commentPage)
  }, [id, commentPage])

  const loadBoard = () => {
    if (loadedRef.current && lastIdRef.current === id) return
    loadedRef.current = true
    lastIdRef.current = id
    setLoading(true)
    Boards.getBoard(id)
      .then(res => {
        const item = res?.item || null
        if (item && item.hit == null) item.hit = 0
        setBoard(item)
      })
      .finally(() => setLoading(false))
  }

  const loadComments = (page) => {
    Boards.listComments(id, page)
      .then(res => {
        setComments(res?.items || [])
        setCommentMeta({ totalP: res?.totalP || 1, startPage: res?.startPage || 1, endPage: res?.endPage || 1 })
      })
      .catch(() => setComments([]))
  }

  const userId = user?.empNo ?? user?.id ?? user?.depno

  const onDelete = async () => {
    if (!window.confirm('게시글을 삭제하시겠습니까?')) return
    try {
      await Boards.deleteBoard(id)
      alert('삭제되었습니다.')
      nav('/boards')
    } catch (err) {
      alert(err?.message || '삭제 중 오류가 발생했습니다.')
    }
  }

  const onSendComment = async (e) => {
    e.preventDefault()
    if (!commentInput.trim()) return
    try {
      setSending(true)
      await Boards.addComment(id, commentInput.trim(), user)
      setCommentInput('')
      loadComments(1)
    } catch (err) {
      alert(err?.message || '댓글 등록 중 오류가 발생했습니다.')
    } finally {
      setSending(false)
    }
  }

  const onDeleteComment = async (commentseq) => {
    if (!window.confirm('댓글을 삭제하시겠습니까?')) return
    try {
      await Boards.deleteComment(id, commentseq, user)
      loadComments(commentPage)
    } catch (err) {
      alert(err?.message || '댓글 삭제 중 오류가 발생했습니다.')
    }
  }

  const onEditComment = (item) => {
    setEditingId(item.commentseq)
    setEditingText(item.content)
  }

  const onCancelEdit = () => {
    setEditingId(null)
    setEditingText('')
  }

  const onUpdateComment = async () => {
    if (!editingId || !editingText.trim()) return
    try {
      setSavingEdit(true)
      await Boards.updateComment(id, editingId, editingText.trim(), user)
      onCancelEdit()
      loadComments(commentPage)
    } catch (err) {
      alert(err?.message || '댓글 수정 중 오류가 발생했습니다.')
    } finally {
      setSavingEdit(false)
    }
  }

  if (loading) return <section className="page"><p>불러오는 중...</p></section>
  if (!board) return <section className="page"><p>게시글을 찾을 수 없습니다.</p></section>

  const canEdit = !!user && (Number(userId) === Number(board.depno) || user.roleId === 5)
  const depDisplay = depToKorean(board.depName) || `부서 ${board.depno}`

  return (
    <section className="page">
      <div className="board-card">
        <div className="board-meta">
          <span>
            작성자 {board.authorName}
            {board.roleName || board.roleId ? ` (${roleToKorean(board.roleName, board.roleId)})` : ''}
            · {depDisplay}
          </span>
          <span>작성일 {new Date(board.logtime).toLocaleString()}</span>
          <span>조회 {board.hit}</span>
        </div>
        <h1 className="page-title">{board.subject}</h1>
        <div className="detail-body" style={{ marginTop: 16 }}>{board.content}</div>

        <div className="detail-actions" style={{ marginTop: 16 }}>
          <Link className="btn sm" to="/boards">목록</Link>
          {canEdit && (
            <>
              <button className="btn sm" type="button" onClick={() => nav(`/boards/${board.boardseq}/edit`)}>수정</button>
              <button className="btn sm" type="button" onClick={onDelete}>삭제</button>
            </>
          )}
        </div>
      </div>

      <section className="board-card" style={{ marginTop: 20 }}>
        <div className="info-head" style={{ marginBottom: 12 }}>
          <h2>댓글</h2>
        </div>
        <ul className="comment-list">
          {comments.length === 0 ? (
            <li className="comment-item">등록된 댓글이 없습니다.</li>
          ) : (
            comments.map(item => (
              <li key={item.commentseq} className="comment-item">
                <div className="comment-meta">
                  <span>{item.authorName} · {new Date(item.logtime).toLocaleString()}</span>
                  {(!!user && (Number(userId) === Number(item.empNo ?? item.depno) || user.roleId === 5)) && (
                    <div className="btn-group" style={{ display: 'flex', gap: 8 }}>
                      <button type="button" className="btn sm" onClick={() => onEditComment(item)}>수정</button>
                      <button type="button" className="btn sm" onClick={() => onDeleteComment(item.commentseq)}>삭제</button>
                    </div>
                  )}
                </div>
                {editingId === item.commentseq ? (
                  <div className="comment-edit">
                    <textarea className="input" value={editingText} onChange={e => setEditingText(e.target.value)} />
                    <div style={{ display: 'flex', gap: 8, marginTop: 6 }}>
                      <button type="button" className="btn primary sm" disabled={savingEdit || !editingText.trim()} onClick={onUpdateComment}>
                        {savingEdit ? '저장 중...' : '저장'}
                      </button>
                      <button type="button" className="btn sm" disabled={savingEdit} onClick={onCancelEdit}>취소</button>
                    </div>
                  </div>
                ) : (
                  <p>{item.content}</p>
                )}
              </li>
            ))
          )}
        </ul>

        <div className="pagination" style={{ marginTop: 12 }}>
          <button type="button" className="btn sm" disabled={commentPage <= 1} onClick={() => setCommentPage(p => Math.max(1, p - 1))}>이전</button>
          <button type="button" className="btn sm" disabled={commentPage >= (commentMeta.totalP || 1)} onClick={() => setCommentPage(p => Math.min(commentMeta.totalP || 1, p + 1))}>다음</button>
        </div>

        <form className="comment-form" onSubmit={onSendComment}>
          <textarea className="input" value={commentInput} onChange={e => setCommentInput(e.target.value)} placeholder="댓글을 입력하세요" />
          <button type="submit" className="btn primary" disabled={sending}>{sending ? '등록 중...' : '댓글 등록'}</button>
        </form>
      </section>
    </section>
  )
}
