# 🌸 小红花家庭奖励系统

一个基于Spring Cloud微服务架构的家庭和班级奖励管理系统，支持多孩子独立管理、成长日志记录和班级管理功能。

## ✨ 主要功能

### 🏠 家庭场景
- **多孩子管理**: 每个孩子独立的红花账户和任务体系
- **任务管理**: 支持学习、行为、习惯等多种任务类型，包含计时功能
- **奖励系统**: 实物奖励、权限奖励、体验奖励等多样化奖励机制
- **红花经济**: 红花获得、消费、转账，黑花惩罚机制
- **成长记录**: 完整的活动日志和时间线，支持PDF报告导出

### 🏫 教育场景
- **班级管理**: 老师可管理多个班级，支持小组分组
- **班级任务**: 班级整体任务、小组协作任务、个人任务
- **排行竞赛**: 班级排行榜和小组竞赛机制
- **家校互通**: 家长可查看孩子在校表现

### 📊 数据分析
- **里程碑系统**: 自动检测并解锁成就
- **统计分析**: 多维度数据统计和趋势分析
- **可视化展示**: 时间线、排行榜、成长曲线等丰富展示

## 🏗️ 技术架构

### 后端技术栈
- **Java 17** + **Spring Boot 3.x** + **Spring Cloud 2022.x**
- **MySQL 8.0** + **MyBatis Plus** + **Redis 7.x**
- **RocketMQ** + **Nacos** + **Spring Cloud Gateway**
- **JWT认证** + **Spring Security** + **Knife4j文档**

### 前端技术栈
- **Vue 3** + **TypeScript** + **Element Plus**
- **Pinia状态管理** + **Vue Router** + **Axios**
- **ECharts图表** + **响应式设计**

### 微服务架构
```
├── red-flower-gateway     # API网关
├── red-flower-user        # 用户服务
├── red-flower-flower      # 红花服务
├── red-flower-task        # 任务服务
├── red-flower-reward      # 奖励服务
├── red-flower-class       # 班级服务
├── red-flower-log         # 日志服务
├── red-flower-message     # 消息服务
└── red-flower-web         # 前端应用
```

## 🚀 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+
- Redis 7.x
- RocketMQ 5.x
- Nacos 2.x
- Node.js 16+

### 1. 克隆项目
```bash
git clone https://github.com/your-repo/red-flower.git
cd red-flower
```

### 2. 数据库初始化
```bash
# 执行数据库初始化脚本
mysql -u root -p < init.sql
```

### 3. 启动基础服务
```bash
# 启动 MySQL
sudo systemctl start mysql

# 启动 Redis
sudo systemctl start redis

# 启动 RocketMQ Name Server
cd rocketmq/bin
./mqnamesrv

# 启动 RocketMQ Broker (新开终端)
cd rocketmq/bin
./mqbroker -n localhost:9876

# 启动 Nacos (下载后解压)
cd nacos/bin
./startup.sh -m standalone
```

### 4. 启动后端服务
```bash
# 按顺序启动各个微服务
cd red-flower-user && mvn spring-boot:run
cd red-flower-flower && mvn spring-boot:run
cd red-flower-log && mvn spring-boot:run
# ... 启动其他服务
```

### 5. 启动前端应用
```bash
cd red-flower-web
npm install
npm run serve
```

### 6. 访问应用
- 前端地址: http://localhost:8080
- API文档: http://localhost:8081/doc.html
- Nacos控制台: http://localhost:8848/nacos

## 📱 功能截图

### 登录页面
![登录页面](screenshots/login.png)

### 仪表板
![仪表板](screenshots/dashboard.png)

### 家庭管理
![家庭管理](screenshots/family.png)

### 成长时间线
![成长时间线](screenshots/timeline.png)

## 🔧 配置说明

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/red_flower
    username: root
    password: your_password
```

### Redis配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
```

