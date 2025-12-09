import { APPROVALS } from '../data/approvals.js'
import { apiClient, API_BASE_URL } from './apiClient.js'
import { load, save, unshiftItem } from './storage.js'

const KEY = 'kg_approvals_local'
const OVERRIDE = 'kg_approvals_state'
const HIST = 'kg_approvals_history'
const EVENT = 'kg-approvals-changed'
const API_ENABLED = Boolean(API_BASE_URL)

const hasWindow = () => typeof window !== 'undefined'

const emitChange = () => {
  if (!hasWindow()) return
  try { window.dispatchEvent(new CustomEvent(EVENT)) } catch (e) { console.error('이벤트 발생 실패:', e) }
}

if (hasWindow() && !window.__kgApprovalsStorageHooked) {
  window.__kgApprovalsStorageHooked = true
  window.addEventListener('storage', (e) => {
    if (!e || !e.key) return
    if ([KEY, OVERRIDE, HIST].includes(e.key)) emitChange()
  })
}

const normalizeApprovers = (approvers = []) =>
  approvers.map((a, idx) => {
    const dept =
      a.dept ||
      a.deptName ||
      a.departmentName ||
      (a.department && (a.department.name || a.department.deptName)) ||
      ''
    return {
      id: a.id ?? idx,
      name: a.name || `결재자${idx + 1}`,
      email: a.email || '',
      dept,
      status: a.status || 'PENDING'
    }
  })

const mergedListLocal = () => {
  const loc = load(KEY, [])
  const base = [...APPROVALS.map(a => ({ ...a })), ...loc]
  const overrides = load(OVERRIDE, {})
  base.forEach(a => { if (overrides[a.id]) a.state = overrides[a.id] })
  return base
}

const mapApproval = (raw) => {
  // 백엔드 Approval_request 엔티티 응답 매핑
  const approvalSeq = raw?.approvalSeq ?? raw?.approvalid ?? raw?.approvalId ?? raw?.id ?? Date.now()
  const requesterId = raw?.requesterId?.empNo ?? raw?.requesterId ?? raw?.requester_id ?? raw?.requesterid ?? ''
  const approverId = raw?.approver?.empNo ?? raw?.approver ?? null
  const taskSeq = raw?.task?.taskSeq ?? raw?.task_seq ?? raw?.taskId ?? null
  
  return {
    id: approvalSeq,
    approvalSeq: approvalSeq,
    title: raw?.subject || raw?.title || '제목 없음',
    body: raw?.approvalContent ?? raw?.approval_content ?? raw?.body ?? '',
    requester: raw?.requesterId?.name ?? raw?.requester ?? '',
    requesterId: requesterId,
    approverId: approverId,
    approver: raw?.approver?.name ?? null,
    taskId: taskSeq,
    taskSeq: taskSeq,
    date: raw?.approvalRequestedAt ? String(raw.approvalRequestedAt).slice(0, 10) : (raw?.logtime ? String(raw.logtime).slice(0, 10) : new Date().toISOString().slice(0, 10)),
    fileName: raw?.fileName ?? raw?.file_name ?? '',
    approvers: normalizeApprovers(
      raw?.approvers ??
      (approverId ? [{
        id: approverId,
        name: raw?.approver?.name ?? '결재자',
        dept: raw?.approver?.dept || raw?.approver?.deptName || raw?.approver?.departmentName || (raw?.approver?.department && (raw.approver.department.name || raw.approver.department.deptName)) || ''
      }] : [])
    ),
    files: raw?.files || (raw?.fileName ? [{ name: raw.fileName }] : []),
    state: raw?.state || 'IN_REVIEW'
  }
}

const mapHistory = (raw) => {
  // 백엔드 Task_history 엔티티 응답 매핑
  const statusId = raw?.status?.statusseq ?? raw?.status?.statusId ?? raw?.statusId
  const statusName = raw?.status?.statusname ?? raw?.status?.statusName ?? ''
  let action = 'UPDATED'
  if (statusId === 3) action = 'APPROVED'
  if (statusId === 4) action = 'REJECTED'
  
  return {
    id: raw?.historySeq ?? raw?.historyid ?? raw?.id ?? Date.now(),
    action,
    statusId,
    statusName,
    approver: raw?.approver?.empNo ?? raw?.approver ?? null,
    approverName: raw?.approver?.name ?? null,
    approvalId: raw?.approval?.approvalSeq ?? raw?.approvalid ?? raw?.approvalId ?? null,
    at: raw?.approvalCompletedAt ? new Date(raw.approvalCompletedAt).toISOString() : (raw?.logtime ? new Date(raw.logtime).toISOString() : new Date().toISOString())
  }
}

const isNetworkError = (err) =>
  err && (!('status' in err) || err.status === 0 || err.name === 'TypeError' || /Failed to fetch/i.test(err.message || ''))

async function listBackend() {
  const data = await apiClient.get('/approvals')
  return Array.isArray(data) ? data.map(mapApproval) : []
}

async function getByIdBackend(id) {
  const data = await apiClient.get(`/approvals/${id}`)
  return data ? mapApproval(data) : null
}

