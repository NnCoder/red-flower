-- 创建数据库
CREATE DATABASE IF NOT EXISTS red_flower DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE red_flower;

-- 用户表
CREATE TABLE `user` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `username` varchar(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) COMMENT '昵称',
  `avatar` varchar(200) COMMENT '头像',
  `phone` varchar(20) COMMENT '手机号',
  `email` varchar(100) COMMENT '邮箱',
  `role` varchar(20) NOT NULL COMMENT '角色:ADMIN,PARENT,CHILD,TEACHER,STUDENT',
  `status` tinyint DEFAULT 1 COMMENT '状态:0禁用,1启用',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 家庭表
CREATE TABLE `family` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '家庭名称',
  `description` text COMMENT '家庭描述',
  `creator_id` bigint NOT NULL COMMENT '创建者ID',
  `flower_total` int DEFAULT 0 COMMENT '红花总量',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 家庭成员关系表
CREATE TABLE `family_member` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `family_id` bigint NOT NULL COMMENT '家庭ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` varchar(20) NOT NULL COMMENT '家庭角色:PARENT,CHILD',
  `relation` varchar(20) COMMENT '关系:父亲,母亲,孩子等',
  `join_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_family_user` (`family_id`, `user_id`)
);

-- 学校表
CREATE TABLE `school` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '学校名称',
  `address` varchar(200) COMMENT '学校地址',
  `contact_phone` varchar(20) COMMENT '联系电话',
  `description` text COMMENT '学校描述',
  `flower_total` int DEFAULT 0 COMMENT '学校红花总量',
  `status` tinyint DEFAULT 1 COMMENT '状态:0禁用,1启用',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 班级表
CREATE TABLE `class` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `school_id` bigint NOT NULL COMMENT '学校ID',
  `name` varchar(100) NOT NULL COMMENT '班级名称',
  `grade` varchar(20) COMMENT '年级',
  `description` text COMMENT '班级描述',
  `teacher_id` bigint NOT NULL COMMENT '班主任ID',
  `max_students` int DEFAULT 50 COMMENT '最大学生数',
  `flower_pool` int DEFAULT 0 COMMENT '班级红花池',
  `status` tinyint DEFAULT 1 COMMENT '状态:0禁用,1启用',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 班级成员表
CREATE TABLE `class_member` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `student_number` varchar(20) COMMENT '学号',
  `join_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint DEFAULT 1 COMMENT '状态:0退出,1在读',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_class_student` (`class_id`, `student_id`)
);

-- 学习小组表
CREATE TABLE `study_group` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `name` varchar(50) NOT NULL COMMENT '小组名称',
  `description` varchar(200) COMMENT '小组描述',
  `leader_id` bigint COMMENT '组长ID',
  `max_members` int DEFAULT 6 COMMENT '最大成员数',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 小组成员表
CREATE TABLE `group_member` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `group_id` bigint NOT NULL COMMENT '小组ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `role` varchar(20) DEFAULT 'MEMBER' COMMENT '角色:LEADER,MEMBER',
  `join_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_group_student` (`group_id`, `student_id`)
);

-- 红花账户表
CREATE TABLE `flower_account` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL UNIQUE COMMENT '用户ID',
  `red_flower_balance` int DEFAULT 0 COMMENT '红花余额',
  `black_flower_balance` int DEFAULT 0 COMMENT '黑花余额',
  `total_earned` int DEFAULT 0 COMMENT '累计获得',
  `total_spent` int DEFAULT 0 COMMENT '累计消费',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 红花交易记录表
CREATE TABLE `flower_transaction` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `transaction_no` varchar(50) NOT NULL UNIQUE COMMENT '交易号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` varchar(20) NOT NULL COMMENT '交易类型:EARN,SPEND,TRANSFER_IN,TRANSFER_OUT',
  `flower_type` varchar(10) NOT NULL COMMENT '花朵类型:RED,BLACK',
  `amount` int NOT NULL COMMENT '交易数量',
  `balance_after` int NOT NULL COMMENT '交易后余额',
  `related_id` bigint COMMENT '关联业务ID',
  `related_type` varchar(20) COMMENT '关联业务类型:TASK,REWARD,TRANSFER',
  `description` varchar(200) COMMENT '交易描述',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
);

-- 红花转账记录表
CREATE TABLE `flower_transfer` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `transfer_no` varchar(50) NOT NULL UNIQUE COMMENT '转账号',
  `from_user_id` bigint NOT NULL COMMENT '转出用户ID',
  `to_user_id` bigint NOT NULL COMMENT '转入用户ID',
  `amount` int NOT NULL COMMENT '转账数量',
  `status` varchar(20) DEFAULT 'SUCCESS' COMMENT '状态:SUCCESS,FAILED',
  `remark` varchar(200) COMMENT '备注',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 任务表
