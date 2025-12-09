export default function StatusBadge(props) {
  const raw = (props.value ?? props.state ?? props.status ?? '').toString().trim().toUpperCase()
  const map = {
    DRAFT: { label: '기안', cls: 'is-draft' },
    IN_REVIEW: { label: '결재중', cls: 'is-review' },
    APPROVED: { label: '승인', cls: 'is-approved' },
    REJECTED: { label: '반려', cls: 'is-rejected' },
  }
  const info = map[raw] || { label: raw || '-', cls: '' }
  return (
    <span className={`status-badge ${info.cls}`} title={info.label}>{info.label}</span>
  )
}
