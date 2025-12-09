export default function Pagination({ page, total, onChange }) {
  const pages = Array.from({ length: total }, (_, i) => i + 1)
  return <nav className="pagination" aria-label="페이지네이션">
    {pages.map(p => <button key={p} className={p===page?'page-btn active':'page-btn'} onClick={()=>onChange(p)}>{p}</button>)}
  </nav>
}
