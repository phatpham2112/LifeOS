import { afterEach, describe, expect, it, vi } from 'vitest'
import { ApiError, http } from '../http'

function response(status: number, body?: unknown) {
  return new Response(body === undefined ? null : JSON.stringify(body), { status })
}
function mutation(status: number, body?: unknown) {
  return vi
    .spyOn(globalThis, 'fetch')
    .mockResolvedValueOnce(response(200, { headerName: 'X-CSRF-TOKEN', token: 'fresh-token' }))
    .mockResolvedValueOnce(response(status, body))
}

describe('http', () => {
  afterEach(() => vi.restoreAllMocks())

  it('returns parsed JSON on success', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(response(200, { id: 1 }))
    await expect(http.get('/api/things/1')).resolves.toEqual({ id: 1 })
  })
  it('sends JSON with session credentials and a CSRF token', async () => {
    const fetch = mutation(201, { id: 1 })
    await http.post('/api/things', { title: 'a' })
    expect(fetch).toHaveBeenNthCalledWith(2, '/api/things', {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json', 'X-CSRF-TOKEN': 'fresh-token' },
      body: '{"title":"a"}',
    })
  })
  it('encodes login credentials as form data', async () => {
    const fetch = mutation(204)
    await http.post(
      '/api/auth/login',
      new URLSearchParams({ username: 'alice', password: 'a&b=12345' }),
    )
    expect(fetch.mock.calls[1]?.[1]?.body).toBe('username=alice&password=a%26b%3D12345')
  })
  it('returns undefined on 204', async () => {
    mutation(204)
    await expect(http.delete('/api/things/1')).resolves.toBeUndefined()
  })
  it('throws field errors from ProblemDetail', async () => {
    mutation(400, { detail: 'Validation failed', errors: { title: 'must not be blank' } })
    const error = await http.post('/api/things', {}).catch((e: unknown) => e)
    expect(error).toBeInstanceOf(ApiError)
    expect((error as ApiError).status).toBe(400)
    expect((error as ApiError).fieldErrors).toEqual({ title: 'must not be blank' })
  })
  it('notifies the app when a session expires', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(response(401, { detail: 'Login required' }))
    const listener = vi.fn<() => void>()
    window.addEventListener('auth-expired', listener, { once: true })
    await expect(http.get('/api/auth/me')).rejects.toBeInstanceOf(ApiError)
    expect(listener).toHaveBeenCalledOnce()
  })
  it('does not mutate when CSRF retrieval fails', async () => {
    const fetch = vi.spyOn(globalThis, 'fetch').mockResolvedValue(response(500, {}))
    await expect(http.delete('/api/tasks/1')).rejects.toBeInstanceOf(ApiError)
    expect(fetch).toHaveBeenCalledTimes(1)
  })
})
