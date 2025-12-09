import { apiClient } from './apiClient.js'

export async function listBoards(pg = 1, size = 10) {
  const res = await apiClient.get(`/boards?pg=${pg}&size=${size}`)
  return res
}

export async function getBoard(id) {
  return apiClient.get(`/boards/${id}`)
}

export async function createBoard(payload) {
  return apiClient.post('/boards', payload)
}

export async function updateBoard(id, payload) {
  return apiClient.put(`/boards/${id}`, payload)
}

export async function deleteBoard(id) {
  return apiClient.del(`/boards/${id}`)
}

export async function listComments(boardId, pg = 1) {
  return apiClient.get(`/boards/${boardId}/comments?pg=${pg}`)
}

export async function addComment(boardId, content, user) {
  const payload = { content }
  const emp = user?.empNo ?? user?.id ?? user?.depno
  if (emp) payload.empNo = emp
  return apiClient.post(`/boards/${boardId}/comments`, payload)
}

export async function deleteComment(boardId, commentId) {
  return apiClient.del(`/boards/${boardId}/comments/${commentId}`)
}


export async function updateComment(boardId, commentId, content, user) {
  const payload = { content }
  const emp = user?.empNo ?? user?.id ?? user?.depno
  if (emp) payload.empNo = emp
  return apiClient.put(`/boards/${boardId}/comments/${commentId}`, payload)
}
