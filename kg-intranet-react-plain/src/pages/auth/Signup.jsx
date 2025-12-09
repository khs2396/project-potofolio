import { useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import '../../styles/auth.css'
import PasswordInput from '../../components/form/PasswordInput.jsx'
import SubmitButton from '../../components/form/SubmitButton.jsx'
import * as Auth from '../../services/auth.api.js'

const isEmail = (v) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(v || '').trim())

// DB 코드 기반(Department)
const DEPARTMENTS = [
  { id: 1, label: '인사부' },
  { id: 2, label: '총무부' },
  { id: 3, label: '개발부' },
  { id: 4, label: '재무부' },
  { id: 5, label: '영업부' },
]

const formatPhone = (value) => {
  const digits = String(value || '').replace(/\D/g, '')
  if (!digits) return ''
  if (digits.startsWith('02')) return digits.replace(/(\d{2})(\d{3,4})(\d{4}).*/, '$1-$2-$3')
  return digits.replace(/(\d{3})(\d{3,4})(\d{4}).*/, '$1-$2-$3')
}

const passwordChecks = [
  { key: 'len', label: '8자 이상', test: pwd => pwd.length >= 8 },
  { key: 'mix', label: '문자+숫자 조합', test: pwd => /[A-Za-z]/.test(pwd) && /\d/.test(pwd) },
  { key: 'special', label: '특수문자 포함', test: pwd => /[^A-Za-z0-9]/.test(pwd) },
]

const scrollTop = () => window.scrollTo({ top: 0, behavior: 'smooth' })

