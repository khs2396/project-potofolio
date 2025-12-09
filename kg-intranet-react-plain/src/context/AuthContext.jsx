import { createContext, useCallback, useContext, useEffect, useState } from 'react'

const KEY = 'kg_auth_user'
const AuthContext = createContext(null)

function readUser() {
  try { return JSON.parse(localStorage.getItem(KEY) || 'null') } catch (e) { console.error('사용자 정보 읽기 실패:', e); return null }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readUser)

  const signIn = useCallback((u) => {
    setUser(u)
    try { localStorage.setItem(KEY, JSON.stringify(u)) } catch (e) { console.error('인증 정보 저장 실패:', e) }
    // 같은 탭에서도 듣게 커스텀 이벤트 발행
    window.dispatchEvent(new CustomEvent('kg-auth-changed', { detail: u }))
  }, [])

  const signOut = useCallback(() => {
    setUser(null)
    try { localStorage.removeItem(KEY) } catch (e) { console.error('인증 정보 삭제 실패:', e) }
    window.dispatchEvent(new CustomEvent('kg-auth-changed', { detail: null }))
  }, [])

  useEffect(() => {
    const onStorage = (e) => { if (e.key === KEY) setUser(readUser()) }
    const onCustom = (e) => setUser(e.detail ?? readUser())
    window.addEventListener('storage', onStorage)
    window.addEventListener('kg-auth-changed', onCustom)
    return () => {
      window.removeEventListener('storage', onStorage)
      window.removeEventListener('kg-auth-changed', onCustom)
    }
  }, [])

  return (
    <AuthContext.Provider value={{ user, signIn, signOut }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
