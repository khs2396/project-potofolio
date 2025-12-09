import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext.jsx'
import MessageBell from '../messages/MessageBell.jsx'

export default function Header() {
  const { user, signOut } = useAuth()
  const nav = useNavigate()
  const linkClass = ({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')
  const onLogout = () => { signOut(); nav('/login') }
  const roleText = (user?.role || user?.title || '').toLowerCase()
  const isManager = (user?.roleId === 5) || ['manager', 'gm', '부장', 'manager'].some(k => roleText.includes(k))

  return (
    <header className="site-header">
      <div className="container header-inner">
        <Link to="/" className="brand">
          <span className="brand-logo" aria-hidden="true">KG</span>
          <span className="brand-text">KG Intranet</span>
        </Link>

        <nav className="nav">
          <NavLink to="/" end className={linkClass}>홈</NavLink>
          <NavLink to="/announcements" className={linkClass}>공지</NavLink>
          <NavLink to="/tasks" className={linkClass}>업무</NavLink>
          {isManager && <NavLink to="/approvals/inbox" className={linkClass}>결재함</NavLink>}
          {!isManager && <NavLink to="/approvals/outbox" className={linkClass}>결재 요청함</NavLink>}
          <NavLink to="/approvals" end className={linkClass}>결재 내역</NavLink>
          <NavLink to="/boards" className={linkClass}>게시판</NavLink>
          <NavLink to="/directory" className={linkClass}>직원</NavLink>

          {user ? (
            <>
              <span className="welcome">안녕하세요 {user.name || '사용자'}님</span>
              <MessageBell />
              <NavLink to="/settings/profile" className={linkClass}>프로필</NavLink>
              <button type="button" className="btn" onClick={onLogout}>로그아웃</button>
            </>
          ) : (
            <>
              <NavLink to="/login" className={linkClass}>로그인</NavLink>
              <NavLink to="/signup" className={linkClass}>회원가입</NavLink>
            </>
          )}
        </nav>
      </div>
    </header>
  )
}