CREATE TABLE `task` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `title` varchar(100) NOT NULL COMMENT '任务标题',
  `description` text COMMENT '任务描述',
  `creator_id` bigint NOT NULL COMMENT '创建者ID(家长/老师)',
  `assignee_id` bigint COMMENT '执行者ID(孩子/学生，个人任务时使用)',
  `family_id` bigint COMMENT '家庭ID(家庭任务)',
  `class_id` bigint COMMENT '班级ID(班级任务)',
  `group_id` bigint COMMENT '小组ID(小组任务)',
  `type` varchar(20) NOT NULL COMMENT '任务类型:STUDY,BEHAVIOR,HABIT',
  `scope` varchar(20) NOT NULL COMMENT '任务范围:FAMILY,CLASS,GROUP,INDIVIDUAL',
  `category` varchar(50) COMMENT '任务分类',
  `reward_flowers` int NOT NULL COMMENT '奖励红花数',
  `is_recurring` tinyint DEFAULT 0 COMMENT '是否循环:0否,1是',
  `recurring_rule` varchar(100) COMMENT '循环规则:DAILY,WEEKLY,CUSTOM',
  `start_time` datetime COMMENT '开始时间',
  `end_time` datetime COMMENT '结束时间',
  `status` varchar(20) DEFAULT 'ACTIVE' COMMENT '状态:ACTIVE,COMPLETED,CANCELLED',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 活动日志表
CREATE TABLE `activity_log` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `activity_type` varchar(30) NOT NULL COMMENT '活动类型:TASK_COMPLETE,FLOWER_EARN,REWARD_PURCHASE,ACHIEVEMENT_UNLOCK',
  `activity_scope` varchar(20) COMMENT '活动范围:FAMILY,CLASS,GROUP',
  `title` varchar(200) NOT NULL COMMENT '活动标题',
  `description` text COMMENT '活动描述',
  `related_id` bigint COMMENT '关联业务ID',
  `related_type` varchar(30) COMMENT '关联业务类型:TASK,REWARD,ACHIEVEMENT',
  `metadata` json COMMENT '扩展数据',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
);

-- 里程碑记录表
CREATE TABLE `milestone` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` varchar(30) NOT NULL COMMENT '里程碑类型:FIRST_TASK,FLOWER_MILESTONE,CONSECUTIVE_DAYS',
  `title` varchar(100) NOT NULL COMMENT '里程碑标题',
  `description` text COMMENT '里程碑描述',
  `icon_url` varchar(200) COMMENT '图标URL',
  `badge_color` varchar(20) COMMENT '徽章颜色',
  `unlock_condition` varchar(200) COMMENT '解锁条件',
  `unlock_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_special` tinyint DEFAULT 0 COMMENT '是否特殊里程碑:0否,1是',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 成长报告表
CREATE TABLE `growth_report` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `report_type` varchar(20) NOT NULL COMMENT '报告类型:DAILY,WEEKLY,MONTHLY,YEARLY',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `report_data` json NOT NULL COMMENT '报告数据',
  `summary` text COMMENT '报告摘要',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
);

-- 奖励商品表
CREATE TABLE `reward_item` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '奖励名称',
  `description` text COMMENT '奖励描述',
  `icon_url` varchar(200) COMMENT '图标URL',
  `category` varchar(50) COMMENT '奖励分类',
  `red_flower_cost` int DEFAULT 0 COMMENT '红花消费',
  `black_flower_cost` int DEFAULT 0 COMMENT '黑花消费',
  `creator_id` bigint NOT NULL COMMENT '创建者ID(家长/老师)',
  `family_id` bigint COMMENT '家庭ID(家庭奖励)',
  `class_id` bigint COMMENT '班级ID(班级奖励)',
  `scope` varchar(20) NOT NULL COMMENT '范围:FAMILY,CLASS',
  `is_active` tinyint DEFAULT 1 COMMENT '是否有效:0无效,1有效',
  `flower_recycle` tinyint DEFAULT 1 COMMENT '是否回收红花:0否,1是',
  `recycle_rate` decimal(3,2) DEFAULT 1.00 COMMENT '回收比例(0.00-1.00)',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 奖励兑换记录表
CREATE TABLE `reward_exchange` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `exchange_no` varchar(50) NOT NULL UNIQUE COMMENT '兑换号',
  `user_id` bigint NOT NULL COMMENT '兑换用户ID',
  `reward_id` bigint NOT NULL COMMENT '奖励ID',
  `red_flower_cost` int DEFAULT 0 COMMENT '消费红花数',
  `black_flower_cost` int DEFAULT 0 COMMENT '消费黑花数',
  `recycle_amount` int DEFAULT 0 COMMENT '回收红花数',
  `recycle_to_user_id` bigint COMMENT '回收给用户ID(奖励发布者)',
  `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态:PENDING,COMPLETED,USED,CANCELLED',
  `remark` varchar(200) COMMENT '备注',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 家庭权限配置表
