<template>
  <div class="timeline-page">
    <div class="page-header">
      <h1>成长时间线</h1>
      <p>记录每一个成长的美好时刻</p>
    </div>

    <div class="timeline-filters">
      <el-row :gutter="20" align="middle">
        <el-col :span="4">
          <el-select v-model="filters.activityType" placeholder="活动类型" clearable>
            <el-option label="任务完成" value="TASK_COMPLETE" />
            <el-option label="获得红花" value="FLOWER_EARN" />
            <el-option label="购买奖励" value="REWARD_PURCHASE" />
            <el-option label="解锁成就" value="ACHIEVEMENT_UNLOCK" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="filters.activityScope" placeholder="活动范围" clearable>
            <el-option label="家庭" value="FAMILY" />
            <el-option label="班级" value="CLASS" />
            <el-option label="小组" value="GROUP" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="loadTimeline">筛选</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-col>
        <el-col :span="6" style="text-align: right">
          <el-button type="success" @click="exportReport">导出报告</el-button>
          <el-button type="info" @click="showMilestones">查看成就</el-button>
        </el-col>
      </el-row>
    </div>

    <div class="timeline-content">
      <el-row :gutter="20">
        <!-- 时间线 -->
        <el-col :span="16">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>活动时间线</span>
                <el-radio-group v-model="viewMode" size="small">
                  <el-radio-button label="timeline">时间线</el-radio-button>
                  <el-radio-button label="calendar">日历</el-radio-button>
                </el-radio-group>
              </div>
            </template>

            <div v-if="viewMode === 'timeline'" class="timeline-view">
              <el-timeline>
                <el-timeline-item
                  v-for="activity in timelineData"
                  :key="activity.id"
                  :timestamp="formatTime(activity.createTime)"
                  :color="getActivityColor(activity.activityType)"
                >
                  <el-card class="timeline-card">
                    <div class="activity-header">
                      <span class="activity-icon">{{ getActivityIcon(activity.activityType) }}</span>
                      <span class="activity-title">{{ activity.title }}</span>
                      <el-tag :type="getScopeTagType(activity.activityScope)" size="small">
                        {{ getScopeName(activity.activityScope) }}
                      </el-tag>
                    </div>
                    <p class="activity-description">{{ activity.description }}</p>
                    <div v-if="activity.metadata" class="activity-metadata">
                      <el-tag v-for="(value, key) in parseMetadata(activity.metadata)"
                              :key="key"
                              size="small"
                              effect="plain">
                        {{ key }}: {{ value }}
                      </el-tag>
                    </div>
                  </el-card>
                </el-timeline-item>
              </el-timeline>

              <div v-if="timelineData.length === 0" class="empty-state">
                <el-empty description="暂无活动记录" />
              </div>

              <div v-if="hasMore" class="load-more">
                <el-button @click="loadMore" :loading="loading">加载更多</el-button>
              </div>
            </div>

            <div v-else class="calendar-view">
              <el-calendar v-model="calendarDate">
                <template #date-cell="{ data }">
                  <div class="calendar-cell">
                    <span class="date">{{ data.day.split('-')[2] }}</span>
                    <div class="activities">
                      <span
                        v-for="activity in getDateActivities(data.day)"
                        :key="activity.id"
                        class="activity-dot"
                        :style="{ backgroundColor: getActivityColor(activity.activityType) }"
                        :title="activity.title"
                      ></span>
                    </div>
                  </div>
                </template>
              </el-calendar>
            </div>
          </el-card>
        </el-col>

        <!-- 统计面板 -->
        <el-col :span="8">
          <el-card>
            <template #header>
              <span>成长统计</span>
            </template>
            <div class="stats-panel">
              <div class="stat-item">
                <div class="stat-number">{{ statistics.totalActivities }}</div>
                <div class="stat-label">总活动数</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ statistics.activeDays }}</div>
                <div class="stat-label">活跃天数</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ statistics.completedTasks }}</div>
                <div class="stat-label">完成任务</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ statistics.earnedFlowers }}</div>
                <div class="stat-label">获得红花</div>
              </div>
            </div>
          </el-card>

          <el-card style="margin-top: 20px">
            <template #header>
              <span>最近成就</span>
            </template>
            <div class="milestones-panel">
              <div
                v-for="milestone in recentMilestones"
                :key="milestone.id"
                class="milestone-item"
              >
                <div class="milestone-icon">🏆</div>
                <div class="milestone-info">
                  <h4>{{ milestone.title }}</h4>
                  <p>{{ milestone.description }}</p>
                  <small>{{ formatTime(milestone.unlockTime) }}</small>
                </div>
              </div>
              <el-empty v-if="recentMilestones.length === 0" description="暂无成就" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 成就对话框 -->
    <el-dialog v-model="milestonesDialogVisible" title="我的成就" width="600px">
      <div class="milestones-grid">
        <div
          v-for="milestone in allMilestones"
          :key="milestone.id"
          class="milestone-card"
          :class="{ special: milestone.isSpecial }"
        >
          <div class="milestone-badge" :style="{ backgroundColor: milestone.badgeColor }">
            🏆
          </div>
          <h4>{{ milestone.title }}</h4>
          <p>{{ milestone.description }}</p>
          <small>{{ formatTime(milestone.unlockTime) }}</small>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const viewMode = ref('timeline')
const loading = ref(false)
const hasMore = ref(true)
const calendarDate = ref(new Date())
const milestonesDialogVisible = ref(false)

const filters = reactive({
  activityType: '',
  activityScope: '',
  page: 1,
  size: 20
})

const dateRange = ref([])

const timelineData = ref([])
const statistics = reactive({
  totalActivities: 0,
  activeDays: 0,
  completedTasks: 0,
  earnedFlowers: 0
})

