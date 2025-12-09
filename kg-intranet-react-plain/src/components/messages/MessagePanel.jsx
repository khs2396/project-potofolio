import { useEffect, useMemo, useRef, useState } from 'react'
import { useMessages } from '../../context/MessagesContext.jsx'
import { useAuth } from '../../context/AuthContext.jsx'
import '../../styles/messages.css'
import { EMPLOYEES } from '../../data/employees.js'

const MIN_WIDTH = 280
const MIN_HEIGHT = 280

export default function MessagePanel() {
  const { panelState, closePanel, inbox, sent, showMessage, composeMessage, sendMessage, markRead } = useMessages()
  const { user } = useAuth()
  const [position, setPosition] = useState({ right: 24, bottom: 24 })
  const [size, setSize] = useState({ width: 360, height: 420 })
  const [view, setView] = useState('list')
  const [targetId, setTargetId] = useState(null)
  const [form, setForm] = useState({ to: '', subject: '', body: '' })
  const [sending, setSending] = useState(false)
  const dragRef = useRef(null)

  useEffect(() => {
    if (!panelState.open) return
    if (panelState.mode === 'detail' && panelState.focusId) {
      setView('detail')
      setTargetId(panelState.focusId)
    } else if (panelState.mode === 'compose') {
      setView('compose')
      setForm(f => ({ ...f, to: panelState.composeTo || f.to }))
    } else {
      setView('list')
    }
  }, [panelState])

  const currentMessage = useMemo(() => {
    return [...inbox, ...sent].find(m => String(m.id) === String(targetId)) || null
  }, [targetId, inbox, sent])

  const startDrag = (type) => (e) => {
    e.preventDefault()
    const startX = e.clientX
    const startY = e.clientY
    const origin = { ...position }
    const originSize = { ...size }
    dragRef.current = { type, startX, startY, origin, originSize }
    window.addEventListener('pointermove', onPointerMove)
    window.addEventListener('pointerup', stopDrag)
  }

  const onPointerMove = (e) => {
    if (!dragRef.current) return
    const { type, startX, startY, origin, originSize } = dragRef.current
    const dx = e.clientX - startX
    const dy = e.clientY - startY
    if (type === 'move') {
      setPosition({
        right: Math.max(0, origin.right - dx),
        bottom: Math.max(0, origin.bottom - dy)
      })
    } else if (type === 'resize') {
      setSize({
        width: Math.max(MIN_WIDTH, originSize.width + dx),
        height: Math.max(MIN_HEIGHT, originSize.height - dy)
      })
      setPosition({
        right: origin.right,
        bottom: Math.max(0, origin.bottom + dy)
      })
    }
  }

  const stopDrag = () => {
    window.removeEventListener('pointermove', onPointerMove)
    window.removeEventListener('pointerup', stopDrag)
    dragRef.current = null
  }

  useEffect(() => () => stopDrag(), [])

  const handleSend = async (e) => {
    e.preventDefault()
    try {
      setSending(true)
      await sendMessage({ receiver: form.to, subject: form.subject, body: form.body })
      setForm({ to: '', subject: '', body: '' })
      setView('list')
    } catch (err) {
      alert(err?.message || '쪽지 전송에 실패했습니다.')
    } finally {
      setSending(false)
    }
  }

  const quickPeople = EMPLOYEES.slice(0, 5)

  if (!panelState.open) return null

  return (
    <div
      className="message-panel"
      style={{ width: size.width, height: size.height, right: position.right, bottom: position.bottom }}
    >
      <div className="panel-header" onPointerDown={startDrag('move')}>
        <div>
          <strong>쪽지</strong>
          <span className="muted">드래그해 이동 · 하단으로 크기 조절</span>
        </div>
        <div className="panel-actions">
          <button type="button" className={`pill ${view === 'list' ? 'is-active' : ''}`} onClick={() => setView('list')}>목록</button>
          <button type="button" className={`pill ${view === 'compose' ? 'is-active' : ''}`} onClick={() => setView('compose')}>작성</button>
          <button type="button" className="close-btn" onClick={closePanel} aria-label="닫기">×</button>
        </div>
      </div>

      <div className="panel-body">
        {view === 'list' && (
          <MessageList
            inbox={inbox}
            sent={sent}
            onSelect={(id) => { setTargetId(id); setView('detail'); showMessage(id) }}
          />
        )}

        {view === 'detail' && (
          <MessageDetail
            message={currentMessage}
            onBack={() => setView('list')}
            onReply={() => {
              setForm({ to: currentMessage?.sender || '', subject: `RE: ${currentMessage?.subject || ''}`, body: '' })
              setView('compose')
              composeMessage({ to: currentMessage?.sender || '' })
            }}
            onMarkRead={() => currentMessage && markRead(currentMessage.id)}
          />
        )}

        {view === 'compose' && (
          <form className="message-form" onSubmit={handleSend}>
            <label>
              <span>받는 사람</span>
              <input
                className="input"
                value={form.to}
                onChange={e => setForm(f => ({ ...f, to: e.target.value }))}
                list="message-people"
                placeholder="user@kg.co"
                required
              />
              <datalist id="message-people">
                {quickPeople.map(p => <option key={p.email} value={p.email}>{p.name}</option>)}
              </datalist>
            </label>
            <label>
              <span>제목</span>
              <input
                className="input"
                value={form.subject}
                onChange={e => setForm(f => ({ ...f, subject: e.target.value }))}
                placeholder="제목"
              />
            </label>
            <label className="grow">
              <span>내용</span>
              <textarea
                className="input"
                rows="6"
                value={form.body}
                onChange={e => setForm(f => ({ ...f, body: e.target.value }))}
                placeholder="간단한 메모나 안부를 작성해 보세요."
                required
              />
            </label>
            <div className="form-actions">
              <button type="button" className="btn" onClick={() => setView('list')}>취소</button>
              <button type="submit" className="btn primary" disabled={sending}>
                {sending ? '전송 중...' : '보내기'}
              </button>
            </div>
          </form>
        )}
      </div>

      <button className="resize-handle" type="button" aria-label="크기 조절" onPointerDown={startDrag('resize')} />
    </div>
  )
}

