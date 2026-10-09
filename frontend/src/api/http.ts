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

  /** Field-level validation messages, keyed by field name. */
  get fieldErrors(): Record<string, string> {
    return this.problem.errors ?? {}
  }
}

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''

async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  const response = await fetch(BASE_URL + path, {
    method,
    headers: body === undefined ? undefined : { 'Content-Type': 'application/json' },
    body: body === undefined ? undefined : JSON.stringify(body),
  })

  if (!response.ok) {
    const problem = await response
      .json()
      .catch(() => ({ status: response.status, title: response.statusText }))
    throw new ApiError({ ...problem, status: response.status })
  }

  if (response.status === 204) {
    return undefined as T
  }
  return (await response.json()) as T
}

export const http = {
  get: <T>(path: string) => request<T>('GET', path),
  post: <T>(path: string, body?: unknown) => request<T>('POST', path, body),
  put: <T>(path: string, body?: unknown) => request<T>('PUT', path, body),
  patch: <T>(path: string, body?: unknown) => request<T>('PATCH', path, body),
  delete: <T = void>(path: string) => request<T>('DELETE', path),
}
