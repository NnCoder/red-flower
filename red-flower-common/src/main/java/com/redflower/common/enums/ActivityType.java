package com.redflower.common.enums;

/**
 * 活动类型枚举
 */
public enum ActivityType {
    TASK_COMPLETE("TASK_COMPLETE", "任务完成"),
    FLOWER_EARN("FLOWER_EARN", "获得红花"),
    FLOWER_SPEND("FLOWER_SPEND", "消费红花"),
    REWARD_PURCHASE("REWARD_PURCHASE", "购买奖励"),
    ACHIEVEMENT_UNLOCK("ACHIEVEMENT_UNLOCK", "解锁成就"),
    MILESTONE_REACH("MILESTONE_REACH", "达成里程碑"),
    CLASS_JOIN("CLASS_JOIN", "加入班级"),
    GROUP_JOIN("GROUP_JOIN", "加入小组");

    private final String code;
    private final String name;

    ActivityType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}