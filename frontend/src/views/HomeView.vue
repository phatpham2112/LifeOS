<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { errorMessage } from '@/api/http'
import { tasksApi, type Task } from '@/api/tasks'

function today() {
  const date = new Date()
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}
const date = ref(today())
const tasks = ref<Task[]>([])
const title = ref('')
const loading = ref(false)
const busy = ref(false)
const error = ref('')
const editing = ref<number | null>(null)
const editTitle = ref('')
const editDate = ref('')
const deleting = ref<number | null>(null)
const completed = computed(() => tasks.value.filter((task) => task.completed).length)
let loadVersion = 0

async function load() {
  const version = ++loadVersion
  tasks.value = []
  editing.value = null
  deleting.value = null
  error.value = ''
  if (!date.value) {
    loading.value = false
    return
  }
  loading.value = true
  try {
    const result = await tasksApi.list(date.value)
    if (version === loadVersion) tasks.value = result
  } catch (e) {
    if (version === loadVersion) error.value = errorMessage(e)
  } finally {
    if (version === loadVersion) loading.value = false
  }
}
watch(date, load, { immediate: true })

async function mutate(action: () => Promise<unknown>, after?: () => void) {
  if (busy.value) return
  busy.value = true
  error.value = ''
  try {
    await action()
    after?.()
    await load()
  } catch (e) {
    error.value = errorMessage(e)
  } finally {
    busy.value = false
  }
}
function create() {
  const value = title.value.trim()
  if (!value || !date.value) return
  void mutate(
    () => tasksApi.create({ title: value, scheduledDate: date.value }),
    () => {
      title.value = ''
    },
  )
}
function toggle(task: Task) {
  void mutate(() => tasksApi.complete(task.id, !task.completed))
}
function remove(id: number) {
  void mutate(() => tasksApi.delete(id))
}
function edit(task: Task) {
  editing.value = task.id
  editTitle.value = task.title
  editDate.value = task.scheduledDate
  deleting.value = null
}
function save(id: number) {
  if (!editTitle.value.trim() || !editDate.value) return
  void mutate(() =>
    tasksApi.update(id, { title: editTitle.value.trim(), scheduledDate: editDate.value }),
  )
}
</script>

<template>
  <section>
    <div class="heading">
      <div>
        <h1>Công việc hằng ngày</h1>
        <p class="muted">{{ completed }}/{{ tasks.length }} công việc đã hoàn thành</p>
      </div>
      <label>Ngày thực hiện <input v-model="date" type="date" required :disabled="busy" /></label>
    </div>
    <form class="create panel" @submit.prevent="create">
      <label class="grow" for="new-task"
        >Công việc mới
        <input
          id="new-task"
          v-model="title"
          placeholder="Hôm nay bạn muốn làm gì?"
          maxlength="200"
          required
          :disabled="busy"
        />
      </label>
      <button class="primary" :disabled="busy || loading || !title.trim() || !date" type="submit">
        Thêm công việc
      </button>
    </form>
    <p v-if="error" class="error" role="alert">
      {{ error }} <button :disabled="busy" @click="load">Tải lại</button>
    </p>
    <p v-if="loading" class="empty" role="status">Đang tải công việc…</p>
    <p v-else-if="!date" class="empty">Chọn ngày để xem công việc.</p>
    <p v-else-if="!tasks.length && !error" class="empty">
      Chưa có công việc nào. Thêm việc đầu tiên cho ngày này nhé.
    </p>
    <ul v-else class="tasks" :aria-busy="busy">
      <li v-for="task in tasks" :key="task.id" class="panel">
        <form v-if="editing === task.id" class="edit-form" @submit.prevent="save(task.id)">
          <label
            >Tiêu đề <input v-model="editTitle" maxlength="200" required :disabled="busy"
          /></label>
          <label
            >Ngày thực hiện <input v-model="editDate" type="date" required :disabled="busy"
          /></label>
          <div class="actions">
            <button class="primary" :disabled="busy || !editTitle.trim()">Lưu</button
            ><button type="button" :disabled="busy" @click="editing = null">Hủy</button>
          </div>
        </form>
        <div v-else class="task-row">
          <button
            class="check"
            :aria-label="`${task.completed ? 'Bỏ hoàn thành' : 'Hoàn thành'}: ${task.title}`"
            :aria-pressed="task.completed"
            :disabled="busy"
            @click="toggle(task)"
          >
            {{ task.completed ? '✓' : '○' }}
          </button>
          <span class="grow task-title" :class="{ completed: task.completed }">{{
            task.title
          }}</span>
          <div class="actions">
            <button :disabled="busy" @click="edit(task)">Sửa</button
            ><button class="danger" :disabled="busy" @click="deleting = task.id">Xóa</button>
          </div>
        </div>
        <div v-if="deleting === task.id" class="confirm" role="group" aria-label="Xác nhận xóa">
          <p>Xóa công việc này?</p>
          <button class="danger" :disabled="busy" @click="remove(task.id)">Xác nhận xóa</button
          ><button :disabled="busy" @click="deleting = null">Hủy</button>
        </div>
      </li>
    </ul>
  </section>
</template>

<style scoped>
.heading,
.create,
.task-row,
.actions,
.confirm {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}
.heading {
  justify-content: space-between;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}
.create {
  align-items: end;
  margin-bottom: 1.5rem;
}
.grow {
  flex: 1;
  min-width: 0;
}
.tasks {
  list-style: none;
  padding: 0;
  display: grid;
  gap: 0.75rem;
}
.task-title {
  overflow-wrap: anywhere;
}
.completed {
  text-decoration: line-through;
  color: var(--color-text-muted);
}
.check {
  font-size: 1.2rem;
}
.empty {
  text-align: center;
  padding: 3rem 0;
  color: var(--color-text-muted);
}
.edit-form {
  display: grid;
  gap: 0.75rem;
}
.confirm {
  margin-top: 1rem;
  flex-wrap: wrap;
}
@media (max-width: 540px) {
  .create {
    align-items: stretch;
    flex-direction: column;
  }
  .task-row {
    flex-wrap: wrap;
  }
}
</style>
