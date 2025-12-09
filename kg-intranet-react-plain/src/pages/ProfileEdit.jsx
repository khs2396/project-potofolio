import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import PasswordInput from '../components/form/PasswordInput.jsx'
import { loadProfile, saveProfile, loadAuthUser } from '../services/profile.js'
import { useAuth } from '../context/AuthContext.jsx'
import * as Employees from '../services/employees.js'
import { resetPassword } from '../services/auth.api.js'
import '../styles/profile-edit.css'

const ROLE_LABELS = {
  GM: '부장',
  MGR: '과장',
  AS: '주임',
  AC: '대리',
  ST: '사원',
}

const ROLES = ['사원', '주임', '대리', '과장', '부장']
const ROLE_ORDER = ['사원', '주임', '대리', '과장', '부장']
const orderRole = (label = '') => {
  const idx = ROLE_ORDER.indexOf(label)
  return idx >= 0 ? idx : ROLE_ORDER.length
}

const translateRoleLabel = (raw = '') => {
  const txt = String(raw || '').trim()
  if (!txt) return ''
  const upper = txt.toUpperCase()
  if (ROLE_LABELS[upper]) return ROLE_LABELS[upper]
  if (txt.includes('Manager') || upper.includes('MGR')) return '과장'
  if (txt.includes('Lead') || txt.includes('Leader')) return '리더'
  if (txt.includes('Director')) return '이사'
  if (txt.includes('VP')) return '부사장'
  if (txt.includes('Senior')) return '시니어'
  if (txt.includes('Junior')) return '주니어'
  if (txt.includes('Staff') || txt.includes('Employee')) return '사원'
  return txt
}

const formatPhone = (value) => {
  const digits = String(value || '').replace(/\D/g, '').slice(0, 11)
  if (!digits) return ''
  if (digits.startsWith('02')) {
    return digits.replace(/(\d{2})(\d{3,4})(\d{0,4}).*/, (_, a, b, c) => (c ? `${a}-${b}-${c}` : `${a}-${b}`))
  }
  return digits.replace(/(\d{3})(\d{3,4})(\d{0,4}).*/, (_, a, b, c) => (c ? `${a}-${b}-${c}` : `${a}-${b}`))
}

const passwordChecks = [
  { key: 'len', label: '8자 이상', test: pwd => pwd.length >= 8 },
  { key: 'mix', label: '문자+숫자 조합', test: pwd => /[A-Za-z]/.test(pwd) && /\d/.test(pwd) },
  { key: 'special', label: '특수문자 포함', test: pwd => /[^A-Za-z0-9]/.test(pwd) },
]

