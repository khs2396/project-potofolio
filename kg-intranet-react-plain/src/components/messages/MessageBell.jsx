import { useEffect, useRef, useState } from 'react'
import { useMessages } from '../../context/MessagesContext.jsx'
import '../../styles/messages.css'

export default function MessageBell() {
  const { unreadCount, inbox, showMessage, openPanel } = useMessages()
  const [open, setOpen] = useState(false)
  const ref = useRef(null)

  useEffect(() => {
    const handle = (e) => {
      if (!ref.current) return
      if (ref.current.contains(e.target)) return
      setOpen(false)
    }
    window.addEventListener('mousedown', handle)
    return () => window.removeEventListener('mousedown', handle)
  }, [])

  const latest = inbox.slice(0, 5)

  return (
    <div className="message-bell" ref={ref}>
      <button type="button" className="bell-btn" onClick={() => setOpen(o => !o)} aria-label="쪽지 알림">
        <BellIcon />
        {unreadCount > 0 && <span className="bell-badge">{unreadCount}</span>}
      </button>
      {open && (
        <div className="bell-dropdown">
          <div className="bell-head">
            <strong>최근 쪽지</strong>
            <button
              type="button"
              className="btn sm primary"
              onClick={() => { openPanel({ mode: 'list' }); setOpen(false) }}
              style={{ paddingInline: 12 }}
            >
              전체 보기
            </button>
          </div>
          <ul className="bell-list">
            {latest.length === 0 ? (
              <li className="empty">도착한 쪽지가 없습니다.</li>
            ) : latest.map(msg => (
              <li key={msg.id} className={msg.read ? '' : 'is-unread'}>
                <button
                  type="button"
                  onClick={() => { showMessage(msg.id); setOpen(false) }}
                >
                  <span className="title">{msg.subject}</span>
                  <span className="meta">{msg.sender} · {new Date(msg.createdAt).toLocaleString()}</span>
                  <span className="body">{(msg.body || '').slice(0, 64)}</span>
                </button>
              </li>
            ))}
          </ul>
          <div className="bell-foot">
            <button type="button" className="btn sm" onClick={() => { openPanel({ mode: 'compose' }); setOpen(false) }}>새 쪽지</button>
          </div>
        </div>
      )}
    </div>
  )
}

function BellIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" aria-hidden="true">
      <path
        d="M15 19a3 3 0 1 1-6 0M5 9a7 7 0 1 1 14 0c0 4 2 6 2 6H3s2-2 2-6"
        stroke="currentColor"
        strokeWidth="1.6"
        fill="none"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  )
}