### RocketMQ配置
```yaml
spring:
  rocketmq:
    name-server: localhost:9876
```

### Nacos配置
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
```

## 📖 API文档

项目使用Knife4j生成API文档，启动服务后访问：
- 用户服务: http://localhost:8081/doc.html
- 红花服务: http://localhost:8082/doc.html
- 日志服务: http://localhost:8086/doc.html

## 🏃‍♂️ Docker部署

### 1. 构建镜像
```bash
# 构建所有服务镜像
mvn clean package -DskipTests
docker-compose build
```

### 2. 启动服务
```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps
```

### 3. 停止服务
```bash
docker-compose down
```

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 📋 开发计划

### ✅ 已完成功能 (当前阶段)

#### 🏗️ 基础架构
- [x] **项目结构搭建** - Maven多模块项目，微服务架构设计
- [x] **通用模块** - 统一响应结果、异常处理、枚举定义、基础实体类
- [x] **数据库设计** - 完整的表结构设计，支持家庭和班级两种场景
- [x] **Docker配置** - Docker Compose一键部署配置

#### 👤 用户服务 (red-flower-user)
- [x] **用户管理** - 用户注册、登录、信息管理
- [x] **JWT认证** - 完整的Token生成、验证、刷新机制
- [x] **家庭管理** - 家庭创建、成员管理、关系维护
- [x] **多角色支持** - 管理员、家长、孩子、老师、学生角色
- [x] **权限控制** - 基于角色的权限验证

#### 🌸 红花服务 (red-flower-flower)
- [x] **账户管理** - 红花账户创建、余额查询
- [x] **交易系统** - 红花获得、消费、转账功能
- [x] **黑花机制** - 黑花累计、抵扣规则
- [x] **流水记录** - 完整的交易记录和流水查询
- [x] **事务保证** - 分布式事务处理，数据一致性保证

#### 📊 日志服务 (red-flower-log)
- [x] **活动日志** - 自动记录用户所有活动
- [x] **时间线生成** - 可视化时间线展示，支持筛选和分页
- [x] **里程碑系统** - 自动检测成就，解锁里程碑
- [x] **成长报告** - 多维度统计分析，PDF报告导出
- [x] **数据可视化** - 日历视图、统计图表、成就墙

#### 🖥️ 前端应用 (red-flower-web)
- [x] **Vue3框架** - 现代化前端架构，TypeScript支持
- [x] **登录注册** - 用户登录、注册界面，JWT认证集成
- [x] **仪表板** - 数据概览、统计卡片、最近活动展示
- [x] **家庭管理** - 家庭信息编辑、成员管理、红花转账
- [x] **成长时间线** - 活动时间线、里程碑展示、报告导出
- [x] **响应式设计** - 多端适配，移动端友好

#### 📱 UI/UX设计
- [x] **现代化界面** - Element Plus组件库，美观易用
- [x] **游戏化元素** - 红花图标、成就徽章、进度条
- [x] **色彩体系** - 温馨的色彩搭配，适合家庭使用
- [x] **交互体验** - 流畅的动画效果，直观的操作流程

### 🚧 待开发功能 (下一阶段)

#### 📋 任务服务 (red-flower-task)
- [ ] **任务管理** - 任务创建、编辑、删除、状态管理
- [ ] **任务类型** - 学习任务、行为任务、习惯任务
- [ ] **计时功能** - 任务计时器、暂停继续、时长统计
- [ ] **循环任务** - 每日、每周、自定义周期任务
- [ ] **任务执行** - 任务开始、完成、审核流程
- [ ] **奖励发放** - 任务完成后自动发放红花奖励

#### 🏫 班级服务 (red-flower-class)
- [ ] **学校管理** - 学校信息管理、红花池分配
- [ ] **班级管理** - 班级创建、学生管理、红花池
- [ ] **小组功能** - 学习小组创建、成员管理、组长设置
- [ ] **班级任务** - 班级整体任务、小组协作任务
- [ ] **排行榜** - 班级排名、小组竞赛、积分统计
- [ ] **家校互通** - 家长查看孩子在校表现

#### 🎁 奖励服务 (red-flower-reward)
- [ ] **奖励管理** - 奖励创建、编辑、库存管理
- [ ] **奖励类型** - 实物奖励、时间奖励、体验奖励
- [ ] **第三方集成** - 淘宝、京东商品信息获取
- [ ] **购买流程** - 奖励购买、确认、发放流程
- [ ] **使用记录** - 奖励使用时长、超时黑花机制
- [ ] **奖励商城** - 前端奖励商城界面

#### 💬 消息服务 (red-flower-message)
- [ ] **消息推送** - 任务提醒、奖励通知、成就提醒
- [ ] **消息模板** - 可配置的消息模板管理
- [ ] **推送渠道** - 站内消息、邮件、短信推送
- [ ] **消息统计** - 消息发送统计、阅读率分析

#### 🌐 网关服务 (red-flower-gateway)
- [ ] **API网关** - 统一入口、路由转发、负载均衡
- [ ] **权限验证** - JWT验证、角色权限校验
- [ ] **限流熔断** - 接口限流、服务熔断保护
- [ ] **监控日志** - 请求日志、性能监控

### 🔮 远期规划 (v2.0+)

#### 🤖 智能化功能
- [ ] **AI推荐** - 智能任务推荐、个性化奖励建议
- [ ] **行为分析** - 用户行为模式分析、成长预测
- [ ] **智能提醒** - 基于习惯的智能提醒功能

#### 📱 移动端
- [ ] **移动端APP** - React Native跨平台应用
- [ ] **小程序** - 微信小程序、支付宝小程序
- [ ] **离线功能** - 离线任务记录、数据同步

#### 🌍 社交功能
- [ ] **家庭圈子** - 家庭间互动、经验分享
- [ ] **排行榜** - 跨家庭排行、友好竞争
- [ ] **专家指导** - 教育专家指导建议

#### 📈 高级分析
- [ ] **大数据分析** - 用户行为大数据分析
- [ ] **个性化报告** - AI生成个性化成长报告
- [ ] **趋势预测** - 成长趋势预测和建议

### 📊 当前完成度

```
总体进度: ████████░░ 80%

