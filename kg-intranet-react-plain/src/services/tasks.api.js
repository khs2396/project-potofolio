import { apiClient, API_BASE_URL } from './apiClient.js'

const API_ENABLED = Boolean(API_BASE_URL)

const isNetworkError = (err) =>
  err && (!('status' in err) || err.status === 0 || err.name === 'TypeError' || /Failed to fetch/i.test(err.message || ''))

export const mapTask = (raw = {}) => {
  console.log('🔍 mapTask 입력:', raw)

  // 백엔드가 TaskViewDTO로 통일되어 있으므로 단순 매핑
  const result = {
    id: raw.taskSeq || raw.id || raw.task_seq || '',
    taskSeq: raw.taskSeq || raw.id || raw.task_seq || '',
    subject: raw.subject || '',
    requestName: raw.requestName || '',
    assigneeName: raw.assigneeName || '',
    createAt: raw.createAt || '',
    taskContent: raw.taskContent || '',
    fileName: raw.fileName || raw.file_name || '',
    statusSeq: raw.statusSeq || raw.status_seq || null
  }
  console.log('🔍 mapTask 출력:', result)
  return result
}


export async function list(params = {}) {
  if (!API_ENABLED) return []
  const query = new URLSearchParams()
  if (params.assignee_id) query.set('assignee_id', params.assignee_id)
  if (params.requester_id) query.set('requester_id', params.requester_id)
  const url = query.toString() ? `/tasks?${query.toString()}` : '/tasks'
  try {
    const data = await apiClient.get(url)
    return Array.isArray(data) ? data.map(mapTask) : []
  } catch (err) {
    if (!isNetworkError(err)) throw err
    return []
  }
}

export async function getOne(taskId) {
  if (!API_ENABLED) return null
  try {
    const data = await apiClient.get(`/tasks/view?taskSeq=${taskId}`)
    return data ? mapTask(data) : null
  } catch (err) {
    if (!isNetworkError(err)) throw err
    return null
  }
}

export async function create(payload) {
  if (!API_ENABLED) return null
  try {
    const form = new FormData()
    form.append('request_id', payload.request_id ?? '')
    form.append('assignee_id', payload.assignee_id ?? '')
    form.append('subject', payload.subject || '')
    form.append('task_content', payload.task_content || '')
    if (payload.file instanceof File) {
      form.append('file', payload.file)
    }
    const res = await apiClient.post('/taskWriteForm', form)
    // /taskWriteForm returns { rt: 'OK', taskSeq: ... }
    return res
  } catch (err) {
    if (!isNetworkError(err)) throw err
    return null
  }
}
