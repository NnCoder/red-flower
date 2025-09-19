<template>
  <div class="class-container">
    <div class="header">
      <h2>班级管理</h2>
      <el-button type="primary" @click="showJoinDialog = true">
        <el-icon><Plus /></el-icon>
        加入班级
      </el-button>
    </div>

    <div class="class-content">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="我的班级" name="my">
          <div v-if="myClasses.length === 0" class="empty-state">
            <el-empty description="还没有加入任何班级">
              <el-button type="primary" @click="showJoinDialog = true">
                加入班级
              </el-button>
            </el-empty>
          </div>
          <div v-else class="class-grid">
            <ClassCard
              v-for="classInfo in myClasses"
              :key="classInfo.id"
              :class-info="classInfo"
              @enter="handleEnterClass"
              @leave="handleLeaveClass"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="班级排行榜" name="ranking">
          <div v-if="currentClass">
            <div class="ranking-header">
              <h3>{{ currentClass.name }} - 小红花排行榜</h3>
              <el-select v-model="selectedPeriod" placeholder="选择统计周期" style="width: 200px">
                <el-option label="本周" value="week" />
                <el-option label="本月" value="month" />
                <el-option label="总计" value="all" />
              </el-select>
            </div>

            <div class="ranking-list">
              <div
                v-for="(student, index) in classRanking"
                :key="student.id"
                class="ranking-item"
                :class="getRankingClass(index)"
              >
                <div class="rank-number">
                  <span v-if="index < 3" class="medal">{{ getMedal(index) }}</span>
                  <span v-else class="number">{{ index + 1 }}</span>
                </div>
                <div class="student-info">
                  <img :src="student.avatar" :alt="student.name" class="avatar" />
                  <div class="name-score">
                    <span class="name">{{ student.name }}</span>
                    <span class="score">{{ student.flowers }}朵小红花</span>
                  </div>
                </div>
                <div class="progress-bar">
                  <el-progress
                    :percentage="getProgressPercentage(student.flowers)"
                    :stroke-width="8"
                    :show-text="false"
                  />
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty-ranking">
            <el-empty description="请先选择一个班级查看排行榜" />
          </div>
        </el-tab-pane>

        <el-tab-pane label="班级活动" name="activities">
          <div v-if="currentClass">
            <div class="activity-list">
              <div
                v-for="activity in classActivities"
                :key="activity.id"
                class="activity-item"
              >
                <div class="activity-icon">{{ activity.icon }}</div>
                <div class="activity-content">
                  <h4>{{ activity.title }}</h4>
                  <p>{{ activity.description }}</p>
                  <div class="activity-meta">
                    <span>参与人数: {{ activity.participants }}人</span>
                    <span>截止时间: {{ activity.deadline }}</span>
                  </div>
                </div>
                <div class="activity-action">
                  <el-button
                    :type="activity.joined ? 'success' : 'primary'"
                    :disabled="activity.joined"
                    @click="handleJoinActivity(activity)"
                  >
                    {{ activity.joined ? '已参与' : '参与' }}
                  </el-button>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty-activities">
            <el-empty description="请先选择一个班级查看活动" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 加入班级对话框 -->
    <el-dialog v-model="showJoinDialog" title="加入班级" width="500px">
      <el-form :model="joinForm" :rules="joinRules" ref="joinFormRef" label-width="80px">
        <el-form-item label="班级代码" prop="classCode">
          <el-input
            v-model="joinForm.classCode"
            placeholder="请输入班级邀请代码"
            maxlength="10"
          />
        </el-form-item>
        <el-form-item label="学生姓名" prop="studentName">
          <el-input
            v-model="joinForm.studentName"
            placeholder="请输入你的姓名"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showJoinDialog = false">取消</el-button>
          <el-button type="primary" @click="handleJoinClass">加入</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

// 响应式数据
const activeTab = ref('my')
const showJoinDialog = ref(false)
const selectedPeriod = ref('week')
const currentClass = ref<any>(null)

const joinForm = reactive({
  classCode: '',
  studentName: ''
})

const joinRules = {
  classCode: [
    { required: true, message: '请输入班级代码', trigger: 'blur' }
  ],
  studentName: [
    { required: true, message: '请输入学生姓名', trigger: 'blur' }
  ]
}

const myClasses = ref([
  {
    id: 1,
    name: '三年级2班',
    teacher: '张老师',
    studentCount: 28,
    code: 'CLASS2023',
    joinDate: '2024-01-15',
    description: '我们是一个充满活力的班级！',
    avatar: '📚'
  }
])

const classRanking = ref([
  {
    id: 1,
    name: '小明',
    flowers: 95,
    avatar: '👦'
  },
  {
    id: 2,
    name: '小红',
    flowers: 87,
    avatar: '👧'
  },
  {
    id: 3,
    name: '小李',
    flowers: 82,
    avatar: '👦'
  },
  {
    id: 4,
    name: '小花',
    flowers: 76,
    avatar: '👧'
  },
  {
    id: 5,
    name: '小王',
    flowers: 69,
    avatar: '👦'
  }
])

const classActivities = ref([
  {
    id: 1,
    title: '读书打卡挑战',
    description: '连续7天读书打卡，获得额外小红花奖励',
    participants: 15,
    deadline: '2024-01-25',
    icon: '📖',
    joined: false
  },
  {
    id: 2,
    title: '数学竞赛',
    description: '参与班级数学小竞赛，前三名有特殊奖励',
    participants: 12,
    deadline: '2024-01-22',
    icon: '🧮',
    joined: true
  },
  {
    id: 3,
    title: '环保小卫士',
    description: '参与环保活动，为地球贡献一份力量',
    participants: 8,
    deadline: '2024-01-30',
    icon: '🌱',
    joined: false
  }
])

