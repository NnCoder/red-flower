package com.redflower.common.enums;

/**
 * 任务类型枚举
 */
public enum TaskType {
    STUDY("STUDY", "学习任务"),
    BEHAVIOR("BEHAVIOR", "行为任务"),
    HABIT("HABIT", "习惯任务");

    private final String code;
    private final String name;

    TaskType(String code, String name) {
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