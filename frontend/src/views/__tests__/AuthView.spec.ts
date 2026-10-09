import { flushPromises, mount } from '@vue/test-utils'
import { createPinia } from 'pinia'
import { createMemoryHistory, createRouter } from 'vue-router'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import AuthView from '../AuthView.vue'
import { ApiError, http } from '@/api/http'
import { useAuthStore } from '@/stores/auth'

vi.mock('@/api/http', async (original) => ({
  ...(await original<typeof import('@/api/http')>()),
  http: { get: vi.fn<typeof http.get>(), post: vi.fn<typeof http.post>() },
}))

async function setup() {
  const pinia = createPinia()
  const router = createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/login', component: AuthView },
      { path: '/', component: { template: '<p>Home</p>' } },
    ],
  })
  await router.push('/login')
  const wrapper = mount(AuthView, { global: { plugins: [pinia, router] } })
  return { wrapper, router, auth: useAuthStore(pinia) }
}

beforeEach(() => vi.resetAllMocks())

describe('account form', () => {
  it('registers then asks the user to log in', async () => {
    const { wrapper } = await setup()
    await wrapper.get('button[type=button]').trigger('click')
    await wrapper.get('input[autocomplete=username]').setValue('alice')
    await wrapper.get('input[type=password]').setValue('password123')
    await wrapper.get('form').trigger('submit')
    await flushPromises()
    expect(http.post).toHaveBeenCalledWith('/api/auth/register', {
      username: 'alice',
      password: 'password123',
    })
    expect(wrapper.get('[role=status]').text()).toContain('Đã tạo tài khoản')
    expect((wrapper.get('input[type=password]').element as HTMLInputElement).value).toBe('')
  })
  it('restores account information and navigates home after login', async () => {
    vi.mocked(http.get).mockResolvedValue({ username: 'alice' })
    const { wrapper, router, auth } = await setup()
    await wrapper.get('input[autocomplete=username]').setValue('alice')
    await wrapper.get('input[type=password]').setValue('password123')
    await wrapper.get('form').trigger('submit')
    await flushPromises()
    expect(http.post).toHaveBeenCalledWith(
      '/api/auth/login',
      new URLSearchParams({ username: 'alice', password: 'password123' }),
    )
    expect(auth.user?.username).toBe('alice')
    expect(router.currentRoute.value.path).toBe('/')
  })
  it('shows rejected credentials without navigating or setting a user', async () => {
    vi.mocked(http.post).mockRejectedValue(new ApiError({ status: 401, detail: 'Sai mật khẩu' }))
    const { wrapper, router, auth } = await setup()
    await wrapper.get('form').trigger('submit')
    await flushPromises()
    expect(wrapper.get('[role=alert]').text()).toContain('Sai mật khẩu')
    expect(auth.user).toBeNull()
    expect(router.currentRoute.value.path).toBe('/login')
  })
})
