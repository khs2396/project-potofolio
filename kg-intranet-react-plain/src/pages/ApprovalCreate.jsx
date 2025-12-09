import { useEffect, useState } from 'react'
import { useLocation, useNavigate, useSearchParams } from 'react-router-dom'
import ApproverPicker from '../components/ApproverPicker.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import * as approvals from '../services/approvals.js'

export default function ApprovalCreate() {
  const { user } = useAuth()
  const nav = useNavigate()
  const location = useLocation()
  const [params] = useSearchParams()
  const [saving, setSaving] = useState(false)
  const [file, setFile] = useState(null)

  const draft = location.state || null
  const isResubmit = Boolean(draft)

  const [form, setForm] = useState({
    subject: draft?.subject || '',
    approval_content: draft?.approval_content || '',
    task_seq: draft?.task_seq || params.get('taskSeq') || '',
    file_name: draft?.file_name || '',
    approver: null
  })

  useEffect(() => {
    if (!draft) return
    setForm(prev => ({
      ...prev,
      subject: draft.subject || prev.subject,
      approval_content: draft.approval_content || prev.approval_content,
      task_seq: draft.task_seq || prev.task_seq,
      file_name: draft.file_name || prev.file_name,
    }))
  }, [draft])

  const onSubmit = async (e) => {
    e.preventDefault()
    if (!form.subject.trim()) {
      alert('제목을 입력해 주세요.')
      return
    }
    if (!form.approval_content.trim()) {
      alert('내용을 입력해 주세요.')
      return
    }

    setSaving(true)
    try {
      const payload = {
        subject: form.subject.trim(),
        approval_content: form.approval_content.trim(),
        requester_id: user?.empNo || user?.id || '',
        task_seq: form.task_seq ? Number(form.task_seq) : undefined,
        file_name: form.file_name || (file?.name || ''),
        approver: form.approver?.id ?? null,
        file: file || null
      }
      const created = await approvals.create(payload)
      if (!created) {
        alert('결재 생성에 실패했습니다.')
        return
      }

      alert('결재 요청이 등록되었습니다.')
      nav('/approvals/outbox')
      setForm({ subject: '', approval_content: '', task_seq: '', file_name: '', approver: null })
      setFile(null)
    } catch (err) {
      console.error(err)
      alert('결재 생성 중 오류가 발생했습니다.')
    } finally {
      setSaving(false)
    }
  }

  const onFileChange = (e) => {
    const f = e.target.files?.[0] || null
    setFile(f)
    if (f) setForm(prev => ({ ...prev, file_name: f.name }))
  }

  return (
    <section className="page">
      <h1 className="page-title">결재 요청 작성</h1>
      <form className="form" style={{ maxWidth: 720 }} onSubmit={onSubmit}>
        {isResubmit && (
          <div className="card" style={{ background: 'color-mix(in srgb, var(--accent-1, #f7c5d8), #fff 70%)', border: '1px solid color-mix(in srgb, var(--accent-1, #f7c5d8), #000 12%)' }}>
            <strong style={{ display: 'block', marginBottom: 4 }}>반려된 문서를 수정하여 다시 상신합니다.</strong>
            <p className="page-desc" style={{ margin: 0 }}>내용을 확인·보완한 뒤 제출해 주세요.</p>
          </div>
        )}

        <label className="form-row">
          <span className="form-label">제목</span>
          <input
            className="input"
            value={form.subject}
            onChange={e => setForm({ ...form, subject: e.target.value })}
            placeholder="제목"
          />
        </label>

        <label className="form-row">
          <span className="form-label">내용</span>
          <textarea
            className="input"
            rows="10"
            value={form.approval_content}
            onChange={e => setForm({ ...form, approval_content: e.target.value })}
            placeholder="내용"
          />
        </label>

        <div className="form-row">
          <span className="form-label">첨부 (선택)</span>
          <input type="file" className="input" onChange={onFileChange} />
          {file && <div className="page-desc">{file.name}</div>}
          {!file && form.file_name && <div className="page-desc">이전 첨부: {form.file_name} (필요 시 다시 첨부)</div>}
        </div>

        <div className="form-row">
          <span className="form-label">결재선</span>
          <ApproverPicker
            value={form.approver ? [form.approver] : []}
            onChange={arr => setForm({ ...form, approver: arr[0] || null })}
            filterRole="manager"
            maxCount={1}
          />
        </div>

        <div className="form-row" style={{ display: 'flex', gap: 8 }}>
          <button className="btn" type="button" onClick={() => nav('/approvals/outbox')}>
            취소
          </button>
          <button className="btn primary" type="submit" disabled={saving}>
            제출
          </button>
        </div>
      </form>
    </section>
  )
}