async function createBackend(data) {
  if (!API_ENABLED) return null
  try {
    const formData = new FormData()
    formData.append('subject', data.subject || '제목 없음')
    formData.append('approval_content', data.approval_content || '')
    formData.append('task_seq', data.task_seq ?? 0)
    formData.append('requester_id', data.requester_id ?? 0)
    if (data.approver) formData.append('approver', data.approver)
    if (data.file instanceof File) formData.append('file2', data.file)

    const res = await apiClient.post('/approvals', formData)

    // 백엔드 에러 응답 체크
    if (res?.rt === 'FAIL') {
      throw new Error(res.message || '결재 생성에 실패했습니다.')
    }

    const payload = res?.item ?? res
    return payload ? mapApproval(payload) : null
  } catch (err) {
    if (!isNetworkError(err)) throw err
    return null
  }
}

const createMock = ({ subject, approval_content, requester_id, task_seq, file_name, approver }) => {
  const item = {
    id: Date.now(),
    subject: subject || '제목 없음',
    approval_content: approval_content || '',
    requester_id,
    approver_id: approver || null,
    task_seq: task_seq ?? null,
    file_name: file_name || '',
    state: 'IN_REVIEW'
  }
  unshiftItem(KEY, item)
  pushHist({ id: item.id, action: 'CREATED' })
  emitChange()
  return item
}

async function approveBackend(id, statusId = 3) {
  if (!API_ENABLED) return null
  const endpoint = statusId === 3 ? `/approvals/${id}/approve` : `/approvals/${id}/reject`
  const res = await apiClient.put(endpoint, {})
  return res
}

async function historyBackend(id) {
  const data = await apiClient.get(`/approval-history?approvalId=${id}`)
  return Array.isArray(data) ? data.map(mapHistory) : []
}

function legacyOverride(id, state) {
  const m = load(OVERRIDE, {})
  m[id] = state
  save(OVERRIDE, m)
  pushHist({ id, action: state })
  emitChange()
  return getById(id)
}

export function overrideState(id, state) {
  return legacyOverride(id, state)
}

function pushHist(rec) {
  try {
    const a = load(HIST, [])
    a.push({ ...rec, at: new Date().toISOString() })
    save(HIST, a)
  } catch (e) { console.error('히스토리 저장 실패:', e) }
}

export async function list() {
  if (!API_ENABLED) return mergedListLocal()
  try {
    return await listBackend()
  } catch (err) {
    if (!isNetworkError(err)) throw err
    return mergedListLocal()
  }
}

export async function getById(id) {
  if (!API_ENABLED) return mergedListLocal().find(a => String(a.id) === String(id)) || null
  try {
    return await getByIdBackend(id)
  } catch (err) {
    if (!isNetworkError(err)) throw err
    return mergedListLocal().find(a => String(a.id) === String(id)) || null
  }
}

export async function create({ subject, approval_content, requester_id, task_seq, file_name, approver, file }) {
  if (!API_ENABLED) return createMock({ subject, approval_content, requester_id, task_seq, file_name, approver })
  try {
    const created = await createBackend({ subject, approval_content, requester_id, task_seq, file_name, approver, file })
    emitChange()
    return created
  } catch (err) {
    if (!isNetworkError(err)) throw err
    return createMock({ subject, approval_content, requester_id, task_seq, file_name, approver })
  }
}

export async function approveStep(id) {
  if (!API_ENABLED) return legacyOverride(id, 'APPROVED')
  try {
    await approveBackend(id, 3)
    emitChange()
    return getById(id)
  } catch (err) {
    if (!isNetworkError(err)) throw err
    return legacyOverride(id, 'APPROVED')
  }
}

export async function rejectAll(id) {
  if (!API_ENABLED) return legacyOverride(id, 'REJECTED')
  try {
    await approveBackend(id, 4)
    emitChange()
    return getById(id)
  } catch (err) {
    if (!isNetworkError(err)) throw err
    return legacyOverride(id, 'REJECTED')
  }
}

export async function history(id) {
  if (!API_ENABLED) {
    const all = load(HIST, [])
    if (typeof id === 'undefined') return all
    return all.filter(r => String(r.id) === String(id))
  }
  try {
    const hist = await historyBackend(id)
    return hist
  } catch (err) {
    if (!isNetworkError(err)) throw err
    const all = load(HIST, [])
    if (typeof id === 'undefined') return all
    return all.filter(r => String(r.id) === String(id))
  }
}

export const subscribe = (fn) => {
  if (!hasWindow() || typeof fn !== 'function') return () => {}
  const handler = () => fn()
  window.addEventListener(EVENT, handler)
  return () => window.removeEventListener(EVENT, handler)
}

// 기존 특수 조회 함수들 (필요 시 사용)
export async function getapproverList() {
  const user = readCurrentUser()
  const depno = user?.depno
  const qs = new URLSearchParams()
  if (depno) qs.set('approver_id', depno)
  const url = qs.toString() ? `/approvalList?${qs.toString()}` : '/approvalList'
  try {
    const res = await apiClient.get(url)
    return Array.isArray(res) ? res : []
  } catch (err) {
    if (!isNetworkError(err)) throw err
    return []
  }
}

export async function getapproverView(id) {
  try {
    const res = await apiClient.get(`/approvalView?approvalseq=${id}`)
    return res || null
  } catch (err) {
    if (!isNetworkError(err)) throw err
    return null
  }
}

// helpers
const readCurrentUser = () => {
  if (!hasWindow()) return null
  try { return JSON.parse(localStorage.getItem('kg_auth_user') || 'null') } catch { return null }
}
