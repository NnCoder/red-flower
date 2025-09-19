<template>
  <div class="dashboard">
    <div class="sidebar">
      <div class="logo">
        <h2>🌸 小红花系统</h2>
      </div>

      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        router
        background-color="#2c3e50"
        text-color="#ecf0f1"
        active-text-color="#3498db"
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <el-menu-item index="/family">
          <el-icon><User /></el-icon>
          <span>家庭管理</span>
        </el-menu-item>

        <el-menu-item index="/tasks">
          <el-icon><List /></el-icon>
          <span>任务管理</span>
        </el-menu-item>

        <el-menu-item index="/rewards">
          <el-icon><Gift /></el-icon>
          <span>奖励商城</span>
        </el-menu-item>

        <el-menu-item index="/timeline">
          <el-icon><Clock /></el-icon>
          <span>成长时间线</span>
        </el-menu-item>

        <el-menu-item index="/class">
          <el-icon><School /></el-icon>
          <span>班级管理</span>
        </el-menu-item>
      </el-menu>

      <div class="user-info">
        <el-dropdown @command="handleCommand">
          <span class="el-dropdown-link">
            <el-icon><Avatar /></el-icon>
            {{ userInfo.nickname }}
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div class="main-content">
      <div class="content-header">
        <h1>欢迎回来，{{ userInfo.nickname }}！</h1>
        <p>今天也要继续加油哦 🌟</p>
      </div>

      <div class="stats-cards">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="stat-card red">
              <div class="stat-content">
                <div class="stat-icon">🌸</div>
                <div class="stat-info">
                  <h3>{{ flowerAccount.redFlowerBalance }}</h3>
                  <p>红花余额</p>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="6">
            <el-card class="stat-card black">
              <div class="stat-content">
                <div class="stat-icon">🖤</div>
                <div class="stat-info">
                  <h3>{{ flowerAccount.blackFlowerBalance }}</h3>
                  <p>黑花余额</p>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="6">
            <el-card class="stat-card green">
              <div class="stat-content">
                <div class="stat-icon">📋</div>
                <div class="stat-info">
                  <h3>{{ taskStats.completed }}</h3>
                  <p>完成任务</p>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="6">
            <el-card class="stat-card blue">
              <div class="stat-content">
                <div class="stat-icon">🏆</div>
                <div class="stat-info">
                  <h3>{{ milestoneCount }}</h3>
                  <p>解锁成就</p>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <div class="dashboard-content">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card>
              <template #header>
                <span>最近活动</span>
              </template>
              <div class="timeline-preview">
                <el-timeline>
                  <el-timeline-item
                    v-for="activity in recentActivities"
                    :key="activity.id"
                    :timestamp="activity.createTime"
                  >
                    {{ activity.title }}
                  </el-timeline-item>
                </el-timeline>
              </div>
            </el-card>
          </el-col>

          <el-col :span="12">
            <el-card>
              <template #header>
                <span>今日任务</span>
              </template>
              <div class="task-preview">
                <el-empty v-if="todayTasks.length === 0" description="今天没有任务" />
                <div v-else>
                  <div
                    v-for="task in todayTasks"
                    :key="task.id"
                    class="task-item"
                  >
                    <el-tag :type="task.status === 'COMPLETED' ? 'success' : 'warning'">
                      {{ task.title }}
                    </el-tag>
                    <span class="task-reward">+{{ task.rewardFlowers }}🌸</span>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => route.path)

const userInfo = reactive({
  id: 0,
  nickname: '用户',
  role: 'CHILD'
})

const flowerAccount = reactive({
  redFlowerBalance: 0,
  blackFlowerBalance: 0,
  totalEarned: 0,
  totalSpent: 0
})

const taskStats = reactive({
  completed: 0,
  pending: 0
})

const milestoneCount = ref(0)
const recentActivities = ref([])
const todayTasks = ref([])

onMounted(() => {
  loadUserInfo()
  loadFlowerAccount()
  loadRecentActivities()
  loadTodayTasks()
})

const loadUserInfo = async () => {
  try {
    // 这里需要从token中解析用户ID，简化处理
    const userId = 1
    const response = await request.get(`/user/${userId}`)
    Object.assign(userInfo, response)
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

const loadFlowerAccount = async () => {
  try {
    const userId = 1
    const response = await request.get(`/flower/account/${userId}`)
    Object.assign(flowerAccount, response)
  } catch (error) {
    console.error('加载红花账户失败:', error)
  }
}

const loadRecentActivities = async () => {
  try {
    const response = await request.post('/log/timeline', {
      userId: 1,
      page: 1,
      size: 5
    })
    recentActivities.value = response.records || []
  } catch (error) {
    console.error('加载最近活动失败:', error)
  }
}

const loadTodayTasks = async () => {
  try {
    // 模拟今日任务数据
    todayTasks.value = [
      { id: 1, title: '完成数学作业', status: 'PENDING', rewardFlowers: 5 },
      { id: 2, title: '阅读30分钟', status: 'COMPLETED', rewardFlowers: 3 }
    ]
    taskStats.completed = 1
    taskStats.pending = 1
  } catch (error) {
    console.error('加载今日任务失败:', error)
  }
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    ElMessage.success('退出成功')
    router.push('/login')
  } else if (command === 'profile') {
    ElMessage.info('个人信息功能开发中')
  }
}
</script>

<style scoped>
.dashboard {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 250px;
  background-color: #2c3e50;
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #34495e;
}

.logo h2 {
  color: #ecf0f1;
  margin: 0;
}

.sidebar-menu {
  flex: 1;
  border: none;
}

.user-info {
  padding: 20px;
  border-top: 1px solid #34495e;
  text-align: center;
  color: #ecf0f1;
}

.el-dropdown-link {
  cursor: pointer;
  color: #ecf0f1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: #f5f5f5;
  overflow-y: auto;
}

.content-header {
  margin-bottom: 20px;
}

.content-header h1 {
  color: #2c3e50;
  margin-bottom: 5px;
}

.content-header p {
  color: #7f8c8d;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  border: none;
  border-radius: 15px;
  overflow: hidden;
}

.stat-card.red { background: linear-gradient(45deg, #ff6b6b, #ee5a24); }
.stat-card.black { background: linear-gradient(45deg, #2d3436, #636e72); }
.stat-card.green { background: linear-gradient(45deg, #00b894, #00cec9); }
.stat-card.blue { background: linear-gradient(45deg, #0984e3, #74b9ff); }

.stat-content {
  display: flex;
  align-items: center;
  color: white;
  padding: 10px 0;
}

.stat-icon {
  font-size: 40px;
  margin-right: 15px;
}

.stat-info h3 {
  margin: 0;
  font-size: 28px;
  font-weight: bold;
}

.stat-info p {
  margin: 5px 0 0 0;
  opacity: 0.9;
}

.dashboard-content {
  margin-top: 20px;
}

.timeline-preview {
  max-height: 300px;
  overflow-y: auto;
}

.task-preview {
  max-height: 300px;
  overflow-y: auto;
}

.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.task-reward {
  color: #e67e22;
  font-weight: bold;
}
</style>