export default function ProfileEdit() {
  const nav = useNavigate()
  const { user, signIn } = useAuth()
  const authed = useMemo(() => loadAuthUser(), [])
  const [form, setForm] = useState(() => {
    const stored = loadProfile()
    if (stored) return { ...stored, phone: formatPhone(stored.phone), depId: stored.depId ?? null, roleId: stored.roleId ?? null }
    return { email: '', name: '', title: '', dept: '', phone: '', about: '', avatar: '', status: 'ACTIVE', depId: null, roleId: null, empNo: null }
  })
  const [loading, setLoading] = useState(false)
  const [deptOptions, setDeptOptions] = useState([])
  const [roleOptions, setRoleOptions] = useState([])

  // 비밀번호 변경 상태
  const [passwordForm, setPasswordForm] = useState({
    newPassword: '',
    confirmPassword: ''
  })
  const [passwordError, setPasswordError] = useState('')
  const [passwordLoading, setPasswordLoading] = useState(false)

  // 비밀번호 강도 체크
  const passwordStatus = useMemo(
    () => passwordChecks.map(check => ({ ...check, pass: check.test(passwordForm.newPassword) })),
    [passwordForm.newPassword]
  )
  const passCount = passwordStatus.filter(item => item.pass).length

  useEffect(() => {
    if (!authed) {
      alert('로그인이 필요합니다.')
      nav('/login')
    }
  }, [authed, nav])

  // 서버에서 직원 정보 동기화
  useEffect(() => {
    let active = true
    const sync = async () => {
      setLoading(true)
      try {
        const list = await Employees.ensureLoaded()
        const myId = authed?.empNo || authed?.id || authed?.email
        const me = list.find(e =>
          String(e.empNo) === String(myId) ||
          String(e.id) === String(myId) ||
          String(e.email).toLowerCase() === String(authed?.email || '').toLowerCase()
        )
        // 옵션 구축
        const depMap = new Map()
        const roleMap = new Map()
        list.forEach(e => {
          if (e.depId) depMap.set(e.depId, e.dept || `부서 ${e.depId}`)
          if (e.roleId) {
            const raw = e.title || ''
            const upper = raw.toUpperCase()
            const label = ROLE_LABELS[upper] || raw || `직급 ${e.roleId}`
            roleMap.set(e.roleId, label)
          }
        })
        if (active) {
          setDeptOptions(Array.from(depMap.entries()).map(([value, label]) => ({ value, label })))
          const orderedRoles = Array.from(roleMap.entries())
            .map(([value, label]) => ({ value, label, order: orderRole(label) }))
            .sort((a, b) => a.order - b.order)
            .map(({ value, label }) => ({ value, label }))
          setRoleOptions(orderedRoles)
        }

        if (active && me) {
          setForm(s => ({
            ...s,
            empNo: me.empNo ?? authed?.empNo ?? s.empNo,
            email: me.email || authed?.email || s.email,
            name: me.name || s.name,
            dept: me.dept || s.dept,
            title: translateRoleLabel(me.title || s.title),
            phone: formatPhone(me.phone || s.phone),
            depId: me.depId ?? s.depId,
            roleId: me.roleId ?? s.roleId,
          }))
        }
      } catch (err) {
        console.error('프로필 동기화 실패:', err)
      } finally {
        if (active) setLoading(false)
      }
    }
    sync()
    const unsub = Employees.subscribe(() => sync())
    return () => { active = false; unsub && unsub() }
  }, [authed])

  const onChange = (k) => (e) => setForm(s => ({ ...s, [k]: e?.target ? e.target.value : e }))
  const onPhoneChange = (e) => setForm(s => ({ ...s, phone: formatPhone(e.target.value) }))

  // 비밀번호 변경 핸들러
  const onPasswordChange = (field) => (e) => {
    setPasswordForm(s => ({ ...s, [field]: e.target.value }))
    setPasswordError('') // 입력 시 에러 초기화
  }

  const onPasswordSubmit = async (e) => {
    e.preventDefault()
    const empNo = form.empNo || authed?.empNo

    if (!empNo) {
      setPasswordError('사번 정보를 찾을 수 없습니다. 다시 로그인해 주세요.')
      return
    }

    if (!passwordForm.newPassword || passwordForm.newPassword.length < 8) {
      setPasswordError('새 비밀번호를 8자 이상 입력해 주세요.')
      return
    }

    // 비밀번호 조건 검증
    const allPassed = passwordStatus.every(item => item.pass)
    if (!allPassed) {
      setPasswordError('비밀번호는 8자 이상, 문자+숫자 조합, 특수문자를 포함해야 합니다.')
      return
    }

    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      setPasswordError('비밀번호가 일치하지 않습니다.')
      return
    }

    try {
      setPasswordLoading(true)
      setPasswordError('')
      await resetPassword({ empNo, password: passwordForm.newPassword })
      alert('비밀번호가 변경되었습니다.')
      setPasswordForm({ newPassword: '', confirmPassword: '' })
    } catch (err) {
      console.error('비밀번호 변경 실패:', err)
      setPasswordError(err?.message || '비밀번호 변경에 실패했습니다.')
    } finally {
      setPasswordLoading(false)
    }
  }

  const onSubmit = async (e) => {
    e.preventDefault()
    const empNo = form.empNo || authed?.empNo
    if (!empNo) {
      alert('사번 정보를 찾을 수 없습니다. 다시 로그인해 주세요.')
      return
    }
    const telDigits = String(form.phone || '').replace(/\D/g, '')
    const selectedDeptLabel = deptOptions.find(d => String(d.value) === String(form.depId))?.label
    const selectedRoleLabel = roleOptions.find(r => String(r.value) === String(form.roleId))?.label || form.title
    const payload = {
      empNo,
      name: form.name || '',
      email: form.email || '',
      tel: telDigits,
      dep_id: form.depId || authed?.depId || authed?.department?.depId || null,
      role_id: form.roleId || authed?.roleId || authed?.role?.roleId || null,
    }
    try {
      setLoading(true)
      await Employees.update(payload)
      // 최신 정보 다시 로드 후 인증 컨텍스트 갱신
      const list = await Employees.ensureLoaded()
      const updated = list.find(e => String(e.empNo) === String(empNo))
      if (updated) {
        const mergedUser = {
          ...(user || authed || {}),
          ...updated,
          phone: updated.phone || telDigits,
          dept: selectedDeptLabel || updated.dept,
          title: selectedRoleLabel || updated.title,
          depId: updated.depId ?? form.depId,
          roleId: updated.roleId ?? form.roleId,
        }
        signIn(mergedUser)
        setForm(s => ({
          ...s,
          ...updated,
          phone: formatPhone(updated.phone || telDigits),
          dept: selectedDeptLabel || updated.dept,
          title: selectedRoleLabel || updated.title,
          depId: updated.depId ?? form.depId,
          roleId: updated.roleId ?? form.roleId,
        }))
      }
      alert('프로필이 업데이트되었습니다.')
      nav(-1)
    } catch (err) {
      console.error('프로필 업데이트 실패:', err)
      alert('프로필 업데이트에 실패했습니다. 권한을 확인하거나 다시 시도해 주세요.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="page profile-edit-page">
      <div className="edit-hero">
        <div>
          <p className="eyebrow">Profile settings</p>
          <h1>프로필 수정</h1>
          <p>조직 구성원과 공유되는 정보를 최신 상태로 유지해 주세요.</p>
        </div>
      </div>

      <form className="profile-form" onSubmit={onSubmit}>
        <section className="form-panel">
          <div className="panel-head">
            <div>
              <h2>기본 정보</h2>
              <p>이름과 연락처를 최신 상태로 입력해 주세요.</p>
            </div>
          </div>

          <div className="form-grid two">
            <label className="field">
              <span className="label">이메일</span>
              <input className="input" value={form.email} readOnly disabled />
            </label>
            <label className="field">
              <span className="label">이름</span>
              <input className="input" value={form.name} onChange={onChange('name')} placeholder="홍길동" />
            </label>
            <label className="field">
              <span className="label">부서</span>
              <input className="input" value={form.dept || ''} readOnly disabled />
            </label>
            <label className="field">
              <span className="label">직급</span>
              <input className="input" value={form.title || ''} readOnly disabled />
            </label>
            <label className="field">
              <span className="label">전화번호</span>
              <input className="input" value={form.phone} onChange={onPhoneChange} placeholder="010-0000-0000" />
            </label>
          </div>
        </section>

        <div className="form-actions">
          <button type="submit" className="btn primary lg" disabled={loading}>변경사항 저장</button>
          <button type="button" className="btn" onClick={() => nav(-1)} disabled={loading}>취소</button>
        </div>
      </form>

      {/* 비밀번호 변경 섹션 */}
      <form className="profile-form" onSubmit={onPasswordSubmit} style={{ marginTop: 24 }}>
        <section className="form-panel">
          <div className="panel-head">
            <div>
              <h2>비밀번호 변경</h2>
              <p>보안을 위해 주기적으로 비밀번호를 변경해 주세요.</p>
            </div>
          </div>

          {passwordError && (
            <div style={{
              padding: '12px 16px',
              backgroundColor: '#fee',
              border: '1px solid #fcc',
              borderRadius: '8px',
              color: '#c33',
              marginBottom: '16px'
            }}>
              {passwordError}
            </div>
          )}

          <div className="form-grid two">
            <label className="field" style={{ gridColumn: '1 / -1' }}>
              <span className="label">새 비밀번호</span>
              <div className="pwd-wrapper">
                <PasswordInput
                  value={passwordForm.newPassword}
                  onChange={onPasswordChange('newPassword')}
                  placeholder="8자 이상 입력"
                  className="input"
                  autoComplete="new-password"
                />
                <div className="pwd-meter">
                  <div className="bar" style={{ width: `${(passCount / passwordChecks.length) * 100}%` }} />
                </div>
              </div>
              <ul className="pwd-hints" style={{ display: 'flex', gap: '24px', flexWrap: 'wrap', marginTop: '8px' }}>
                {passwordStatus.map(item => (
                  <li key={item.key} className={item.pass ? 'pass' : ''} style={{ minWidth: 'fit-content' }}>{item.label}</li>
                ))}
              </ul>
            </label>

            <label className="field" style={{ gridColumn: '1 / -1' }}>
              <span className="label">새 비밀번호 확인</span>
              <PasswordInput
                value={passwordForm.confirmPassword}
                onChange={onPasswordChange('confirmPassword')}
                placeholder="한 번 더 입력"
                className="input"
                autoComplete="new-password"
              />
            </label>
          </div>
        </section>

        <div className="form-actions">
          <button type="submit" className="btn primary lg" disabled={passwordLoading}>
            비밀번호 변경
          </button>
          <button
            type="button"
            className="btn"
            onClick={() => {
              setPasswordForm({ newPassword: '', confirmPassword: '' })
              setPasswordError('')
            }}
            disabled={passwordLoading}
          >
            초기화
          </button>
        </div>
      </form>
    </section>
  )
}
