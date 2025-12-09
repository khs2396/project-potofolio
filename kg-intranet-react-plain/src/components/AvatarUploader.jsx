import { useRef, useState, useEffect } from 'react'
import { getDeptColor } from '../utils/avatarColors.js'

export default function AvatarUploader({ value, onChange, size = 96, dept = '', initials = 'U' }) {
  const fileRef = useRef(null)
  const [preview, setPreview] = useState(value || '')

  useEffect(() => { setPreview(value || '') }, [value])

  const pick = () => fileRef.current?.click()

  const toBase64 = (file) => new Promise((res, rej) => {
    const fr = new FileReader()
    fr.onload = () => res(fr.result)
    fr.onerror = rej
    fr.readAsDataURL(file)
  })

  const onFile = async (e) => {
    const file = e.target.files?.[0]
    if (!file) return
    if (!file.type.startsWith('image/')) {
      alert('이미지 파일만 업로드할 수 있습니다.')
      return
    }
    const max = 2 * 1024 * 1024
    if (file.size > max) {
      alert('2MB 이하의 이미지를 선택해 주세요.')
      return
    }
    const b64 = await toBase64(file)
    setPreview(b64)
    onChange?.(b64, file)
  }

  const reset = () => { setPreview(''); onChange?.('') }

  const deptColor = getDeptColor(dept)

  return (
    <div className="avatar-uploader">
      <div
        className="avatar"
        style={{
          width: size,
          height: size,
          backgroundColor: preview ? 'transparent' : deptColor.bg,
          color: deptColor.text,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          fontWeight: 800,
          fontSize: size > 80 ? '2rem' : '1.5rem',
          borderRadius: size > 100 ? '24px' : '16px'
        }}
        onClick={pick}
      >
        {preview ? (
          <img src={preview} alt="avatar" />
        ) : (
          <Silhouette size={size} color={deptColor.text} />
        )}
      </div>
      <div className="avatar-actions">
        <button type="button" className="btn sm" onClick={pick}>이미지 선택</button>
        {preview && <button type="button" className="btn sm" onClick={reset}>삭제</button>}
      </div>
      <input ref={fileRef} type="file" accept="image/*" style={{ display: 'none' }} onChange={onFile} />
    </div>
  )
}

function Silhouette({ size = 96, color = '#1f2937' }) {
  return (
    <svg
      aria-hidden="true"
      width={Math.min(size * 0.6, 72)}
      height={Math.min(size * 0.6, 72)}
      viewBox="0 0 64 64"
      fill="none"
      stroke="none"
    >
      <circle cx="32" cy="20" r="12" fill={color} opacity="0.9" />
      <path
        d="M16 54c0-8.84 7.16-16 16-16s16 7.16 16 16"
        fill={color}
        opacity="0.85"
      />
    </svg>
  )
}
