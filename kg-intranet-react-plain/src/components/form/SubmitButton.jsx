export default function SubmitButton({ loading, children }) {
  return (
    <button type="submit" className="btn primary" disabled={loading}>
      {loading ? '처리 중...' : children}
    </button>
  )
}
