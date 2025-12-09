import { useState } from 'react'

function Eye({ off }) {
  return off ? (
    <svg width="16" height="16" viewBox="0 0 24 24" aria-hidden="true">
      <path d="M3 3l18 18M10.58 10.58A3 3 0 0 0 12 15a3 3 0 0 0 3-3c0-.39-.08-.76-.22-1.1M6.1 6.1C4.24 7.24 2.77 8.88 2 12c2.5 6 8 7 10 7 1.54 0 6.64-.42 10-7-.67-1.62-1.69-2.95-2.92-4.01" stroke="currentColor" strokeWidth="1.6" fill="none" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  ) : (
    <svg width="16" height="16" viewBox="0 0 24 24" aria-hidden="true">
      <path d="M1 12C3.5 6 8 5 12 5s8.5 1 11 7c-2.5 6-8 7-11 7s-8.5-1-11-7z" fill="none" stroke="currentColor" strokeWidth="1.6" />
      <circle cx="12" cy="12" r="3" fill="none" stroke="currentColor" strokeWidth="1.6" />
    </svg>
  )
}

export default function PasswordInput({
  value,
  onChange,
  placeholder = '비밀번호',
  name = 'password',
  className = 'auth-input',
  ...rest
}) {
  const [show, setShow] = useState(false)
  return (
    <div className="password-input">
      <input
        type={show ? 'text' : 'password'}
        name={name}
        className={className}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        autoComplete="current-password"
        {...rest}
      />
      <button
        type="button"
        className="password-toggle"
        onClick={() => setShow(s => !s)}
        aria-label={show ? '비밀번호 숨기기' : '비밀번호 보기'}
        title={show ? '숨기기' : '보기'}
      >
        <Eye off={show} />
      </button>
    </div>
  )
}
