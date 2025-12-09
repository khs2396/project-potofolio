import { apiClient, API_BASE_URL } from './apiClient.js'

const API_ENABLED = Boolean(API_BASE_URL)

const isNetworkError = (err) =>
  err && (!('status' in err) || err.status === 0 || err.name === 'TypeError' || /Failed to fetch/i.test(err.message || ''))

const mapHistory = (raw = {}) => {
  // 백엔드 Task_history 엔티티 응답 매핑
  const statusId = raw?.status?.statusseq ?? raw?.status?.statusId ?? raw?.statusId
  const statusName = raw?.status?.statusname ?? raw?.status?.statusName ?? ''
  let action = 'UPDATED'
  if (statusId === 3) action = 'APPROVED'
  if (statusId === 4) action = 'REJECTED'
  if (statusId === 2) action = 'IN_PROGRESS'
  
  return {
    id: raw?.historySeq ?? raw?.historyid ?? raw?.historyId ?? raw?.id ?? Date.now(),
    approver: raw?.approver?.empNo ?? raw?.approver ?? null,
    approverName: raw?.approver?.name ?? null,
    approvalId: raw?.approval?.approvalSeq ?? raw?.approvalid ?? raw?.approvalId ?? null,
    statusId,
    statusName: statusName || (statusId === 3 ? '승인' : statusId === 4 ? '반려' : statusId === 2 ? '진행중' : ''),
    action,
    logtime: raw?.approvalCompletedAt ? String(raw.approvalCompletedAt) : (raw?.logtime ? String(raw.logtime) : '')
  }
}

async function get(url) {
  if (!API_ENABLED) return []
  try {
    const data = await apiClient.get(url)
    if (Array.isArray(data)) return data.map(mapHistory)
    return []
  } catch (err) {
    if (!isNetworkError(err)) throw err
    return []
  }
}

export const inbox = async ({ approverId, statusId } = {}) => {
  const params = new URLSearchParams()
  if (approverId) params.set('approverId', approverId)
  if (statusId) params.set('statusId', statusId)
  return get(`/approval-history/inbox?${params.toString()}`)
}

export const outbox = async ({ requesterId, statusId } = {}) => {
  const params = new URLSearchParams()
  if (requesterId) params.set('requesterId', requesterId)
  if (statusId) params.set('statusId', statusId)
  return get(`/approval-history/outbox?${params.toString()}`)
}

export const resubmit = async ({ approvalId, requesterId }) => {
  if (!API_ENABLED) return null
  return apiClient.post('/approval-history/resubmit', { approvalId, requesterId })
}

export const latestByApproval = (records = []) => {
  const map = new Map()
  records.forEach(r => {
    const key = r.approvalId
    const prev = map.get(key)
    const time = new Date(r.logtime || r.at || 0).getTime()
    if (!prev || time > (prev._time || 0)) {
      map.set(key, { ...r, _time: time })
    }
  })
  return Array.from(map.values())
}
