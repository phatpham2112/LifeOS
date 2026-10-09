<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<template>
  <div class="layout">
    <header class="header">
      <RouterLink to="/" class="brand">LifeOS</RouterLink>
      <nav class="nav">
        <RouterLink to="/">Hôm nay</RouterLink>
      </nav>
    </header>

    <main class="content">
      <slot />
    </main>
  </div>
</template>

<style scoped>
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
