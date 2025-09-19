<template>
  <div class="rewards-container">
    <div class="header">
      <h2>奖励商店</h2>
      <div class="balance-info">
        <el-icon><Coin /></el-icon>
        <span>我的小红花: {{ userBalance }}朵</span>
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
              :user-balance="userBalance"
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
          <p>需要消耗: <strong>{{ selectedReward.cost }}朵小红花</strong></p>
          <p>当前余额: <strong>{{ userBalance }}朵小红花</strong></p>
          <p>兑换后余额: <strong>{{ userBalance - selectedReward.cost }}朵小红花</strong></p>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showRedeemDialog = false">取消</el-button>
          <el-button
            type="primary"
            @click="confirmRedeem"
            :disabled="!selectedReward || userBalance < selectedReward.cost"
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
import { Coin } from '@element-plus/icons-vue'

// 响应式数据
const activeTab = ref('shop')
const showRedeemDialog = ref(false)
const selectedReward = ref<any>(null)
const userBalance = ref(45) // 用户当前小红花余额

const availableRewards = ref([
  {
    id: 1,
    name: '额外游戏时间',
    description: '获得额外30分钟游戏时间',
    cost: 10,
    icon: '🎮',
    category: 'time',
    available: true
  },
  {
    id: 2,
    name: '选择今日菜单',
    description: '可以选择今天晚餐吃什么',
    cost: 15,
    icon: '🍽️',
    category: 'privilege',
    available: true
  },
  {
    id: 3,
    name: '小礼品',
    description: '获得一个小礼品',
    cost: 20,
    icon: '🎁',
    category: 'gift',
    available: true
  },
  {
    id: 4,
    name: '延迟睡觉时间',
    description: '今晚可以晚睡30分钟',
    cost: 12,
    icon: '🌙',
    category: 'time',
    available: true
  },
  {
    id: 5,
    name: '电影票',
    description: '和家人一起看电影',
    cost: 30,
    icon: '🎬',
    category: 'activity',
    available: true
  },
  {
    id: 6,
    name: '零花钱',
    description: '获得10元零花钱',
    cost: 25,
    icon: '💰',
    category: 'money',
    available: true
  }
])

const myRewards = ref([
  {
    id: 101,
    name: '额外游戏时间',
    description: '获得额外30分钟游戏时间',
    cost: 10,
    status: 'used',
    redeemTime: '2024-01-18 19:30:00'
  },
  {
    id: 102,
    name: '小礼品',
    description: '获得一个小礼品',
    cost: 20,
    status: 'pending',
    redeemTime: '2024-01-19 10:15:00'
  }
])

// 方法
const handleTabClick = (tab: any) => {
  console.log('切换到标签页:', tab.props.name)
}

const handleRedeem = (reward: any) => {
  if (userBalance.value < reward.cost) {
    ElMessage.warning('小红花余额不足，无法兑换此奖励')
    return
  }

  selectedReward.value = reward
  showRedeemDialog.value = true
}

const confirmRedeem = () => {
  if (!selectedReward.value) return

  // 扣除小红花
  userBalance.value -= selectedReward.value.cost

  // 添加到我的奖励
  const newRecord = {
    id: Date.now(),
    name: selectedReward.value.name,
    description: selectedReward.value.description,
    cost: selectedReward.value.cost,
    status: 'pending',
    redeemTime: new Date().toLocaleString()
  }

  myRewards.value.unshift(newRecord)

  ElMessage.success(`成功兑换"${selectedReward.value.name}"！`)

  // 关闭对话框
  showRedeemDialog.value = false
  selectedReward.value = null

  // 切换到"我的奖励"标签页
  activeTab.value = 'my'
}

const getStatusType = (status: string) => {
  switch (status) {
    case 'pending': return 'warning'
    case 'used': return 'success'
    case 'expired': return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'pending': return '待使用'
    case 'used': return '已使用'
    case 'expired': return '已过期'
    default: return '未知'
  }
}

onMounted(() => {
  console.log('奖励商店页面已加载')
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
    userBalance: {
      type: Number,
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
          <span>{{ reward.cost }}朵小红花</span>
        </div>
      </div>
      <div class="reward-actions">
        <el-button
          type="primary"
          :disabled="userBalance < reward.cost"
          @click="$emit('redeem', reward)"
        >
          {{ userBalance < reward.cost ? '余额不足' : '兑换' }}
        </el-button>
      </div>
    </div>
  `
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
  align-items: center;
  gap: 8px;
  background: linear-gradient(45deg, #ff6b6b, #ffa500);
  color: white;
  padding: 12px 20px;
  border-radius: 20px;
  font-weight: bold;
  font-size: 16px;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
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
}

.reward-cost span {
  background: linear-gradient(45deg, #ff6b6b, #ffa500);
  color: white;
  padding: 6px 12px;
  border-radius: 16px;
  font-weight: bold;
  font-size: 14px;
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
</style>