import { describe, expect, it } from 'vitest'
import { mount, RouterLinkStub } from '@vue/test-utils'

import AppLayout from '@/layouts/AppLayout.vue'

describe('AppLayout', () => {
  it('renders brand and slot content', () => {
    const wrapper = mount(AppLayout, {
      slots: { default: '<p>page content</p>' },
      global: { stubs: { RouterLink: RouterLinkStub } },
    })

    expect(wrapper.text()).toContain('LifeOS')
    expect(wrapper.html()).toContain('page content')
  })
})