export default function Signup() {
  const nav = useNavigate()
  const [form, setForm] = useState({
    name: '',
    email: '',
    password: '',
    confirm: '',
    phone: '',
    depId: DEPARTMENTS[0].id,
  })
  const [verificationSent, setVerificationSent] = useState(false)
  const [verificationCode, setVerificationCode] = useState('')
  const [verificationStatus, setVerificationStatus] = useState({ type: 'idle', message: '' })
  const [isVerified, setIsVerified] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const onChange = (key) => (e) => setForm(prev => ({ ...prev, [key]: e.target.value }))
  const onEmailChange = (e) => {
    setForm(prev => ({ ...prev, email: e.target.value }))
    setVerificationSent(false)
    setVerificationStatus({ type: 'idle', message: '' })
    setIsVerified(false)
  }
  const passwordStatus = useMemo(
    () => passwordChecks.map(check => ({ ...check, pass: check.test(form.password) })),
    [form.password]
  )
  const passCount = passwordStatus.filter(item => item.pass).length

  const sendVerification = async () => {
    if (!isEmail(form.email)) {
      setError('이메일을 확인해주세요')
      setVerificationStatus({ type: 'error', message: '유효한 이메일 주소를 입력해주세요' })
      return
    }
    setError('')
    try {
      await Auth.requestEmailCode(form.email.trim())
      setVerificationSent(true)
      setVerificationStatus({ type: 'success', message: '인증번호를 메일로 보냈습니다.' })
    } catch (err) {
      setVerificationStatus({ type: 'error', message: err?.message || '인증번호 발송에 실패했습니다.' })
      setError(err?.message || '인증번호 발송에 실패했습니다.')
    }
  }

  const verifyCode = async () => {
    if (!form.email.trim() || !verificationCode.trim()) {
      setVerificationStatus({ type: 'error', message: '이메일과 인증번호를 입력해주세요.' })
      scrollTop()
      return
    }
    try {
      const res = await Auth.verifyEmailCode(form.email.trim(), verificationCode.trim())
      if (res?.rt === 'OK') {
        setVerificationStatus({ type: 'success', message: '이메일 인증이 완료되었습니다.' })
        setIsVerified(true)
      } else {
        setVerificationStatus({ type: 'error', message: res?.message || '인증번호가 올바르지 않습니다.' })
        setIsVerified(false)
      }
    } catch (err) {
      setVerificationStatus({ type: 'error', message: err?.message || '인증번호 검증에 실패했습니다.' })
      setIsVerified(false)
    }
  }

  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    if (!isEmail(form.email)) { setError('이메일을 확인해주세요'); scrollTop(); return }
    if (!form.name.trim()) { setError('이름을 입력해주세요'); scrollTop(); return }
    if (form.password.length < 8) { setError('비밀번호는 8자 이상이어야 합니다.'); scrollTop(); return }
    if (form.password !== form.confirm) { setError('비밀번호가 일치하지 않습니다.'); scrollTop(); return }
    if (!form.phone.trim()) { setError('전화번호를 입력해주세요'); scrollTop(); return }
    if (!isVerified) { setError('이메일 인증을 완료해주세요'); scrollTop(); return }

    try {
      setLoading(true)
      await Auth.signup({
        email: form.email.trim(),
        password: form.password,
        name: form.name.trim(),
        phone: form.phone.trim(),
        depId: Number(form.depId),
        roleId: 1, // 기본값: 사원
        agree: true,
      })
      nav('/signup/complete', { replace: true })
    } catch (err) {
      setError(err?.message || '가입에 실패했습니다.')
      scrollTop()
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="auth-page signup">
      <div className="signup-plain">
        <h1 className="auth-title signup-title">회원가입</h1>
        <p className="auth-sub signup-sub">기본 정보를 입력하고 이메일 인증을 완료해주세요</p>
        {error && <div className="auth-error" style={{ textAlign: 'center' }}>{error}</div>}

        <form className="auth-form signup-form" onSubmit={onSubmit}>
          <label className="auth-field">
            <span className="auth-label">이름</span>
            <input className="auth-input" value={form.name} onChange={onChange('name')} placeholder="홍길동" />
          </label>

          <label className="auth-field">
            <span className="auth-label">이메일</span>
            <div className="signup-email-row">
              <input className="auth-input" type="email" value={form.email} onChange={onEmailChange} placeholder="you@example.com" />
              <button className="btn" type="button" onClick={sendVerification}>인증</button>
            </div>
          </label>

          <label className="auth-field">
            <span className="auth-label">인증번호</span>
            <div className="signup-email-row">
              <input
                className="auth-input"
                value={verificationCode}
                onChange={e => setVerificationCode(e.target.value)}
                placeholder={verificationSent ? '인증번호 입력' : '먼저 이메일 인증을 요청하세요'}
                disabled={isVerified}
              />
              <button className="btn" type="button" onClick={verifyCode} disabled={isVerified || !verificationSent}>확인</button>
            </div>
          </label>

          {verificationStatus.message && (
            <div className={`signup-status ${verificationStatus.type === 'error' ? 'is-error' : 'is-success'}`}>
              {verificationStatus.message}
            </div>
          )}

          <label className="auth-field">
            <span className="auth-label">비밀번호</span>
            <div className="pwd-wrapper">
              <PasswordInput className="auth-input" value={form.password} onChange={onChange('password')} placeholder="비밀번호" />
              <div className="pwd-meter">
                <div className="bar" style={{ width: `${(passCount / passwordChecks.length) * 100}%` }} />
              </div>
            </div>
            <ul className="pwd-hints">
              {passwordStatus.map(item => (
                <li key={item.key} className={item.pass ? 'pass' : ''}>{item.label}</li>
              ))}
            </ul>
          </label>

          <label className="auth-field">
            <span className="auth-label">비밀번호 확인</span>
            <PasswordInput className="auth-input" value={form.confirm} onChange={onChange('confirm')} placeholder="비밀번호 확인" />
          </label>

          <div className="auth-row">
            <label className="auth-field">
              <span className="auth-label">전화번호</span>
              <input className="auth-input" value={form.phone} onChange={e => setForm(prev => ({ ...prev, phone: formatPhone(e.target.value) }))} placeholder="010-1234-5678" />
            </label>

            <label className="auth-field">
              <span className="auth-label">부서</span>
              <select className="auth-input" value={form.depId} onChange={onChange('depId')}>
                {DEPARTMENTS.map(dep => <option key={dep.id} value={dep.id}>{dep.label}</option>)}
              </select>
            </label>
          </div>

          <p className="auth-help" style={{ textAlign: 'center', marginTop: 4 }}>
            기본 정보를 입력한 뒤 가입하기 버튼을 눌러주세요.
          </p>

          <div className="auth-actions" style={{ justifyContent: 'center' }}>
            <SubmitButton loading={loading}>가입하기</SubmitButton>
            <Link to="/login" className="btn">로그인</Link>
          </div>
        </form>
      </div>
    </section>
  )
}
