import { apiClient } from './apiClient.js'

const EVENT = 'kg-announcements-changed'
let cache = []
let loaded = false

const hasWindow = () => typeof window !== 'undefined'

const emitChange = () => {
  if (!hasWindow()) return
  try { window.dispatchEvent(new CustomEvent(EVENT, { detail: cache })) } catch (e) { console.error('이벤트 발생 실패:', e) }
}

function formatDate(value) {
  if (!value) return ''
  const d = typeof value === 'string' ? new Date(value) : value
  if (Number.isNaN(d.getTime())) return ''
  return d.toISOString().slice(0, 10)
}

function normalizeNotice(n) {
  return {
    id: n.seq ?? n.id,
    seq: n.seq ?? n.id,
    title: n.subject || n.title || '',
    dept: n.depName || n.dept || '',
    author: n.authorName || n.name || n.email || '관리자',
    date: formatDate(n.logtime || n.date),
    views: n.hit ?? n.views ?? 0,
    body: n.content || n.body || '',
  }
}

function upsertCache(item) {
  const idx = cache.findIndex(x => String(x.id) === String(item.id))
  if (idx >= 0) cache[idx] = item
  else cache.unshift(item)
}

export async function fetchList({ page = 1, size = 50 } = {}) {
  const res = await apiClient.get(`/notices?pg=${page}&size=${size}`)
  const items = Array.isArray(res?.items) ? res.items.map(normalizeNotice) : []
  cache = items
  loaded = true
  emitChange()
  return cache
}

export async function fetchById(id) {
  const res = await apiClient.get(`/notices/${id}`)
  if (res?.rt !== 'viewOK') throw new Error(res?.msg || '공지 조회에 실패했습니다.')
  const item = normalizeNotice(res.item || res)
  upsertCache(item)
  emitChange()
  return item
}

export async function create({ title, body }) {
  const payload = { subject: title, content: body }
  const res = await apiClient.post('/notices', payload)
  if (res?.rt !== 'writeOK') throw new Error(res?.msg || '공지 등록에 실패했습니다.')
  const item = normalizeNotice(res.item || res)
  upsertCache(item)
  emitChange()
  return item
}

export async function bumpHit(id) {
  if (!id) return null
  const res = await apiClient.post(`/notices/${id}/hit`)
  const item = normalizeNotice(res?.item || res)
  upsertCache(item)
  emitChange()
  return item
}

export function list() {
  return cache
}

export function getById(id) {
  return cache.find(a => String(a.id) === String(id)) || null
}

export function subscribe(fn) {
  if (!hasWindow() || typeof fn !== 'function') return () => {}
  const handler = (e) => fn(e.detail ?? cache)
  window.addEventListener(EVENT, handler)
  return () => window.removeEventListener(EVENT, handler)
}

export function ensureLoaded() {
  return loaded ? Promise.resolve(cache) : fetchList()
}
