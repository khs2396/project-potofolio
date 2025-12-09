import { createContext, useContext, useEffect, useMemo, useState, useCallback } from 'react'
import { useAuth } from './AuthContext.jsx'
import * as MessageService from '../services/messages.js'

const MessagesContext = createContext(null)

export function MessagesProvider({ children }) {
  const { user } = useAuth()
  const [messages, setMessages] = useState(() => MessageService.list())
  const [panelState, setPanelState] = useState({ open: false, mode: 'list', focusId: null, composeTo: '' })

  useEffect(() => {
    const sync = () => setMessages(MessageService.list())
    sync()
    const unsub = MessageService.subscribe(sync)
    return () => unsub()
  }, [])

  const inbox = useMemo(() => {
    if (!user?.email) return []
    const email = user.email.toLowerCase()
    return messages.filter(m => (m.receiver || '').toLowerCase() === email)
  }, [messages, user])

  const sent = useMemo(() => {
    if (!user?.email) return []
    const email = user.email.toLowerCase()
    return messages.filter(m => (m.sender || '').toLowerCase() === email)
  }, [messages, user])

  const unreadCount = useMemo(() => inbox.filter(m => !m.read).length, [inbox])

  const openPanel = useCallback((nextState = {}) => {
    setPanelState(prev => ({
      open: true,
      mode: nextState.mode || prev.mode || 'list',
      focusId: nextState.focusId ?? prev.focusId ?? null,
      composeTo: nextState.composeTo ?? prev.composeTo ?? ''
    }))
  }, [])

  const closePanel = useCallback(() => {
    setPanelState(prev => ({ ...prev, open: false }))
  }, [])

  const showMessage = useCallback((id) => {
    setPanelState({ open: true, mode: 'detail', focusId: id })
    MessageService.markRead(id)
  }, [])

  const composeMessage = useCallback((initial = {}) => {
    setPanelState({ open: true, mode: 'compose', focusId: null, composeTo: initial.to || '' })
  }, [])

  const sendMessage = useCallback(async ({ receiver, subject, body }) => {
    if (!user?.email) throw new Error('로그인 후 이용해 주세요.')
    return MessageService.send({
      sender: user.email,
      receiver,
      subject,
      body
    })
  }, [user])

  const markRead = useCallback((id) => {
    MessageService.markRead(id)
  }, [])

  const value = {
    messages,
    inbox,
    sent,
    unreadCount,
    panelState,
    openPanel,
    closePanel,
    showMessage,
    composeMessage,
    sendMessage,
    markRead
  }

  return (
    <MessagesContext.Provider value={value}>
      {children}
    </MessagesContext.Provider>
  )
}

export const useMessages = () => useContext(MessagesContext)
