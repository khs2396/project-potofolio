export default function Table({ children, cols, className = '', wrapClassName = '' }) {
  return (
    <div className={`table-wrap ${wrapClassName}`}>
      <table className={`table ${className}`}>
        {Array.isArray(cols) && cols.length > 0 && (
          <colgroup>
            {cols.map((w, i) => (
              <col key={i} style={w ? { width: typeof w === 'number' ? `${w}px` : String(w) } : {}} />
            ))}
          </colgroup>
        )}
        {children}
      </table>
    </div>
  )
}