核心功能模块:
├── 基础架构     ████████████ 100%
├── 用户服务     ████████████ 100%
├── 红花服务     ████████████ 100%
├── 日志服务     ████████████ 100%
├── 前端应用     ██████████░░ 85%
├── 任务服务     ░░░░░░░░░░░░ 0%
├── 班级服务     ░░░░░░░░░░░░ 0%
├── 奖励服务     ░░░░░░░░░░░░ 0%
├── 消息服务     ░░░░░░░░░░░░ 0%
└── 网关服务     ░░░░░░░░░░░░ 0%
```

### 🎯 下一步开发建议

1. **优先级1**: 完成任务服务 - 这是系统的核心功能
2. **优先级2**: 开发奖励服务 - 与任务服务配套使用
3. **优先级3**: 实现班级服务 - 扩展到教育场景
4. **优先级4**: 完善消息服务 - 提升用户体验
5. **优先级5**: 部署网关服务 - 生产环境必需

## ⚠️ 注意事项

1. **数据安全**: 生产环境请修改默认密码和密钥
2. **网络配置**: 确保各服务间网络连通性
3. **资源配置**: 根据实际负载调整JVM参数
4. **备份策略**: 定期备份数据库和重要配置

## 📞 技术支持

- 📧 邮箱: support@redflower.com
- 📱 微信群: 扫码加入技术交流群
- 🐛 问题反馈: [GitHub Issues](https://github.com/your-repo/red-flower/issues)

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

感谢所有为本项目贡献代码和建议的开发者们！

---

**让成长变得更有趣！** 🌟