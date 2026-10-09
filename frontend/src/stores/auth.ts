import { ref } from 'vue'
import { defineStore } from 'pinia'
import { ApiError, http } from '@/api/http'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<{ username: string } | null>(null)
  const ready = ref(false)
  async function restore() {
    try {
      user.value = await http.get('/api/auth/me')
    } catch (error) {
      if (!(error instanceof ApiError && error.status === 401)) throw error
      user.value = null
    }
    ready.value = true
  }
  async function login(username: string, password: string) {
    await http.post('/api/auth/login', new URLSearchParams({ username, password }))
    await restore()
  }
  async function logout() {
    await http.post('/api/auth/logout')
    user.value = null
  }
  return { user, ready, restore, login, logout }
})
