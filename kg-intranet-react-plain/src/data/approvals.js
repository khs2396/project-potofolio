export const APPROVAL_STATES = ['DRAFT', 'IN_REVIEW', 'APPROVED', 'REJECTED']

export const APPROVALS = [
  { id: 201, title: '재택 근무 신청(11/12)', state: 'IN_REVIEW', requester: '김길동', date: '2025-11-11' },
  { id: 202, title: '교육 참가 신청', state: 'APPROVED', requester: '김미희', date: '2025-11-09' },
  { id: 203, title: '장비 구매 신청(모니터)', state: 'REJECTED', requester: '이찬호', date: '2025-11-05' },
  { id: 204, title: '연차 신청(12/2~12/4)', state: 'DRAFT', requester: '박재우', date: '2025-11-02' }
]

export const FLOW = ['기안', '검토', '승인']
