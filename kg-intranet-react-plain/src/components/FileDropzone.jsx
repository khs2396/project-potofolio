import { useRef, useState } from 'react'

export default function FileDropzone({ onFiles }) {
  const [active, setActive] = useState(false)
  const ref = useRef(null)
  const onPick = e => onFiles?.(Array.from(e.target.files || []))
  const onDrop = e => { e.preventDefault(); setActive(false); onFiles?.(Array.from(e.dataTransfer.files || [])) }
  return (
    <div className={active ? 'dropzone active' : 'dropzone'}
      onDragOver={e => { e.preventDefault(); setActive(true) }}
      onDragLeave={() => setActive(false)}
      onDrop={onDrop}
      onClick={() => ref.current?.click()}>
      <input ref={ref} type="file" hidden multiple onChange={onPick} />
      <div className="dz-inner">
        <div className="dz-icon">📎</div>
        <div className="dz-text">파일을 끌어오거나 클릭하여 선택하세요</div>
      </div>
    </div>
  )
}
