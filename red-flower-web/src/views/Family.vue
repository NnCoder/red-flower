<template>
  <div class="family-page">
    <div class="page-header">
      <h1>家庭管理</h1>
      <p>管理您的家庭成员和红花分配</p>
    </div>

    <el-row :gutter="20">
      <!-- 家庭信息 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>家庭信息</span>
              <el-button type="primary" size="small" @click="editFamily">编辑</el-button>
            </div>
          </template>
          <div class="family-info">
            <div class="family-avatar">👨‍👩‍👧‍👦</div>
            <h3>{{ familyInfo.name }}</h3>
            <p>{{ familyInfo.description }}</p>
            <div class="family-stats">
              <div class="stat-item">
                <span class="label">成员数量:</span>
                <span class="value">{{ familyMembers.length }}</span>
              </div>
              <div class="stat-item">
                <span class="label">红花总量:</span>
                <span class="value">{{ familyInfo.flowerTotal }}🌸</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 家庭成员 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>家庭成员</span>
              <el-button type="primary" size="small" @click="addMember">添加成员</el-button>
            </div>
          </template>
          <div class="members-grid">
            <div
              v-for="member in familyMembers"
              :key="member.id"
              class="member-card"
              :class="member.role.toLowerCase()"
            >
              <div class="member-avatar">
                <el-avatar :size="60" :src="member.avatar">
                  {{ member.nickname.charAt(0) }}
                </el-avatar>
              </div>
              <div class="member-info">
                <h4>{{ member.nickname }}</h4>
                <el-tag :type="member.role === 'PARENT' ? 'success' : 'primary'">
                  {{ getRoleName(member.role) }}
                </el-tag>
                <p>{{ member.relation }}</p>
              </div>
              <div class="member-flowers">
                <div class="flower-item">
                  <span class="flower-icon">🌸</span>
                  <span class="flower-count">{{ member.redFlowers || 0 }}</span>
                </div>
                <div class="flower-item">
                  <span class="flower-icon">🖤</span>
                  <span class="flower-count">{{ member.blackFlowers || 0 }}</span>
                </div>
              </div>
              <div class="member-actions">
                <el-button
                  v-if="member.role === 'CHILD'"
                  type="primary"
                  size="small"
                  @click="transferFlowers(member)"
                >
                  转红花
                </el-button>
                <el-button
                  type="info"
                  size="small"
                  @click="viewMemberDetail(member)"
                >
                  详情
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 红花转账对话框 -->
    <el-dialog
      v-model="transferDialogVisible"
      title="红花转账"
      width="400px"
    >
      <el-form :model="transferForm" label-width="80px">
        <el-form-item label="转给">
          <span>{{ selectedMember?.nickname }}</span>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number
            v-model="transferForm.amount"
            :min="1"
            :max="currentUserFlowers"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="transferForm.remark"
            type="textarea"
            placeholder="转账备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmTransfer">确认转账</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const familyInfo = reactive({
  id: 1,
  name: '幸福之家',
  description: '一个温馨的家庭',
  flowerTotal: 1000
})

const familyMembers = ref([
  {
    id: 1,
    nickname: '爸爸',
    role: 'PARENT',
    relation: '父亲',
    avatar: '',
    redFlowers: 50,
    blackFlowers: 0
  },
  {
    id: 2,
    nickname: '妈妈',
    role: 'PARENT',
    relation: '母亲',
    avatar: '',
    redFlowers: 45,
    blackFlowers: 0
  },
  {
    id: 3,
    nickname: '小明',
    role: 'CHILD',
    relation: '儿子',
    avatar: '',
    redFlowers: 15,
    blackFlowers: 2
  },
  {
    id: 4,
    nickname: '小红',
    role: 'CHILD',
    relation: '女儿',
    avatar: '',
    redFlowers: 20,
    blackFlowers: 0
  }
])

const transferDialogVisible = ref(false)
const selectedMember = ref(null)
const currentUserFlowers = ref(50)

const transferForm = reactive({
  amount: 1,
  remark: ''
})

onMounted(() => {
  loadFamilyInfo()
  loadFamilyMembers()
})

const loadFamilyInfo = async () => {
  try {
    // 加载家庭信息
    console.log('加载家庭信息')
  } catch (error) {
    console.error('加载家庭信息失败:', error)
  }
}

const loadFamilyMembers = async () => {
  try {
    // 加载家庭成员
    console.log('加载家庭成员')
  } catch (error) {
    console.error('加载家庭成员失败:', error)
  }
}

const getRoleName = (role: string) => {
  const roleMap = {
    'PARENT': '家长',
    'CHILD': '孩子'
  }
  return roleMap[role] || role
}

const editFamily = () => {
  ElMessage.info('编辑家庭功能开发中')
}

const addMember = () => {
  ElMessage.info('添加成员功能开发中')
}

const transferFlowers = (member: any) => {
  selectedMember.value = member
  transferForm.amount = 1
  transferForm.remark = ''
  transferDialogVisible.value = true
}

const viewMemberDetail = (member: any) => {
  ElMessage.info(`查看${member.nickname}的详细信息`)
}

const confirmTransfer = async () => {
  try {
    await request.post('/flower/transfer', {
      fromUserId: 1, // 当前用户ID
      toUserId: selectedMember.value.id,
      amount: transferForm.amount,
      remark: transferForm.remark
    })

    ElMessage.success('转账成功')
    transferDialogVisible.value = false
    loadFamilyMembers()
  } catch (error) {
    console.error('转账失败:', error)
  }
}
</script>

<style scoped>
.family-page {
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.family-info {
  text-align: center;
}

.family-avatar {
  font-size: 60px;
  margin-bottom: 15px;
}

.family-info h3 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.family-stats {
  margin-top: 20px;
  text-align: left;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.label {
  color: #7f8c8d;
}

.value {
  font-weight: bold;
  color: #2c3e50;
}

.members-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.member-card {
  border: 2px solid #e9ecef;
  border-radius: 15px;
  padding: 20px;
  background: white;
  transition: all 0.3s ease;
}

.member-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.member-card.parent {
  border-color: #28a745;
  background: linear-gradient(135deg, #f8fff9 0%, #e8f5e8 100%);
}

.member-card.child {
  border-color: #007bff;
  background: linear-gradient(135deg, #f8fbff 0%, #e3f2fd 100%);
}

.member-avatar {
  text-align: center;
  margin-bottom: 15px;
}

.member-info {
  text-align: center;
  margin-bottom: 15px;
}

.member-info h4 {
  margin-bottom: 8px;
  color: #2c3e50;
}

.member-info p {
  color: #7f8c8d;
  margin-top: 8px;
}

.member-flowers {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 15px;
}

.flower-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 16px;
  font-weight: bold;
}

.flower-icon {
  font-size: 20px;
}

.member-actions {
  text-align: center;
}

.member-actions .el-button {
  margin: 0 5px;
}
</style>