const recentMilestones = ref([])
const allMilestones = ref([])

onMounted(() => {
  loadTimeline()
  loadStatistics()
  loadRecentMilestones()
})

const loadTimeline = async () => {
  try {
    loading.value = true
    const query = {
      userId: 1,
      ...filters,
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1]
    }

    const response = await request.post('/log/timeline', query)

    if (filters.page === 1) {
      timelineData.value = response.records || []
    } else {
      timelineData.value.push(...(response.records || []))
    }

    hasMore.value = response.current < response.pages
  } catch (error) {
    console.error('加载时间线失败:', error)
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  filters.page++
  loadTimeline()
}

const loadStatistics = async () => {
  try {
    // 模拟统计数据
    Object.assign(statistics, {
      totalActivities: 156,
      activeDays: 45,
      completedTasks: 89,
      earnedFlowers: 234
    })
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadRecentMilestones = async () => {
  try {
    const response = await request.get('/log/milestones/1?page=1&size=5')
    recentMilestones.value = response.records || []
  } catch (error) {
    console.error('加载成就失败:', error)
  }
}

const resetFilters = () => {
  filters.activityType = ''
  filters.activityScope = ''
  filters.page = 1
  dateRange.value = []
  loadTimeline()
}

const exportReport = async () => {
  try {
    const response = await request.post('/log/report/1', null, {
      params: {
        reportType: 'MONTHLY',
        startDate: dateRange.value?.[0] || '2024-01-01',
        endDate: dateRange.value?.[1] || '2024-12-31'
      }
    })
    ElMessage.success('报告生成成功')
  } catch (error) {
    console.error('导出报告失败:', error)
  }
}

const showMilestones = async () => {
  try {
    const response = await request.get('/log/milestones/1?page=1&size=50')
    allMilestones.value = response.records || []
    milestonesDialogVisible.value = true
  } catch (error) {
    console.error('加载所有成就失败:', error)
  }
}

const getActivityIcon = (type: string) => {
  const iconMap = {
    'TASK_COMPLETE': '✅',
    'FLOWER_EARN': '🌸',
    'REWARD_PURCHASE': '🎁',
    'ACHIEVEMENT_UNLOCK': '🏆',
    'CLASS_JOIN': '🏫',
    'GROUP_JOIN': '👥'
  }
  return iconMap[type] || '📝'
}

const getActivityColor = (type: string) => {
  const colorMap = {
    'TASK_COMPLETE': '#67c23a',
    'FLOWER_EARN': '#f56c6c',
    'REWARD_PURCHASE': '#e6a23c',
    'ACHIEVEMENT_UNLOCK': '#909399',
    'CLASS_JOIN': '#409eff',
    'GROUP_JOIN': '#909399'
  }
  return colorMap[type] || '#909399'
}

const getScopeName = (scope: string) => {
  const scopeMap = {
    'FAMILY': '家庭',
    'CLASS': '班级',
    'GROUP': '小组'
  }
  return scopeMap[scope] || scope
}

const getScopeTagType = (scope: string) => {
  const typeMap = {
    'FAMILY': 'success',
    'CLASS': 'primary',
    'GROUP': 'warning'
  }
  return typeMap[scope] || 'info'
}

const formatTime = (time: string) => {
  return new Date(time).toLocaleString('zh-CN')
}

const parseMetadata = (metadata: string) => {
  try {
    return JSON.parse(metadata || '{}')
  } catch {
    return {}
  }
}

const getDateActivities = (date: string) => {
  return timelineData.value.filter(activity =>
    activity.createTime.startsWith(date)
  )
}
</script>

<style scoped>
.timeline-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  color: #2c3e50;
  margin-bottom: 5px;
}

.page-header p {
  color: #7f8c8d;
}

.timeline-filters {
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.timeline-view {
  max-height: 600px;
  overflow-y: auto;
}

.timeline-card {
  margin-bottom: 0;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.activity-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.activity-icon {
  font-size: 20px;
}

.activity-title {
  font-weight: bold;
  color: #2c3e50;
}

.activity-description {
  color: #7f8c8d;
  margin-bottom: 10px;
}

.activity-metadata {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

.calendar-view {
  height: 600px;
}

.calendar-cell {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.date {
  font-size: 14px;
  margin-bottom: 5px;
}

.activities {
  display: flex;
  gap: 2px;
  flex-wrap: wrap;
}

.activity-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  display: inline-block;
}

.stats-panel {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 10px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 5px;
}

.stat-label {
  color: #7f8c8d;
  font-size: 12px;
}

.milestones-panel {
  max-height: 300px;
  overflow-y: auto;
}

.milestone-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.milestone-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.milestone-info h4 {
  margin: 0 0 5px 0;
  color: #2c3e50;
}

.milestone-info p {
  margin: 0 0 5px 0;
  color: #7f8c8d;
  font-size: 12px;
}

.milestone-info small {
  color: #aaa;
  font-size: 11px;
}

.milestones-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 15px;
}

.milestone-card {
  text-align: center;
  padding: 20px 10px;
  border: 2px solid #e9ecef;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.milestone-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.milestone-card.special {
  border-color: #f39c12;
  background: linear-gradient(135deg, #fff9e6 0%, #fef3d4 100%);
}

.milestone-badge {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 10px;
  font-size: 24px;
  color: white;
}

.milestone-card h4 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 14px;
}

.milestone-card p {
  margin: 0 0 5px 0;
  color: #7f8c8d;
  font-size: 12px;
}

.empty-state {
  text-align: center;
  padding: 40px;
}

.load-more {
  text-align: center;
  padding: 20px;
}
</style>