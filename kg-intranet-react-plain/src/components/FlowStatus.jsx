export default function FlowStatus({ state, compact = false }) {
  const s = String(state || '').toUpperCase()
  let pct = 0, label3 = '완료', cls = ''
  if (s === 'DRAFT') { pct = 0; cls = 'is-draft' }
  else if (s === 'IN_REVIEW') { pct = 50; cls = 'is-review' }
  else if (s === 'APPROVED') { pct = 100; cls = 'is-approved' }
  else if (s === 'REJECTED') { pct = 100; cls = 'is-rejected'; label3 = '반려' }
  const klass = `flow-status${compact ? ' compact' : ''} ${cls}`.trim()
  return (
    <div className={klass} title={`진행률 ${pct}%`}>
      <div className="track" aria-hidden="true">
        <div className="bar" style={{ width: `${pct}%` }}></div>
      </div>
      <div className="marks" aria-hidden="true">
        <span className={`mark ${pct >= 0 ? 'done' : ''}`}>기안</span>
        <span className={`mark ${pct >= 50 ? (pct < 100 ? 'active' : 'done') : ''}`}>검토</span>
        <span className={`mark ${pct >= 100 ? (cls === 'is-rejected' ? 'reject' : 'done') : ''}`}>{label3}</span>
      </div>
      <span className="sr-only">{`현재 상태: ${s}`}</span>
    </div>
  )
}
