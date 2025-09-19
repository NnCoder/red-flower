import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/Register.vue')
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: () => import('@/views/Dashboard.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/family',
      name: 'Family',
      component: () => import('@/views/Family.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/tasks',
      name: 'Tasks',
      component: () => import('@/views/Tasks.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/rewards',
      name: 'Rewards',
      component: () => import('@/views/Rewards.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/timeline',
      name: 'Timeline',
      component: () => import('@/views/Timeline.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/class',
      name: 'Class',
      component: () => import('@/views/Class.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router