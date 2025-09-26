<template>
  <div class="rewards-container">
    <div class="header">
      <h2>奖励商店</h2>
      <div class="balance-info">
        <div class="balance-item red-flower">
          <el-icon><Coin /></el-icon>
          <span>红花: {{ userAccount.redFlowerBalance }}朵</span>
        </div>
        <div class="balance-item black-flower">
          <el-icon><Warning /></el-icon>
          <span>黑花: {{ userAccount.blackFlowerBalance }}朵</span>
        </div>
      </div>
    </div>

    <div class="rewards-content">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="奖励商店" name="shop">
          <div class="reward-grid">
            <RewardCard
              v-for="reward in availableRewards"
              :key="reward.id"
              :reward="reward"
              :user-account="userAccount"
              @redeem="handleRedeem"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="我的奖励" name="my">
          <div class="my-rewards">
            <el-empty v-if="myRewards.length === 0" description="暂无兑换记录" />
            <div v-else class="reward-history">
              <div
                v-for="record in myRewards"
                :key="record.id"
                class="reward-record"
              >
                <div class="record-info">
                  <h4>{{ record.name }}</h4>
                  <p>{{ record.description }}</p>
                  <span class="redeem-time">兑换时间: {{ record.redeemTime }}</span>
                </div>
                <div class="record-status">
                  <el-tag :type="getStatusType(record.status)">
                    {{ getStatusText(record.status) }}
                  </el-tag>
                  <span class="cost">-{{ record.cost }}朵</span>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 兑换确认对话框 -->
    <el-dialog
      v-model="showRedeemDialog"
      title="确认兑换"
      width="400px"
      center
    >
      <div v-if="selectedReward" class="redeem-confirmation">
        <div class="reward-preview">
          <img :src="selectedReward.icon" :alt="selectedReward.name" />
          <h3>{{ selectedReward.name }}</h3>
          <p>{{ selectedReward.description }}</p>
        </div>
        <div class="cost-info">
          <p>需要消耗:
            <strong v-if="selectedReward.redFlowerCost > 0">{{ selectedReward.redFlowerCost }}朵红花</strong>
            <strong v-if="selectedReward.blackFlowerCost > 0">{{ selectedReward.blackFlowerCost }}朵黑花</strong>
          </p>
          <p>当前余额:
            <strong>{{ userAccount.redFlowerBalance }}朵红花</strong> /
            <strong>{{ userAccount.blackFlowerBalance }}朵黑花</strong>
          </p>
          <p v-if="selectedReward.flowerRecycle && selectedReward.recycleRate > 0" class="recycle-info">
            <el-icon><Refresh /></el-icon>
            <span>回收机制: {{ Math.round(selectedReward.recycleRate * 100) }}%红花将返还给奖励发布者</span>
          </p>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showRedeemDialog = false">取消</el-button>
          <el-button
            type="primary"
            @click="confirmRedeem"
            :disabled="!canRedeem(selectedReward)"
          >
            确认兑换
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Coin, Warning, Refresh } from '@element-plus/icons-vue'
import axios from 'axios'

// 响应式数据
const activeTab = ref('shop')
const showRedeemDialog = ref(false)
const selectedReward = ref<any>(null)
const userAccount = ref({
  redFlowerBalance: 45,
  blackFlowerBalance: 3,
  totalEarned: 120,
  totalSpent: 75
}) // 用户账户信息

const availableRewards = ref([
  {
    id: 1,
    name: '额外游戏时间',
    description: '获得额外30分钟游戏时间',
    redFlowerCost: 10,
    blackFlowerCost: 0,
    icon: '🎮',
    category: 'time',
    isActive: true,
    flowerRecycle: true,
    recycleRate: 0.8,
    creatorId: 1001
  },
  {
    id: 2,
    name: '选择今日菜单',
    description: '可以选择今天晚餐吃什么',
    redFlowerCost: 15,
    blackFlowerCost: 0,
    icon: '🍽️',
    category: 'privilege',
    isActive: true,
    flowerRecycle: false,
    recycleRate: 0,
    creatorId: 1001
  },
  {
    id: 3,
    name: '小礼品',
    description: '获得一个小礼品',
    redFlowerCost: 20,
    blackFlowerCost: 0,
    icon: '🎁',
    category: 'gift',
    isActive: true,
    flowerRecycle: true,
    recycleRate: 0.6,
    creatorId: 1001
  },
  {
    id: 4,
    name: '减少黑花惩罚',
    description: '消除2朵黑花',
    redFlowerCost: 0,
    blackFlowerCost: 2,
    icon: '🌙',
    category: 'penalty',
    isActive: true,
    flowerRecycle: false,
    recycleRate: 0,
    creatorId: 1001
  },
  {
    id: 5,
    name: '电影票',
    description: '和家人一起看电影',
    redFlowerCost: 25,
    blackFlowerCost: 5,
    icon: '🎬',
    category: 'activity',
    isActive: true,
    flowerRecycle: true,
    recycleRate: 1.0,
    creatorId: 1001
  },
  {
    id: 6,
    name: '零花钱',
    description: '获得10元零花钱',
    redFlowerCost: 25,
    blackFlowerCost: 0,
    icon: '💰',
    category: 'money',
    isActive: true,
    flowerRecycle: true,
    recycleRate: 0.9,
    creatorId: 1001
  }
])

