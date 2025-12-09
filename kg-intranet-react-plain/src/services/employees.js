import { apiClient } from './apiClient.js'

const EVENT = 'kg-employees-changed'
const KEY_TERMINATED = 'kg_employees_terminated'
let cache = []
let loaded = false

const hasWindow = () => typeof window !== 'undefined'

const emitChange = () => {
  if (!hasWindow()) return
  try { window.dispatchEvent(new CustomEvent(EVENT, { detail: cache })) } catch (e) { console.error('직원 목록 변경 이벤트 실패:', e) }
}

export async function fetchEmployees() {
  // 직원 리스트 전체 호출
  const res = await apiClient.get('/employeeList?size=200')
  const items = Array.isArray(res?.items) ? res.items : []
  const terminated = loadTerminated()
  cache = items
    .map(normalizeEmployee)
    .filter(e => !terminated[e.empNo])
  loaded = true
  emitChange()
  return cache
}

export function list() {
  return cache
}

export function getById(id) {
  return cache.find(e => String(e.empNo ?? e.depno ?? e.id ?? e.email) === String(id)) || null
}

export async function update(payload = {}) {
  const body = { ...payload }
  if (body.phone && !body.tel) body.tel = body.phone
  // 백엔드 DTO는 depId/roleId와 dep_id/role_id 모두 받을 수 있도록 호환 필드 전송
  if (body.depId && !body.dep_id) body.dep_id = body.depId
  if (body.roleId && !body.role_id) body.role_id = body.roleId
  await apiClient.post('/employeeModify', body)
  await fetchEmployees()
  return true
}

export async function remove(empNo) {
  if (!empNo) return false
  await apiClient.get(`/employeeDelete?empNo=${empNo}`)
  cache = cache.filter(e => String(e.empNo) !== String(empNo))
  emitChange()
  return true
}

// 프론트 단에서 소프트 삭제(퇴사 처리) 저장: 로컬스토리지에 이유/일시 기록하고 목록에서 숨김
export async function terminate(empNo, reason = '') {
  if (!empNo) return false
  const terminated = loadTerminated()
  terminated[empNo] = {
    reason: reason || '',
    at: new Date().toISOString(),
  }
  saveTerminated(terminated)
  cache = cache.filter(e => String(e.empNo) !== String(empNo))
  emitChange()
  return true
}

export function subscribe(fn) {
  if (!hasWindow() || typeof fn !== 'function') return () => {}
  const handler = (e) => fn(e.detail ?? cache)
  window.addEventListener(EVENT, handler)
  return () => window.removeEventListener(EVENT, handler)
}

export function ensureLoaded() {
  return loaded ? Promise.resolve(cache) : fetchEmployees()
}

// 갱신 알림(profile.js 등에서 사용)
export function notifyChange() {
  emitChange()
}

function normalizeEmployee(e) {
  const depName = e.depName || e.dept || e.department?.depName || ''
  const roleName = e.roleName || e.title || e.role?.roleName || ''
  const empNo = e.empNo ?? e.id ?? e.depno

  return {
    id: empNo ?? e.email ?? '',
    empNo,
    depno: empNo,
    depId: e.depId ?? e.department?.depId,
    roleId: e.roleId ?? e.role?.roleId,
    name: e.name || '',
    email: e.email || '',
    dept: depName,
    title: roleName,
    phone: e.phone || e.tel || '',
    location: e.location || '',
    status: e.status || 'ACTIVE',
  }
}

function loadTerminated() {
  if (!hasWindow()) return {}
  try {
    const raw = localStorage.getItem(KEY_TERMINATED)
    return raw ? JSON.parse(raw) : {}
  } catch {
    return {}
  }
}

function saveTerminated(map) {
  if (!hasWindow()) return
  try {
    localStorage.setItem(KEY_TERMINATED, JSON.stringify(map))
  } catch { /* ignore */ }
}
