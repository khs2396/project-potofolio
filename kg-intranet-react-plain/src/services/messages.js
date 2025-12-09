const KEY = 'kg_messages'
const EVENT = 'kg-messages-changed'

const hasWindow = () => typeof window !== 'undefined'

const DEFAULT_MESSAGES = [
  {
    id: 501,
    subject: '프로필 업데이트 요청',
    body: '안녕하세요. 사내 디렉터리 최신화를 위해 프로필 정보를 한 번 더 확인 부탁드립니다.',
    sender: 'people@kg.co',
    receiver: 'haneul@kg.co',
    read: false,
    createdAt: '2025-11-10T09:10:00.000Z'
  },
  {
    id: 502,
    subject: '디자인 가이드 공유',
    body: '새로운 디자인 가이드를 문서함에도 올려두었습니다. 검토 후 의견 주시면 감사하겠습니다.',
    sender: 'yuri@kg.co',
    receiver: 'haneul@kg.co',
    read: true,
    createdAt: '2025-11-09T07:32:00.000Z'
  },
  {
    id: 503,
    subject: '점심 미팅 일정',
    body: '오늘 12시 30분에 7층 라운지에서 간단한 점심 미팅 진행하려고 합니다. 가능하신가요?',
    sender: 'jimin@kg.co',
    receiver: 'haneul@kg.co',
    read: false,
    createdAt: '2025-11-09T01:20:00.000Z'
  }
]

function emitChange() {
  if (!hasWindow()) return
  try {
    window.dispatchEvent(new CustomEvent(EVENT))
  } catch (e) { console.error('메시지 변경 이벤트 실패:', e) }
}

function readStore() {
  if (!hasWindow()) return DEFAULT_MESSAGES
  try {
    const raw = localStorage.getItem(KEY)
    if (!raw) {
      localStorage.setItem(KEY, JSON.stringify(DEFAULT_MESSAGES))
      return DEFAULT_MESSAGES
    }
    const parsed = JSON.parse(raw)
    if (!Array.isArray(parsed)) throw new Error('invalid messages')
    return parsed
  } catch {
    localStorage.setItem(KEY, JSON.stringify(DEFAULT_MESSAGES))
    return DEFAULT_MESSAGES
  }
}

function writeStore(list) {
  if (!hasWindow()) return
  try {
    localStorage.setItem(KEY, JSON.stringify(list))
    emitChange()
  } catch (e) { console.error('메시지 저장 실패:', e) }
}

export function list() {
  return readStore().slice().sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
}

export function getById(id) {
  return readStore().find(m => String(m.id) === String(id)) || null
}

export function send({ sender, receiver, subject, body }) {
  const trimmedBody = String(body || '').trim()
  const trimmedSubject = String(subject || '').trim() || '(제목 없음)'
  if (!receiver) throw new Error('수신자를 선택해 주세요.')
  if (!trimmedBody) throw new Error('내용을 입력해 주세요.')
  const list = readStore()
  const message = {
    id: Date.now(),
    sender: sender || 'unknown@kg.co',
    receiver,
    subject: trimmedSubject,
    body: trimmedBody,
    read: false,
    createdAt: new Date().toISOString()
  }
  list.unshift(message)
  writeStore(list)
  return message
}

export function markRead(id) {
  const list = readStore()
  const idx = list.findIndex(m => String(m.id) === String(id))
  if (idx === -1) return null
  list[idx] = { ...list[idx], read: true }
  writeStore(list)
  return list[idx]
}

export function remove(id) {
  const list = readStore()
  const next = list.filter(m => String(m.id) !== String(id))
  writeStore(next)
}

export function subscribe(fn) {
  if (!hasWindow() || typeof fn !== 'function') return () => {}
  const handler = () => fn()
  window.addEventListener(EVENT, handler)
  return () => window.removeEventListener(EVENT, handler)
}
