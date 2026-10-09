import { flushPromises, mount } from '@vue/test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import HomeView from '../HomeView.vue'
import { tasksApi, type Task } from '@/api/tasks'

vi.mock('@/api/tasks', () => ({
  tasksApi: {
    list: vi.fn<typeof tasksApi.list>(),
    create: vi.fn<typeof tasksApi.create>(),
    update: vi.fn<typeof tasksApi.update>(),
    complete: vi.fn<typeof tasksApi.complete>(),
    delete: vi.fn<typeof tasksApi.delete>(),
  },
}))
const task: Task = { id: 1, title: 'Đọc sách', scheduledDate: '2026-10-09', completed: false }

beforeEach(() => {
  vi.resetAllMocks()
  vi.mocked(tasksApi.list).mockResolvedValue([task])
})

describe('daily tasks', () => {
  it('creates a trimmed task for the selected date and clears the form', async () => {
    const wrapper = mount(HomeView)
    await flushPromises()
    await wrapper.get('input[type=date]').setValue('2026-10-09')
    await flushPromises()
    await wrapper.get('#new-task').setValue('  Tập thể dục  ')
    await wrapper.get('form').trigger('submit')
    await flushPromises()
    expect(tasksApi.create).toHaveBeenCalledWith({
      title: 'Tập thể dục',
      scheduledDate: '2026-10-09',
    })
    expect((wrapper.get('#new-task').element as HTMLInputElement).value).toBe('')
  })
  it('toggles completion and requires confirmation to delete', async () => {
    const wrapper = mount(HomeView)
    await flushPromises()
    await wrapper.get('[aria-pressed]').trigger('click')
    await flushPromises()
    expect(tasksApi.complete).toHaveBeenCalledWith(1, true)
    await wrapper.get('button.danger').trigger('click')
    expect(tasksApi.delete).not.toHaveBeenCalled()
    await wrapper.get('.confirm button.danger').trigger('click')
    await flushPromises()
    expect(tasksApi.delete).toHaveBeenCalledWith(1)
  })
  it('keeps entered text when saving fails', async () => {
    vi.mocked(tasksApi.create).mockRejectedValue(new Error('offline'))
    const wrapper = mount(HomeView)
    await flushPromises()
    await wrapper.get('#new-task').setValue('Giữ nội dung')
    await wrapper.get('form').trigger('submit')
    await flushPromises()
    expect(wrapper.get('[role=alert]').text()).toContain('Không thể kết nối')
    expect((wrapper.get('#new-task').element as HTMLInputElement).value).toBe('Giữ nội dung')
  })
  it('ignores a stale list response after changing the date', async () => {
    let resolveFirst!: (tasks: Task[]) => void
    vi.mocked(tasksApi.list)
      .mockReturnValueOnce(
        new Promise((resolve) => {
          resolveFirst = resolve
        }),
      )
      .mockResolvedValueOnce([{ ...task, title: 'Ngày mới' }])
    const wrapper = mount(HomeView)
    await wrapper.get('input[type=date]').setValue('2027-01-01')
    await flushPromises()
    resolveFirst([{ ...task, title: 'Ngày cũ' }])
    await flushPromises()
    expect(wrapper.text()).toContain('Ngày mới')
    expect(wrapper.text()).not.toContain('Ngày cũ')
  })
})
