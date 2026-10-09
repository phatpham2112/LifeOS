<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { errorMessage } from '@/api/http'
import router from '@/router'
import { RouterView } from 'vue-router'

import AppLayout from '@/layouts/AppLayout.vue'
const auth = useAuthStore()
const error = ref('')
function expired() {
  auth.user = null
  if (auth.ready) void router.replace('/login')
}
const removeErrorHandler = router.onError((e) => {
  error.value = errorMessage(e)
})
onMounted(() => window.addEventListener('auth-expired', expired))
onUnmounted(() => {
  window.removeEventListener('auth-expired', expired)
  removeErrorHandler()
})
function retry() {
  window.location.reload()
}
</script>

<template>
  <AppLayout>
    <p v-if="error" role="alert" class="error">
      {{ error }} <button @click="retry">Thử lại</button>
    </p>
    <RouterView v-else />
  </AppLayout>
</template>
