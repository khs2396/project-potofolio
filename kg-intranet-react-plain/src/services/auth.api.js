import { notifyChange as notifyEmployeesChange } from './employees.js'
import { apiClient, API_BASE_URL } from './apiClient.js'

const KEY_USER = 'kg_auth_user'
const KEY_EMP_LOCAL = 'kg_employees_local'
const API_ENABLED = Boolean(API_BASE_URL)

const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms))

function emitUserChange(payload) {
  try {
    window.dispatchEvent(new StorageEvent('storage', { key: KEY_USER, newValue: JSON.stringify(payload) }))
  } catch (e) { console.error('사용자 변경 이벤트 발생 실패:', e) }
}

function normalizeEmail(email) {
  return String(email || '').trim().toLowerCase()
}

function readCurrentUser() {
  try { return JSON.parse(localStorage.getItem(KEY_USER) || 'null') } catch (e) { console.error('사용자 정보 읽기 실패:', e); return null }
}

function persistUser(user) {
  try { localStorage.setItem(KEY_USER, JSON.stringify(user)) } catch (e) { console.error('사용자 정보 저장 실패:', e) }
  emitUserChange(user)
  upsertDirectory(user)
}

function clearPersistedUser() {
  try { localStorage.removeItem(KEY_USER) } catch (e) { console.error('사용자 정보 삭제 실패:', e) }
  emitUserChange(null)
}

function upsertDirectory(user) {
  let list = []
  try { list = JSON.parse(localStorage.getItem(KEY_EMP_LOCAL) || '[]') } catch (e) { console.error('디렉터리 목록 읽기 실패:', e) }
  const idx = list.findIndex(entry => normalizeEmail(entry.email) === normalizeEmail(user.email))
  const record = {
    id: idx >= 0 ? list[idx].id : Date.now(),
    name: user.name || 'User',
    email: user.email,
    dept: user.dept || list[idx]?.dept || 'Department',
    title: user.title || list[idx]?.title || 'Member',
    phone: user.phone || list[idx]?.phone || '',
    location: user.location || list[idx]?.location || '',
    status: user.status || list[idx]?.status || 'ACTIVE',
    avatar: user.avatar || list[idx]?.avatar || '',
  }
  if (idx >= 0) list[idx] = { ...list[idx], ...record }
  else list.push(record)
  localStorage.setItem(KEY_EMP_LOCAL, JSON.stringify(list))
  notifyEmployeesChange()
}

async function loginViaBackend({ email, password }) {
  const em = normalizeEmail(email)
  if (!em || !password) throw new Error('이메일과 비밀번호를 입력하세요.')
  const response = await apiClient.post('/login', { email: em, pw: password })
  if (response?.rt !== 'loginOK') throw new Error('이메일 또는 비밀번호가 올바르지 않습니다.')
  const emp = response?.employee || response?.item || {}
  const joinDate = emp.joinTime || emp.join_time || emp.hireDate || emp.joinDate || emp.joinedAt || ''
  const user = {
    id: emp.empNo ?? emp.id,
    empNo: emp.empNo ?? emp.id,
    email: emp.email || em,
    name: emp.name || em.split('@')[0] || 'User',
    depId: emp.depId,
    dept: emp.depName || emp.dept,
    title: emp.title || emp.roleName || emp.role || 'Member',
    role: emp.roleName || emp.role || 'USER',
    phone: emp.tel || emp.phone || '',
    status: emp.status || 'ACTIVE',
    joinedAt: joinDate,
    hireDate: joinDate,
    joinDate: joinDate,
  }
  persistUser(user)
  return { user }
}

async function signupViaBackend({ email, password, name, agree, phone, depId, roleId }) {
  const em = normalizeEmail(email)
  if (!em || !password || !name || !agree) throw new Error('필수 입력값을 확인해주세요.')
  const payload = {
    email: em,
    pw: password,
    name,
    tel: (phone || '').trim(),
    dep_id: depId,
    role_id: roleId,
  }
  const response = await apiClient.post('/employeeSave', payload)
  if (response.rt !== 'OK') throw new Error('회원가입 처리 중 문제가 발생했습니다.')
  const user = { email: em, name, phone: payload.tel, dep_id: depId, role_id: roleId }
  upsertDirectory(user)
  return { user }
}

// --- Public API (백엔드 사용) ---

export async function login(payload) {
  if (!API_ENABLED) throw new Error('API_BASE_URL이 설정되지 않았습니다. 백엔드 연결을 확인하세요.')
  return loginViaBackend(payload)
}

export async function signup(payload) {
  if (!API_ENABLED) throw new Error('API_BASE_URL이 설정되지 않았습니다. 백엔드 연결을 확인하세요.')
  return signupViaBackend(payload)
}

export async function me() {
  // 현재는 로컬에 저장된 세션 정보를 사용
  await delay(50)
  return readCurrentUser()
}

export async function logout() {
  try { await apiClient.post('/logout') } catch (e) { console.warn('로그아웃 API 호출 실패 (무시됨):', e) }
  await delay(50)
  clearPersistedUser()
}

export async function emailAvailable() {
  // 백엔드에 이메일 중복 확인 API가 없어서 항상 true 반환
  return true
}

// 이메일 인증 코드 발송
export async function requestEmailCode(email) {
  if (!API_ENABLED) throw new Error('API_BASE_URL이 설정되지 않았습니다.')
  return apiClient.post('/auth/email-code', { email })
}

// 이메일 인증 코드 검증
export async function verifyEmailCode(email, code) {
  if (!API_ENABLED) throw new Error('API_BASE_URL이 설정되지 않았습니다.')
  return apiClient.post('/auth/email-verify', { email, code })
}

// 비밀번호 찾기: 이메일/사번 확인
export async function verifyAccountForReset({ empNo, email }) {
  if (!API_ENABLED) throw new Error('API_BASE_URL이 설정되지 않았습니다.')
  const em = normalizeEmail(email)
  const res = await apiClient.get(`/checkPW?empNo=${encodeURIComponent(empNo)}&email=${encodeURIComponent(em)}`)
  if (res?.rt !== 'checkOK') throw new Error('계정을 확인할 수 없습니다.')
  return true
}

// 비밀번호 변경
export async function resetPassword({ empNo, password }) {
  if (!API_ENABLED) throw new Error('API_BASE_URL이 설정되지 않았습니다.')
  const res = await apiClient.post('/changePW', { empNo, pw: password })
  if (res?.rt !== 'changeOK') throw new Error('비밀번호 변경에 실패했습니다.')
  // 현재 로그인 사용자면 저장된 정보도 갱신
  try {
    const current = readCurrentUser()
    if (current && String(current.empNo) === String(empNo)) {
      persistUser({ ...current }) // 내용은 동일하지만 변경 이벤트를 발생시켜 세션을 갱신
    }
  } catch (e) { console.warn('비밀번호 변경 후 사용자 동기화 실패(무시):', e) }
  return true
}
