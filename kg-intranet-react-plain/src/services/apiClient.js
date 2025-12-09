const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/$/, '')

const isAbsoluteUrl = (url = '') => /^https?:\/\//i.test(url)
const isPlainObject = (value) => Object.prototype.toString.call(value) === '[object Object]'

function resolveUrl(path) {
  if (!path) return API_BASE_URL || '/'
  if (isAbsoluteUrl(path) || !API_BASE_URL) return path
  return `${API_BASE_URL}${path.startsWith('/') ? path : `/${path}`}`
}

async function request(path, options = {}) {
  const url = resolveUrl(path)
  const init = { ...options }
  init.credentials = 'include'
  const isFormData = init.body instanceof FormData

  init.headers = {
    ...(isFormData ? {} : { 'Content-Type': 'application/json' }),
    ...(options.headers || {})
  }

  if (init.body && !isFormData && isPlainObject(init.body)) {
    init.body = JSON.stringify(init.body)
  }

  const res = await fetch(url, init)
  const text = await res.text()

  let data
  try {
    data = text ? JSON.parse(text) : null
  } catch {
    data = text
  }

  if (!res.ok) {
    const err = new Error(data?.message || `Request failed (${res.status})`)
    err.status = res.status
    err.payload = data
    throw err
  }

  return data
}

const apiClient = {
  request,
  get: (path, options) => request(path, { ...(options || {}), method: 'GET' }),
  post: (path, body, options) => request(path, { ...(options || {}), method: 'POST', body }),
  put: (path, body, options) => request(path, { ...(options || {}), method: 'PUT', body }),
  patch: (path, body, options) => request(path, { ...(options || {}), method: 'PATCH', body }),
  del: (path, options) => request(path, { ...(options || {}), method: 'DELETE' })
}

export { apiClient, API_BASE_URL }
