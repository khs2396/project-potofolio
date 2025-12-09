import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import '../../styles/auth.css'
import PasswordInput from '../../components/form/PasswordInput.jsx'
import SubmitButton from '../../components/form/SubmitButton.jsx'
import * as Auth from '../../services/auth.api.js'

const isEmail = (v) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(v || '').trim())

export default function Login() {
  const nav = useNavigate()
  const loc = useLocation()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [remember, setRemember] = useState(true)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [caps, setCaps] = useState(false)

  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    if (!isEmail(email)) { setError('이메일 형식을 확인해주세요.'); return }
    try {
      setLoading(true)
      await Auth.login({ email, password, remember })
      const back = loc.state?.from || '/'
      nav(back, { replace: true })
    } catch (err) {
      setError(err?.message || '로그인에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="auth-page">
      <div className="auth-layout">
        <aside className="auth-hero">
          <div className="brand-badge">KG</div>
          <h2 className="hero-title">사내 인트라넷</h2>
          <p className="hero-copy">안전하고 빠른 업무 허브</p>
        </aside>

        <div className="auth-card glass">
          <h1 className="auth-title">로그인</h1>
          <p className="auth-sub">계정으로 계속 진행하세요</p>

          <form className="auth-form" onSubmit={onSubmit}>
            <div className="auth-field" style={{ marginBottom: '20px' }}>
              <input
                id="email"
                className={`auth-input ${email && !isEmail(email) ? 'invalid' : ''}`}
                type="email"
                value={email}
                onChange={e => setEmail(e.target.value)}
                placeholder="이메일"
                autoComplete="username"
              />
              {email && !isEmail(email) && (
                <div className="auth-error">이메일 형식을 확인해주세요.</div>
              )}
            </div>

            <div className="auth-field" style={{ marginBottom: '20px' }}>
              <PasswordInput
                value={password}
                onChange={e => setPassword(e.target.value)}
                onKeyUp={(e) => setCaps(e.getModifierState && e.getModifierState('CapsLock'))}
                placeholder="비밀번호"
              />
              {caps && <div className="auth-help">Caps Lock이 켜져 있습니다.</div>}
            </div>

            <div className="auth-row">
              <label className="auth-help">
                <input type="checkbox" checked={remember} onChange={e => setRemember(e.target.checked)} /> 로그인 상태 유지
              </label>
              <div className="auth-row-actions">
                <Link className="link" to="/find-password">비밀번호 찾기</Link>
                <span>·</span>
                <Link className="link" to="/signup">회원가입</Link>
              </div>
            </div>

            {error && <div className="auth-error">{error}</div>}

            <div className="auth-actions">
              <SubmitButton loading={loading}>로그인</SubmitButton>
            </div>
          </form>
        </div>
      </div>
    </section>
  )
}