function MessageList({ inbox, sent, onSelect }) {
  return (
    <div className="message-list">
      <p className="section-title">받은 쪽지</p>
      {inbox.length === 0 ? (
        <div className="empty">받은 쪽지가 없습니다.</div>
      ) : (
        <ul>
          {inbox.slice(0, 6).map(msg => (
            <li key={msg.id} className={msg.read ? '' : 'is-unread'}>
              <button type="button" onClick={() => onSelect(msg.id)}>
                <strong>{msg.subject}</strong>
                <span>{msg.sender} · {new Date(msg.createdAt).toLocaleString()}</span>
                <p>{(msg.body || '').slice(0, 80)}</p>
              </button>
            </li>
          ))}
        </ul>
      )}

      <p className="section-title" style={{ marginTop: 12 }}>보낸 쪽지</p>
      {sent.length === 0 ? (
        <div className="empty">보낸 쪽지가 없습니다.</div>
      ) : (
        <ul>
          {sent.slice(0, 4).map(msg => (
            <li key={msg.id}>
              <button type="button" onClick={() => onSelect(msg.id)}>
                <strong>{msg.subject}</strong>
                <span>To. {msg.receiver}</span>
                <p>{(msg.body || '').slice(0, 80)}</p>
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

function MessageDetail({ message, onBack, onReply, onMarkRead }) {
  if (!message) {
    return (
      <div className="message-detail">
        <p>선택된 쪽지가 없습니다.</p>
        <button className="btn sm" type="button" onClick={onBack}>목록으로</button>
      </div>
    )
  }
  return (
    <div className="message-detail">
      <button className="link sm" type="button" onClick={onBack}>← 목록</button>
      <h3>{message.subject}</h3>
      <div className="meta">
        <span>From. {message.sender}</span>
        <span>To. {message.receiver}</span>
        <span>{new Date(message.createdAt).toLocaleString()}</span>
      </div>
      <div className="body">{message.body}</div>
      <div className="detail-actions">
        <button className="btn sm" type="button" onClick={onReply}>답장</button>
        {!message.read && <button className="btn sm" type="button" onClick={onMarkRead}>읽음 표시</button>}
      </div>
    </div>
  )
}
