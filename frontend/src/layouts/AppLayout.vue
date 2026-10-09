<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { errorMessage } from '@/api/http'

const auth = useAuthStore()
const router = useRouter()
const error = ref('')
const busy = ref(false)
async function logout() {
  busy.value = true
  error.value = ''
  try {
    await auth.logout()
    await router.replace('/login')
  } catch (e) {
    error.value = errorMessage(e)
  } finally {
    busy.value = false
  }
}
</script>

<template>
  <div class="layout">
    <header class="header">
      <RouterLink to="/" class="brand">LifeOS</RouterLink>
      <nav v-if="auth.user" class="nav">
        <RouterLink to="/">Hôm nay</RouterLink>
      </nav>
      <div v-if="auth.user" class="account">
        <span>{{ auth.user.username }}</span>
        <button :disabled="busy" @click="logout">Đăng xuất</button>
      </div>
    </header>

    <main class="content">
      <p v-if="error" role="alert" class="error">{{ error }}</p>
      <slot />
    </main>
  </div>
</template>

<style scoped>
.account {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}
.header {
  flex-wrap: wrap;
}
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  align-items: center;
  gap: 2rem;
  padding: 0.75rem 1.5rem;
  border-bottom: 1px solid var(--color-border);
  background: var(--color-surface);
}

.brand {
  font-weight: 700;
  font-size: 1.125rem;
  color: var(--color-text);
}

.nav {
  display: flex;
  gap: 1rem;
}

.nav a {
  color: var(--color-text-muted);
}

.nav a.router-link-exact-active {
  color: var(--color-primary);
}

.content {
  flex: 1;
  width: 100%;
  max-width: 960px;
  margin: 0 auto;
  padding: 1.5rem;
}
</style>