// 计算属性
const maxFlowers = computed(() => {
  if (classRanking.value.length === 0) return 100
  return Math.max(...classRanking.value.map(s => s.flowers))
})

// 方法
const handleTabClick = (tab: any) => {
  if (tab.props.name === 'ranking' || tab.props.name === 'activities') {
    if (myClasses.value.length > 0) {
      currentClass.value = myClasses.value[0]
    }
  }
}

const handleEnterClass = (classInfo: any) => {
  currentClass.value = classInfo
  activeTab.value = 'ranking'
  ElMessage.success(`进入班级: ${classInfo.name}`)
}

const handleLeaveClass = (classInfo: any) => {
  ElMessage.warning('确定要退出这个班级吗？')
}

const handleJoinClass = () => {
  if (!joinForm.classCode || !joinForm.studentName) {
    ElMessage.warning('请填写完整信息')
    return
  }

  // 模拟加入班级逻辑
  const newClass = {
    id: Date.now(),
    name: '示例班级',
    teacher: '李老师',
    studentCount: 25,
    code: joinForm.classCode,
    joinDate: new Date().toLocaleDateString(),
    description: '欢迎加入我们的班级！',
    avatar: '🎓'
  }

  myClasses.value.push(newClass)

  // 重置表单
  Object.assign(joinForm, {
    classCode: '',
    studentName: ''
  })

  showJoinDialog.value = false
  ElMessage.success('成功加入班级！')
}

const handleJoinActivity = (activity: any) => {
  activity.joined = true
  activity.participants += 1
  ElMessage.success(`成功参与活动: ${activity.title}`)
}

const getRankingClass = (index: number) => {
  if (index === 0) return 'first-place'
  if (index === 1) return 'second-place'
  if (index === 2) return 'third-place'
  return ''
}

const getMedal = (index: number) => {
  const medals = ['🥇', '🥈', '🥉']
  return medals[index]
}

const getProgressPercentage = (flowers: number) => {
  return Math.round((flowers / maxFlowers.value) * 100)
}

onMounted(() => {
  console.log('班级管理页面已加载')
})
</script>

<script lang="ts">
// ClassCard 组件定义
import { defineComponent } from 'vue'

const ClassCard = defineComponent({
  name: 'ClassCard',
  props: {
    classInfo: {
      type: Object,
      required: true
    }
  },
  emits: ['enter', 'leave'],
  template: `
    <div class="class-card">
      <div class="class-header">
        <div class="class-avatar">{{ classInfo.avatar }}</div>
        <div class="class-basic-info">
          <h3>{{ classInfo.name }}</h3>
          <p>{{ classInfo.teacher }} | {{ classInfo.studentCount }}名学生</p>
        </div>
      </div>
      <div class="class-description">
        <p>{{ classInfo.description }}</p>
      </div>
      <div class="class-meta">
        <span>加入时间: {{ classInfo.joinDate }}</span>
        <span>班级代码: {{ classInfo.code }}</span>
      </div>
      <div class="class-actions">
        <el-button type="primary" @click="$emit('enter', classInfo)">
          进入班级
        </el-button>
        <el-button type="danger" plain @click="$emit('leave', classInfo)">
          退出
        </el-button>
      </div>
    </div>
  `
})

export { ClassCard }
</script>

<style scoped>
.class-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  color: #333;
  margin: 0;
}

.class-content {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.empty-state {
  min-height: 400px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.class-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.class-card {
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s;
  background: linear-gradient(135deg, #f8f9fa, #ffffff);
}

.class-card:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.class-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.class-avatar {
  font-size: 40px;
  margin-right: 16px;
}

.class-basic-info h3 {
  margin: 0 0 4px 0;
  color: #333;
  font-size: 18px;
}

.class-basic-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.class-description {
  margin-bottom: 16px;
}

.class-description p {
  color: #666;
  line-height: 1.5;
  margin: 0;
}

.class-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  font-size: 12px;
  color: #999;
}

.class-actions {
  display: flex;
  gap: 12px;
}

.ranking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.ranking-header h3 {
  margin: 0;
  color: #333;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-radius: 12px;
  background: #f8f9fa;
  transition: all 0.3s;
}

.ranking-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.ranking-item.first-place {
  background: linear-gradient(135deg, #ffd700, #ffed4a);
}

.ranking-item.second-place {
  background: linear-gradient(135deg, #c0c0c0, #e5e5e5);
}

.ranking-item.third-place {
  background: linear-gradient(135deg, #cd7f32, #daa520);
}

.rank-number {
  width: 50px;
  text-align: center;
}

.medal {
  font-size: 24px;
}

.number {
  font-size: 18px;
  font-weight: bold;
  color: #666;
}

.student-info {
  display: flex;
  align-items: center;
  flex: 1;
  margin-left: 16px;
}

.avatar {
  font-size: 24px;
  margin-right: 12px;
}

.name-score {
  display: flex;
  flex-direction: column;
}

.name {
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.score {
  font-size: 14px;
  color: #666;
}

.progress-bar {
  width: 150px;
  margin-left: 20px;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  background: #fafafa;
  transition: all 0.3s;
}

.activity-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.activity-icon {
  font-size: 32px;
  margin-right: 16px;
}

.activity-content {
  flex: 1;
}

.activity-content h4 {
  margin: 0 0 8px 0;
  color: #333;
}

.activity-content p {
  margin: 0 0 8px 0;
  color: #666;
  line-height: 1.5;
}

.activity-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #999;
}

.activity-action {
  margin-left: 16px;
}

.empty-ranking,
.empty-activities {
  min-height: 400px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>