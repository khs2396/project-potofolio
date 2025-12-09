// Simple localStorage helpers
export const load = (key, fallback=[]) => {
  try { const raw = localStorage.getItem(key); return raw ? JSON.parse(raw) : fallback } catch { return fallback }
}
export const save = (key, value) => {
  try { localStorage.setItem(key, JSON.stringify(value)) } catch (e) { console.error('저장 실패:', key, e) }
}
export const unshiftItem = (key, item) => {
  const arr = load(key, []); arr.unshift(item); save(key, arr); return arr
}
export const findById = (key, id) => load(key, []).find(it => String(it.id) === String(id))
