import { notifyChange as notifyEmployeesChange } from './employees.js'

// Local-only profile helpers (no backend persistence beyond employees.update)
const KEY_USER = 'kg_auth_user'
const KEY_EMP_LOCAL = 'kg_employees_local'

export function loadAuthUser(){
  try { return JSON.parse(localStorage.getItem(KEY_USER) || 'null') } catch { return null }
}

export function saveAuthUser(user){
  localStorage.setItem(KEY_USER, JSON.stringify(user))
  try {
    window.dispatchEvent(new StorageEvent('storage', { key: KEY_USER, newValue: JSON.stringify(user) }))
  } catch (e) { console.error('사용자 변경 이벤트 실패:', e) }
}

export function upsertEmployeeFromProfile(profile){
  let list = []
  try { list = JSON.parse(localStorage.getItem(KEY_EMP_LOCAL) || '[]') } catch (e) { console.error('직원 목록 읽기 실패:', e) }
  const idx = list.findIndex(e => e.email && profile.email && e.email.toLowerCase()===profile.email.toLowerCase())
  const record = {
    id: idx >= 0 ? list[idx].id : Date.now(),
    name: profile.name || profile.username || '이름',
    email: profile.email,
    dept: profile.dept || list[idx]?.dept || '-',
    title: profile.title || list[idx]?.title || '-',
    phone: profile.phone || list[idx]?.phone || '',
    status: profile.status || list[idx]?.status || 'ACTIVE',
    avatar: profile.avatar || list[idx]?.avatar || '',
  }
  if(idx >= 0) list[idx] = { ...list[idx], ...record }
  else list.push(record)
  localStorage.setItem(KEY_EMP_LOCAL, JSON.stringify(list))
  notifyEmployeesChange()
}

export function loadProfile(){
  const u = loadAuthUser()
  if(!u) return null
  const base = {
    email: u.email || '',
    name: u.name || u.username || '',
    title: u.title || '',
    dept: u.dept || '',
    phone: u.phone || '',
    about: u.about || '',
    avatar: u.avatar || '',
    status: u.status || 'ACTIVE',
  }
  return base
}

export function saveProfile(p){
  // persist to auth user
  const current = loadAuthUser() || {}
  const next = { ...current, ...p }
  saveAuthUser(next)
  // sync to directory local list
  upsertEmployeeFromProfile(next)
  return next
}
