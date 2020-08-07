import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../components/Login.vue'
import Home from '../components/Home.vue'
import Welcome from '../components/Welcome.vue'
import Users from '../components/user/users.vue'
import Rights from '../components/power/Rights.vue'
import Roles from '../components/power/Roles.vue'
import Machines from '../components/machine/Machines.vue'
import Products from '../components/machine/Products.vue'
import Productivitys from '../components/machine/Productivitys.vue'
import Plans from '../components/plan/Plans.vue'
import MachinePlans from '../components/plan/MachinePlans.vue'
import AssignPlans from '../components/plan/AssignPlans.vue'

Vue.use(VueRouter)

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  {
    path: '/home',
    component: Home,
    redirect: '/welcome',
    children: [
      { path: '/welcome', component: Welcome },
      { path: '/users', component: Users },
      { path: '/rights', component: Rights },
      { path: '/roles', component: Roles },
      { path: '/machines', component: Machines },
      { path: '/products', component: Products },
      { path: '/productivitys', component: Productivitys },
      { path: '/plans', component: Plans },
      { path: '/machinePlans', component: MachinePlans },
      { path: '/assignPlans', component: AssignPlans }
    ]
  }
]

const router = new VueRouter({
  routes
})
// 挂载路由导航守卫
router.beforeEach((to, from, next) => {
  // to 将要访问的路径
  // from 代表从哪个路径跳转而来
  // next 是一个函数，表示放行
  //     next()  放行    next('/login')  强制跳转

  if (to.path === '/login') return next()
  // 获取cookie
  const myCookie = document.cookie
  // eslint-disable-next-line no-useless-escape
    .replace(/(?:(?:^|.*;\s*)JSESSIONID\s*\=\s*([^;]*).*$)|^.*$/, '$1')
  if (!myCookie) return next('/login')
  next()
})

export default router