const myRewards = ref([
  {
    id: 101,
    name: '额外游戏时间',
    description: '获得额外30分钟游戏时间',
    redFlowerCost: 10,
    blackFlowerCost: 0,
    recycleAmount: 8,
    status: 'COMPLETED',
    createTime: '2024-01-18 19:30:00'
  },
  {
    id: 102,
    name: '小礼品',
    description: '获得一个小礼品',
    redFlowerCost: 20,
    blackFlowerCost: 0,
    recycleAmount: 12,
    status: 'COMPLETED',
    createTime: '2024-01-19 10:15:00'
  }
])

// 方法
const handleTabClick = (tab: any) => {
  console.log('切换到标签页:', tab.props.name)
}

const handleRedeem = (reward: any) => {
  if (!canRedeem(reward)) {
    ElMessage.warning('花朵余额不足，无法兑换此奖励')
    return
  }

  selectedReward.value = reward
  showRedeemDialog.value = true
}

const canRedeem = (reward: any) => {
  if (!reward) return false
  return userAccount.value.redFlowerBalance >= reward.redFlowerCost &&
         userAccount.value.blackFlowerBalance >= reward.blackFlowerCost
}

const confirmRedeem = async () => {
  if (!selectedReward.value) return

  try {
    // 调用后端 API
    const response = await axios.post('/api/flower/reward/exchange', {
      rewardId: selectedReward.value.id,
      userId: getCurrentUserId(), // 获取当前用户ID
      remark: '兑换奖励'
    })

    if (response.data.success) {
      // 更新用户余额
      userAccount.value.redFlowerBalance -= selectedReward.value.redFlowerCost
      userAccount.value.blackFlowerBalance -= selectedReward.value.blackFlowerCost
      userAccount.value.totalSpent += selectedReward.value.redFlowerCost

      // 添加到我的奖励
      const newRecord = {
        id: Date.now(),
        name: selectedReward.value.name,
        description: selectedReward.value.description,
        redFlowerCost: selectedReward.value.redFlowerCost,
        blackFlowerCost: selectedReward.value.blackFlowerCost,
        recycleAmount: selectedReward.value.flowerRecycle ?
          Math.round(selectedReward.value.redFlowerCost * selectedReward.value.recycleRate) : 0,
        status: 'COMPLETED',
        createTime: new Date().toLocaleString()
      }

      myRewards.value.unshift(newRecord)

      ElMessage.success(`成功兑换"${selectedReward.value.name}"！${newRecord.recycleAmount > 0 ? `将回收${newRecord.recycleAmount}朵红花给奖励发布者` : ''}`)
    }
  } catch (error) {
    console.error('兑换失败:', error)
    ElMessage.error('兑换失败，请稍后重试')
  }

  // 关闭对话框
  showRedeemDialog.value = false
  selectedReward.value = null

  // 切换到"我的奖励"标签页
  activeTab.value = 'my'
}

const getCurrentUserId = () => {
  // 这里应该从登录状态或localStorage中获取
  return localStorage.getItem('userId') || '1'
}

const loadUserAccount = async () => {
  try {
    const userId = getCurrentUserId()
    const response = await axios.get(`/api/flower/account/${userId}`)
    if (response.data.success) {
      userAccount.value = response.data.data
    }
  } catch (error) {
    console.error('获取用户账户信息失败:', error)
  }
}

const loadRewards = async () => {
  try {
    // 获取家庭奖励列表
    const familyId = localStorage.getItem('familyId')
    const response = await axios.get('/api/flower/rewards', {
      params: { familyId }
    })
    if (response.data.success) {
      availableRewards.value = response.data.data
    }
  } catch (error) {
    console.error('获取奖励列表失败:', error)
  }
}