CREATE TABLE `family_permission` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `family_id` bigint NOT NULL COMMENT '家庭ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `permission_key` varchar(50) NOT NULL COMMENT '权限键',
  `permission_value` varchar(100) NOT NULL COMMENT '权限值',
  `description` varchar(200) COMMENT '权限描述',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_family_user_permission` (`family_id`, `user_id`, `permission_key`)
);

-- 黑花消除记录表
CREATE TABLE `black_flower_elimination` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `elimination_no` varchar(50) NOT NULL UNIQUE COMMENT '消除号',
  `target_user_id` bigint NOT NULL COMMENT '目标用户ID(被消除黑花的用户)',
  `operator_user_id` bigint NOT NULL COMMENT '操作者ID(家长)',
  `amount` int NOT NULL COMMENT '消除数量',
  `reason` varchar(200) COMMENT '消除原因',
  `family_id` bigint COMMENT '家庭ID',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除:0正常,1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP
);

-- 插入初始数据
-- 所有用户默认密码: admin123 (BCrypt加密)
-- 使用从实际系统生成的确认有效的哈希值
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', '$2a$10$3SoRzbGHNXst/ANPdBQYZOeuDU3r4Sca/NDNsKjUE/BMSX2u9C9Q6', '管理员', 'ADMIN', 1),
('parent1', '$2a$10$3SoRzbGHNXst/ANPdBQYZOeuDU3r4Sca/NDNsKjUE/BMSX2u9C9Q6', '爸爸', 'PARENT', 1),
('parent2', '$2a$10$3SoRzbGHNXst/ANPdBQYZOeuDU3r4Sca/NDNsKjUE/BMSX2u9C9Q6', '妈妈', 'PARENT', 1),
('child1', '$2a$10$3SoRzbGHNXst/ANPdBQYZOeuDU3r4Sca/NDNsKjUE/BMSX2u9C9Q6', '小明', 'CHILD', 1),
('child2', '$2a$10$3SoRzbGHNXst/ANPdBQYZOeuDU3r4Sca/NDNsKjUE/BMSX2u9C9Q6', '小红', 'CHILD', 1);

INSERT INTO `family` (`name`, `description`, `creator_id`, `flower_total`) VALUES
('幸福之家', '一个温馨的家庭', 2, 1000);

INSERT INTO `family_member` (`family_id`, `user_id`, `role`, `relation`) VALUES
(1, 2, 'PARENT', '父亲'),
(1, 3, 'PARENT', '母亲'),
(1, 4, 'CHILD', '儿子'),
(1, 5, 'CHILD', '女儿');

INSERT INTO `flower_account` (`user_id`, `red_flower_balance`, `black_flower_balance`, `total_earned`, `total_spent`) VALUES
(2, 50, 0, 100, 50),
(3, 45, 0, 95, 50),
(4, 15, 2, 25, 10),
(5, 20, 0, 30, 10);

-- 初始化家庭权限配置
INSERT INTO `family_permission` (`family_id`, `user_id`, `permission_key`, `permission_value`, `description`) VALUES
(1, 2, 'BLACK_FLOWER_ELIMINATION', 'true', '允许消除黑花'),
(1, 3, 'BLACK_FLOWER_ELIMINATION', 'true', '允许消除黑花'),
(1, 2, 'FLOWER_MANAGEMENT', 'true', '红花管理权限'),
(1, 3, 'FLOWER_MANAGEMENT', 'true', '红花管理权限');

-- 初始化奖励商品
INSERT INTO `reward_item` (`name`, `description`, `category`, `red_flower_cost`, `creator_id`, `family_id`, `scope`, `flower_recycle`, `recycle_rate`) VALUES
('额外游戏时间', '获得额外30分钟游戏时间', 'time', 10, 2, 1, 'FAMILY', 1, 1.00),
('选择今日菜单', '可以选择今天晚餐吃什么', 'privilege', 15, 2, 1, 'FAMILY', 1, 1.00),
('小礼品', '获得一个小礼品', 'gift', 20, 2, 1, 'FAMILY', 1, 0.80),
('延迟睡觉时间', '今晚可以晚睡30分钟', 'time', 12, 3, 1, 'FAMILY', 1, 1.00),
('零花钱', '获得10元零花钱', 'money', 25, 2, 1, 'FAMILY', 1, 0.60);