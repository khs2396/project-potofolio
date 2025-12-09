import { apiClient } from './apiClient.js'

export async function checkIn() {
  return apiClient.post('/attendance/check-in')
}

export async function checkOut() {
  return apiClient.post('/attendance/check-out')
}

export async function listMy(pg = 1, size = 10, empNo) {
  const params = new URLSearchParams({ pg, size })
  if (empNo) params.set('empNo', empNo)
  return apiClient.get(`/attendance?${params.toString()}`)
}

export async function listAll(pg = 1, size = 10) {
  return apiClient.get(`/attendance/all?pg=${pg}&size=${size}`)
}

export async function updateMemo(attSeq, memo) {
  return apiClient.patch(`/attendance/${attSeq}/memo`, { memo })
}

// HR/관리자용 근태 수정 (empNo + workdate 기준)
export async function modify(payload = {}) {
  return apiClient.post('/attendance/modify', payload)
}
