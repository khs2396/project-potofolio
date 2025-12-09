import { Link } from 'react-router-dom'
export default function Footer() {
  return (
    <footer className="footer">
      <div className="container" style={{ display: 'flex', alignItems: 'center', gap: 12, flexWrap: 'wrap', padding: '12px 16px' }}>
        <div style={{ flex: 1 }} />
        <nav className="footer-links" style={{ display: 'flex', alignItems: 'center', gap: 12, flexWrap: 'wrap' }}>
          <Link className="link" to="/approvals">전자결재</Link>
          <Link className="link" to="/directory">직원 디렉터리</Link>
          <Link className="link" to="/announcements">공지사항</Link>
        </nav>
      </div>
    </footer>
  )
}