const getStatusType = (status: string) => {
  switch (status) {
    case 'COMPLETED': return 'success'
    case 'PENDING': return 'warning'
    case 'CANCELLED': return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'COMPLETED': return '已完成'
    case 'PENDING': return '处理中'
    case 'CANCELLED': return '已取消'
    default: return '未知'
  }
}

onMounted(() => {
  console.log('奖励商店页面已加载')
  loadUserAccount()
  loadRewards()
})
</script>

<script lang="ts">
// RewardCard 组件定义
import { defineComponent } from 'vue'

const RewardCard = defineComponent({
  name: 'RewardCard',
  props: {
    reward: {
      type: Object,
      required: true
    },
    userAccount: {
      type: Object,
      required: true
    }
  },
  emits: ['redeem'],
  template: `
    <div class="reward-card">
      <div class="reward-icon">{{ reward.icon }}</div>
      <div class="reward-info">
        <h3>{{ reward.name }}</h3>
        <p>{{ reward.description }}</p>
        <div class="reward-cost">
          <span v-if="reward.redFlowerCost > 0" class="red-cost">{{ reward.redFlowerCost }}朵红花</span>
          <span v-if="reward.blackFlowerCost > 0" class="black-cost">{{ reward.blackFlowerCost }}朵黑花</span>
        </div>
        <div v-if="reward.flowerRecycle && reward.recycleRate > 0" class="recycle-info">
          <span class="recycle-rate">回收率: {{ Math.round(reward.recycleRate * 100) }}%</span>
        </div>
      </div>
      <div class="reward-actions">
        <el-button
          type="primary"
          :disabled="!canRedeem()"
          @click="$emit('redeem', reward)"
        >
          {{ getButtonText() }}
        </el-button>
      </div>
    </div>
  `,
  methods: {
    canRedeem() {
      return this.userAccount.redFlowerBalance >= this.reward.redFlowerCost &&
             this.userAccount.blackFlowerBalance >= this.reward.blackFlowerCost
    },
    getButtonText() {
      if (!this.canRedeem()) {
        if (this.userAccount.redFlowerBalance < this.reward.redFlowerCost) {
          return '红花不足'
        }
        if (this.userAccount.blackFlowerBalance < this.reward.blackFlowerCost) {
          return '黑花不足'
        }
      }
      return '兑换'
    }
  }
})

export { RewardCard }
</script>

<style scoped>
.rewards-container {
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

.balance-info {
  display: flex;
  gap: 20px;
}

.balance-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 20px;
  font-weight: bold;
  font-size: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.balance-item.red-flower {
  background: linear-gradient(45deg, #ff6b6b, #ffa500);
  color: white;
}

.balance-item.black-flower {
  background: linear-gradient(45deg, #666666, #333333);
  color: white;
}

.rewards-content {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.reward-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  min-height: 400px;
}

.reward-card {
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  transition: all 0.3s;
  background: linear-gradient(135deg, #f8f9fa, #ffffff);
}

.reward-card:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.reward-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.reward-info h3 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 18px;
}

.reward-info p {
  color: #666;
  margin: 0 0 16px 0;
  line-height: 1.5;
}

.reward-cost {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.reward-cost .red-cost {
  background: linear-gradient(45deg, #ff6b6b, #ffa500);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-weight: bold;
  font-size: 12px;
}

.reward-cost .black-cost {
  background: linear-gradient(45deg, #666666, #333333);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-weight: bold;
  font-size: 12px;
}

.recycle-info {
  margin-bottom: 8px;
}

.recycle-rate {
  background: #e8f5e8;
  color: #52c41a;
  padding: 2px 6px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: bold;
}

.my-rewards {
  min-height: 400px;
}

.reward-history {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.reward-record {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #fafafa;
}

.record-info h4 {
  margin: 0 0 4px 0;
  color: #333;
}

.record-info p {
  margin: 0 0 8px 0;
  color: #666;
  font-size: 14px;
}

.redeem-time {
  color: #999;
  font-size: 12px;
}

.record-status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.cost {
  color: #f56c6c;
  font-weight: bold;
}

.redeem-confirmation {
  text-align: center;
}

.reward-preview {
  margin-bottom: 20px;
}

.reward-preview img {
  width: 80px;
  height: 80px;
  margin-bottom: 12px;
}

.reward-preview h3 {
  margin: 0 0 8px 0;
  color: #333;
}

.reward-preview p {
  color: #666;
  margin: 0;
}

.cost-info {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  text-align: left;
}

.cost-info p {
  margin: 8px 0;
  font-size: 14px;
}

.cost-info strong {
  color: #ff6b6b;
}

.recycle-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #52c41a;
  font-size: 13px;
  margin-top: 8px;
}
</style>