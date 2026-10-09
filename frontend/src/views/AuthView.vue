<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { errorMessage, http } from '@/api/http'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const registering = ref(false)
const username = ref('')
const password = ref('')
const busy = ref(false)
const error = ref('')
const notice = ref('')

function switchMode() {
  registering.value = !registering.value
  error.value = ''
  notice.value = ''
}

async function submit() {
  busy.value = true
  error.value = ''
  notice.value = ''
  try {
    if (registering.value) {
      await http.post('/api/auth/register', { username: username.value, password: password.value })
      registering.value = false
      notice.value = 'Đã tạo tài khoản. Bạn có thể đăng nhập ngay.'
      password.value = ''
    } else {
      await auth.login(username.value, password.value)
      await router.replace('/')
    }
  } catch (e) {
    error.value = errorMessage(e)
  } finally {
    busy.value = false
  }
}
</script>

<template>
  <section class="auth-card panel">
    <h1>{{ registering ? 'Tạo tài khoản' : 'Đăng nhập LifeOS' }}</h1>
    <p class="muted">Một nơi dành cho những việc quan trọng mỗi ngày.</p>
    <form @submit.prevent="submit">
      <fieldset :disabled="busy">
        <label
          >Tên đăng nhập
          <input
            v-model="username"
            required
            pattern="[a-z0-9_]{3,40}"
            maxlength="40"
            autocomplete="username"
            autocapitalize="none"
          />
        </label>
        <small class="muted">3–40 ký tự thường, số hoặc dấu gạch dưới.</small>
        <label
          >Mật khẩu
          <input
            v-model="password"
            type="password"
            required
            :minlength="registering ? 8 : undefined"
            maxlength="72"
            :autocomplete="registering ? 'new-password' : 'current-password'"
          />
        </label>
        <small v-if="registering" class="muted">Tối thiểu 8 ký tự, tối đa 72 byte UTF-8.</small>
        <p v-if="error" role="alert" class="error">{{ error }}</p>
        <p v-if="notice" role="status">{{ notice }}</p>
        <button class="primary" type="submit">
          {{ busy ? 'Đang xử lý…' : registering ? 'Tạo tài khoản' : 'Đăng nhập' }}
        </button>
        <button type="button" @click="switchMode">
          {{ registering ? 'Đã có tài khoản? Đăng nhập' : 'Chưa có tài khoản? Đăng ký' }}
        </button>
      </fieldset>
    </form>
  </section>
</template>

<style scoped>
.auth-card {
  max-width: 440px;
  margin: 3rem auto;
}
form {
  margin-top: 1.5rem;
}
fieldset {
  display: grid;
  gap: 1rem;
}
</style>
