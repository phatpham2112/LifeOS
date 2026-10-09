/** Error body returned by the backend (RFC 9457 ProblemDetail). */
export interface ProblemDetail {
  type?: string
  title?: string
  status: number
  detail?: string
  instance?: string
  errors?: Record<string, string>
}

export class ApiError extends Error {
  constructor(readonly problem: ProblemDetail) {
    super(problem.detail ?? problem.title ?? `Request failed with status ${problem.status}`)
    this.name = 'ApiError'
  }
  get status(): number {
    return this.problem.status
  }
  get fieldErrors(): Record<string, string> {
    return this.problem.errors ?? {}
  }
}

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''

async function read<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const problem = await response.json().catch(() => ({ title: response.statusText }))
    if (response.status === 401) window.dispatchEvent(new Event('auth-expired'))
    throw new ApiError({ ...problem, status: response.status })
  }
  return response.status === 204 ? (undefined as T) : ((await response.json()) as T)
}

async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  const headers: Record<string, string> = {}
  // Fetch the current token for each mutation: login/logout rotate the session's CSRF token.
  if (method !== 'GET') {
    const csrf = await read<{ headerName: string; token: string }>(
      await fetch(BASE_URL + '/api/auth/csrf', { credentials: 'include' }),
    )
    headers[csrf.headerName] = csrf.token
  }
  const isForm = body instanceof URLSearchParams
  if (body !== undefined)
    headers['Content-Type'] = isForm ? 'application/x-www-form-urlencoded' : 'application/json'
  return read<T>(
    await fetch(BASE_URL + path, {
      method,
      credentials: 'include',
      headers,
      body: body === undefined ? undefined : isForm ? body.toString() : JSON.stringify(body),
    }),
  )
}

export const http = {
  get: <T>(path: string) => request<T>('GET', path),
  post: <T>(path: string, body?: unknown) => request<T>('POST', path, body),
  put: <T>(path: string, body?: unknown) => request<T>('PUT', path, body),
  patch: <T>(path: string, body?: unknown) => request<T>('PATCH', path, body),
  delete: <T = void>(path: string) => request<T>('DELETE', path),
}

export function errorMessage(error: unknown): string {
  if (error instanceof ApiError) {
    const fields = Object.values(error.fieldErrors)
    return fields.length ? fields.join(' ') : error.message
  }
  return 'Không thể kết nối máy chủ. Vui lòng thử lại.'
}
