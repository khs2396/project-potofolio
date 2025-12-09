import { Link } from 'react-router-dom'
import '../../styles/auth.css'

export default function SignupComplete() {
  return (
    <section className="auth-page signup">
      <div className="signup-plain">
        <h1 className="auth-title signup-title">가입 신청 완료</h1>
        <p className="auth-sub signup-sub">
          제출해 주셔서 감사합니다. 관리자가 정보를 확인하는 동안 잠시만 기다려 주세요.
        </p>

        <div className="card" style={{ maxWidth: 520, margin: '0 auto', textAlign: 'center' }}>
          <p style={{ marginBottom: 12 }}>
            가입 메일을 확인하고 인증을 마치면 바로 로그인하실 수 있습니다.
            추가 확인이 필요하면 운영팀에서 연락을 드립니다.
          </p>
          <p style={{ color: 'var(--fg-muted)', fontSize: '0.95rem' }}>
            계속 진행하려면 아래 버튼으로 이동하세요.
          </p>
          <div style={{ display: 'flex', gap: 12, justifyContent: 'center', marginTop: 20 }}>
            <Link to="/login" className="btn primary">
              로그인 화면으로
            </Link>
            <Link to="/" className="btn">
              홈으로 돌아가기
            </Link>
          </div>
        </div>
      </div>
    </section>
  )
}
