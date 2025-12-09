import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import '../../styles/auth.css'
import SubmitButton from '../../components/form/SubmitButton.jsx'
import { verifyAccountForReset, resetPassword } from '../../services/auth.api.js'

const isEmail = (v) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(v || '').trim())
const isEmpNo = (v) => /^\d{4,}$/.test(String(v || '').trim())
const passwordChecks = [
  { key: 'len', label: '8자 이상', test: pwd => pwd.length >= 8 },
  { key: 'mix', label: '문자+숫자 조합', test: pwd => /[A-Za-z]/.test(pwd) && /\d/.test(pwd) },
  { key: 'special', label: '특수문자 포함', test: pwd => /[^A-Za-z0-9]/.test(pwd) },
]

export default function FindPassword() {
  const nav = useNavigate()
  const [empNo, setEmpNo] = useState('')
  const [email, setEmail] = useState('')
  const [pw, setPw] = useState('')
  const [pw2, setPw2] = useState('')
  const [sent, setSent] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const passStatus = passwordChecks.map(check => ({ ...check, pass: check.test(pw) }))
  const passCount = passStatus.filter(c => c.pass).length

  const onSubmit = async (e) => {
    e.preventDefault()
    if (!isEmpNo(empNo)) return setError('사번을 숫자로 입력해 주세요')
    if (!isEmail(email)) return setError('이메일 형식을 확인해 주세요')
    if (!pw || pw.length < 8) return setError('비밀번호는 8자 이상이어야 합니다')
    if (passCount < passwordChecks.length) return setError('문자+숫자+특수문자를 모두 포함해 주세요')
    if (pw !== pw2) return setError('비밀번호가 일치하지 않습니다')
    setError('')
    setLoading(true)
    try {
      await verifyAccountForReset({ empNo, email })
      await resetPassword({ empNo, password: pw })
      setSent(true)
      nav('/login', { replace: true })
    } catch (err) {
      console.error(err)
      setError(err?.message || '설정에 실패했습니다. 정보를 확인해 주세요')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="auth-page">
      <div className="auth-layout">
        <aside className="auth-hero">
          <div className="brand-badge">KG</div>
          <h2 className="hero-title">계정 복구</h2>
          <p className="hero-copy">사번과 이메일을 확인 후 비밀번호를 재설정하세요.</p>
        </aside>

        <div className="auth-card glass">
          <h1 className="auth-title">비밀번호 재설정</h1>
          <p className="auth-sub">사번과 회사 이메일을 입력한 뒤 새 비밀번호를 설정하세요.</p>

          {sent && (
            <div className="auth-note success">
              비밀번호가 변경되었습니다. 새 비밀번호로 다시 로그인해 주세요.
            </div>
          )}
          {error && <div className="auth-error">{error}</div>}

          <form className="auth-form" onSubmit={onSubmit}>
            <label className="auth-field">
              <span className="auth-label">사번</span>
              <input
                className="auth-input"
                value={empNo}
                onChange={e => setEmpNo(e.target.value)}
                placeholder="예) 20250011"
              />
            </label>

            <label className="auth-field">
              <span className="auth-label">회사 이메일</span>
              <input
                className="auth-input"
                type="email"
                value={email}
                onChange={e => setEmail(e.target.value)}
                placeholder="you@kg.co"
              />
            </label>

            <label className="auth-field">
              <span className="auth-label">새 비밀번호</span>
              <input
                className="auth-input"
                type="password"
                value={pw}
                onChange={e => setPw(e.target.value)}
                placeholder="8자 이상, 문자+숫자+특수문자"
              />
              <div className="pwd-meter" style={{ marginTop: 6 }}>
                <div className="bar" style={{ width: `${(passCount / passwordChecks.length) * 100}%` }} />
              </div>
              <ul className="pwd-hints" style={{ marginTop: 6 }}>
                {passStatus.map(item => (
                  <li key={item.key} className={item.pass ? 'pass' : ''}>{item.label}</li>
                ))}
              </ul>
            </label>

            <label className="auth-field">
              <span className="auth-label">새 비밀번호 확인</span>
              <input
                className="auth-input"
                type="password"
                value={pw2}
                onChange={e => setPw2(e.target.value)}
                placeholder="다시 입력"
              />
            </label>

            <div className="auth-actions">
              <SubmitButton disabled={loading}>비밀번호 변경</SubmitButton>
            </div>
          </form>
        </div>
      </div>
    </section>
  )
}
