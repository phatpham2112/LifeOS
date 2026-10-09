import { afterEach, describe, expect, it, vi } from 'vitest'

import { ApiError, http } from '../http'

function mockFetch(status: number, body?: unknown) {
  const response = new Response(body === undefined ? null : JSON.stringify(body), { status })
  return vi.spyOn(globalThis, 'fetch').mockResolvedValue(response)
}

describe('http', () => {
  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('returns parsed JSON on success', async () => {
    mockFetch(200, { id: 1 })

    await expect(http.get('/api/things/1')).resolves.toEqual({ id: 1 })
  })

  it('sends JSON body', async () => {
    const fetchSpy = mockFetch(201, { id: 1 })

    await http.post('/api/things', { title: 'a' })

    expect(fetchSpy).toHaveBeenCalledWith('/api/things', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: '{"title":"a"}',
    })
  })

  it('returns undefined on 204', async () => {
    mockFetch(204)

    await expect(http.delete('/api/things/1')).resolves.toBeUndefined()
  })

  it('throws ApiError with field errors from ProblemDetail', async () => {
    mockFetch(400, {
      status: 400,
      detail: 'Validation failed',
      errors: { title: 'must not be blank' },
    })

    const error = await http.post('/api/things', {}).catch((e: unknown) => e)

    expect(error).toBeInstanceOf(ApiError)
    expect((error as ApiError).status).toBe(400)
    expect((error as ApiError).message).toBe('Validation failed')
    expect((error as ApiError).fieldErrors).toEqual({ title: 'must not be blank' })
  })
})
