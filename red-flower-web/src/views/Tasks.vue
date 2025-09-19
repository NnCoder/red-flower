<template>
  <div class="tasks-container">
    <div class="header">
      <h2>任务管理</h2>
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        创建任务
      </el-button>
    </div>

    <div class="tasks-content">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="我的任务" name="my">
          <div class="task-list">
            <el-empty v-if="myTasks.length === 0" description="暂无任务" />
            <div v-else>
              <TaskCard
                v-for="task in myTasks"
                :key="task.id"
                :task="task"
                @complete="handleCompleteTask"
                @edit="handleEditTask"
              />
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="已完成" name="completed">
          <div class="task-list">
            <el-empty v-if="completedTasks.length === 0" description="暂无已完成任务" />
            <div v-else>
              <TaskCard
                v-for="task in completedTasks"
                :key="task.id"
                :task="task"
                readonly
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 创建/编辑任务对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingTask ? '编辑任务' : '创建任务'"
      width="600px"
    >
      <el-form :model="taskForm" :rules="taskRules" ref="taskFormRef" label-width="80px">
        <el-form-item label="任务名称" prop="title">
          <el-input v-model="taskForm.title" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="任务描述" prop="description">
          <el-input
            v-model="taskForm.description"
            type="textarea"
            rows="3"
            placeholder="请输入任务描述"
          />
        </el-form-item>
        <el-form-item label="奖励花朵" prop="reward">
          <el-input-number v-model="taskForm.reward" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="截止时间" prop="deadline">
          <el-date-picker
            v-model="taskForm.deadline"
            type="datetime"
            placeholder="选择截止时间"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSaveTask">
            {{ editingTask ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

// 组件引用
const taskFormRef = ref()

// 响应式数据
const activeTab = ref('my')
const showCreateDialog = ref(false)
const editingTask = ref<any>(null)

const myTasks = ref([
  {
    id: 1,
    title: '完成数学作业',
    description: '完成第3章练习题',
    reward: 5,
    deadline: '2024-01-20 18:00:00',
    status: 'pending'
  },
  {
    id: 2,
    title: '帮忙做家务',
    description: '洗碗和拖地',
    reward: 3,
    deadline: '2024-01-19 20:00:00',
    status: 'pending'
  }
])

const completedTasks = ref([
  {
    id: 3,
    title: '阅读课外书',
    description: '阅读《小王子》第1-3章',
    reward: 10,
    deadline: '2024-01-18 19:00:00',
    status: 'completed',
    completedAt: '2024-01-18 18:30:00'
  }
])

const taskForm = reactive({
  title: '',
  description: '',
  reward: 1,
  deadline: null
})

const taskRules = {
  title: [
    { required: true, message: '请输入任务名称', trigger: 'blur' }
  ],
  reward: [
    { required: true, message: '请输入奖励花朵数量', trigger: 'blur' }
  ]
}

// 方法
const handleTabClick = (tab: any) => {
  console.log('切换到标签页:', tab.props.name)
}

const handleCompleteTask = (task: any) => {
  ElMessage.success(`任务"${task.title}"已完成，获得${task.reward}朵小红花！`)
  // 移动到已完成列表
  const index = myTasks.value.findIndex(t => t.id === task.id)
  if (index > -1) {
    task.status = 'completed'
    task.completedAt = new Date().toLocaleString()
    myTasks.value.splice(index, 1)
    completedTasks.value.unshift(task)
  }
}

const handleEditTask = (task: any) => {
  editingTask.value = task
  Object.assign(taskForm, task)
  showCreateDialog.value = true
}

const handleSaveTask = async () => {
  try {
    await taskFormRef.value.validate()

    if (editingTask.value) {
      // 更新任务
      Object.assign(editingTask.value, taskForm)
      ElMessage.success('任务更新成功！')
    } else {
      // 创建新任务
      const newTask = {
        id: Date.now(),
        ...taskForm,
        status: 'pending'
      }
      myTasks.value.unshift(newTask)
      ElMessage.success('任务创建成功！')
    }

    // 重置表单
    resetForm()
    showCreateDialog.value = false

  } catch (error) {
    console.error('保存任务失败:', error)
  }
}

const resetForm = () => {
  Object.assign(taskForm, {
    title: '',
    description: '',
    reward: 1,
    deadline: null
  })
  editingTask.value = null
}

onMounted(() => {
  // 组件挂载后的初始化逻辑
  console.log('任务管理页面已加载')
})
</script>

<script lang="ts">
// TaskCard 组件定义
import { defineComponent } from 'vue'

const TaskCard = defineComponent({
  name: 'TaskCard',
  props: {
    task: {
      type: Object,
      required: true
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  emits: ['complete', 'edit'],
  template: `
    <div class="task-card">
      <div class="task-header">
        <h3>{{ task.title }}</h3>
        <el-tag v-if="task.status === 'completed'" type="success">已完成</el-tag>
        <el-tag v-else type="warning">进行中</el-tag>
      </div>
      <p class="task-description">{{ task.description }}</p>
      <div class="task-footer">
        <div class="task-info">
          <span class="reward">奖励: {{ task.reward }}朵小红花</span>
          <span class="deadline">截止: {{ task.deadline }}</span>
        </div>
        <div class="task-actions" v-if="!readonly">
          <el-button size="small" @click="$emit('edit', task)">编辑</el-button>
          <el-button size="small" type="primary" @click="$emit('complete', task)">完成</el-button>
        </div>
      </div>
    </div>
  `
})

export { TaskCard }
</script>

<style scoped>
.tasks-container {
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

.tasks-content {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.task-list {
  min-height: 400px;
}

.task-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  transition: box-shadow 0.3s;
}

.task-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.task-header h3 {
  margin: 0;
  color: #333;
  font-size: 16px;
}

.task-description {
  color: #666;
  margin: 8px 0;
  line-height: 1.5;
}

.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.task-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.task-info span {
  font-size: 14px;
  color: #999;
}

.reward {
  color: #f56c6c !important;
  font-weight: bold;
}

.task-actions {
  display: flex;
  gap: 8px;
}
</style>