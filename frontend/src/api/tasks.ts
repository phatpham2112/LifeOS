import { http } from './http'

export interface Task {
  id: number
  title: string
  scheduledDate: string
  completed: boolean
}
export type TaskInput = Pick<Task, 'title' | 'scheduledDate'>
export const tasksApi = {
  list: (date: string) => http.get<Task[]>(`/api/tasks?date=${encodeURIComponent(date)}`),
  create: (input: TaskInput) => http.post<Task>('/api/tasks', input),
  update: (id: number, input: TaskInput) => http.put<Task>(`/api/tasks/${id}`, input),
  complete: (id: number, completed: boolean) =>
    http.patch<Task>(`/api/tasks/${id}/completion`, { completed }),
  delete: (id: number) => http.delete(`/api/tasks/${id}`),
}
