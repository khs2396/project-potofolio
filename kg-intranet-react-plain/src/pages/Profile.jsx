import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import { useMessages } from '../context/MessagesContext.jsx'
import AttendanceCard from '../components/AttendanceCard.jsx'
import '../styles/profile.css'
import { getDeptColor } from '../utils/avatarColors.js'

const translateTitle = (title = '') => {
  const raw = (title || '').trim()
  const upper = raw.toUpperCase()
  if (upper === 'ST') return '사원'
  if (upper === 'AS') return '주임'
  if (upper === 'AC') return '대리'
  if (upper === 'MGR') return '과장'
  if (upper === 'GM') return '부장'

  const lower = raw.toLowerCase()
  if (lower.includes('manager')) return '매니저'
  if (lower.includes('lead') || lower.includes('leader')) return '리더'
  if (lower.includes('director')) return '이사'
  if (lower.includes('vp')) return '부사장'
  if (lower.includes('gm')) return '총괄'
  if (lower.includes('principal')) return '프린시펄'
  if (lower.includes('senior')) return '시니어'
  if (lower.includes('junior')) return '주니어'
  if (lower.includes('intern')) return '인턴'
  if (lower.includes('staff')) return '사원'
  return raw || '직급 미정'
}

const formatPhone = (value) => {
  const digits = String(value || '').replace(/\D/g, '').slice(0, 11)
  if (!digits) return ''
  if (digits.startsWith('02')) {
    return digits.replace(/(\d{2})(\d{3,4})(\d{0,4}).*/, (_, a, b, c) => (c ? `${a}-${b}-${c}` : `${a}-${b}`))
  }
  return digits.replace(/(\d{3})(\d{3,4})(\d{0,4}).*/, (_, a, b, c) => (c ? `${a}-${b}-${c}` : `${a}-${b}`))
}

const formatDate = (value) => {
  if (!value) return ''
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return ''
  return d.toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

export default function Profile() {
  const { user } = useAuth()
  const { inbox, openPanel, showMessage } = useMessages()
  const u = user || {}

  const displayName = u.name || u.email || '이름을 입력해 주세요'
  const initials = (u.name || u.email || 'U').trim().slice(0, 2).toUpperCase()
  const displayTitle = translateTitle(u.title || '')
  const joinDateRaw = u.joinedAt || u.hireDate || u.joinDate || u.joinTime || ''
  const joinDate = formatDate(joinDateRaw)
  const deptColor = getDeptColor(u.dept)
  const avatar = u.avatar || ''

  const summary = [
    { label: '부서', value: u.dept || '부서 미정' },
    { label: '직급', value: displayTitle },
    { label: '입사일', value: joinDate || '미입력' }
  ]

  const contact = [
    { label: '이메일', value: u.email || '미입력' },
    { label: '전화', value: formatPhone(u.phone) || '미입력' }
  ]

  const latestMessages = inbox.slice(0, 4)

  return (
    <section className="page profile-page">
      <div className="profile-hero">
        <div className="hero-user">
          <div className="avatar-xl" style={{
            color: deptColor.text,
            overflow: 'hidden',
            display: 'grid',
            placeItems: 'center'
          }}>
            <AvatarPreview avatar={avatar} initials={initials} color={deptColor.text} />
          </div>
          <div>
            <p className="eyebrow">오늘의 프로필</p>
            <h1>{displayName}</h1>
            <p className="role-line">
              {displayTitle} · {u.dept || '부서 미정'}
            </p>
            <div className="tag-row">
              {u.email && <span>{u.email}</span>}
            </div>
          </div>
        </div>
        <div className="hero-actions">
          <Link to="/settings/profile/edit" className="btn primary lg">
            프로필 수정
          </Link>
        </div>
      </div>

      <div className="profile-summary-grid">
        {summary.map(item => (
          <div key={item.label} className="summary-card">
            <p className="label">{item.label}</p>
            <p className="value">{item.value}</p>
          </div>
        ))}
      </div>

      <div className="profile-content-grid">
        <div className="info-card">
          <h2>연락처</h2>
          <ul className="contact-list">
            {contact.map(field => (
              <li key={field.label}>
                <span>{field.label}</span>
                <strong>{field.value}</strong>
              </li>
            ))}
          </ul>
        </div>

        <div className="info-card">
          <div className="info-head">
            <h2>최근 메시지</h2>
            <button
              type="button"
              className="link sm"
              onClick={() => openPanel({ mode: 'list' })}
            >
              전체 보기
            </button>
          </div>
        {latestMessages.length === 0 ? (
          <p className="muted">메시지가 없습니다.</p>
        ) : (
          <ul className="message-snippets">
              {latestMessages.map(msg => (
                <li key={msg.id} className={msg.read ? '' : 'is-unread'}>
                  <button type="button" onClick={() => showMessage(msg.id)}>
                    <strong>{msg.subject}</strong>
                    <span className="meta">From. {msg.sender} · {new Date(msg.createdAt).toLocaleDateString()}</span>
                    <span className="preview">{(msg.body || '').slice(0, 80)}</span>
                  </button>
                </li>
              ))}
            </ul>
          )}
        </div>

        <div className="info-card span-2">
          <div className="attendance-center">
            <AttendanceCard adminView={false} />
          </div>
        </div>
      </div>
    </section>
  )
}

function AvatarPreview({ avatar, initials, color }) {
  if (avatar) {
    return <img src={avatar} alt="avatar" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
  }
  // 사람 실루엣 기본 이미지
  return (
    <svg
      aria-hidden="true"
      width="100%"
      height="100%"
      viewBox="0 0 64 64"
      fill="none"
      stroke="none"
      preserveAspectRatio="xMidYMid meet"
    >
      <circle cx="32" cy="20" r="12" fill={color} opacity="0.9" />
      <path d="M16 54c0-8.84 7.16-16 16-16s16 7.16 16 16" fill={color} opacity="0.85" />
    </svg>
  )
}
