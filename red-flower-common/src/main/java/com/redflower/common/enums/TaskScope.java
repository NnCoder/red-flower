package com.redflower.common.enums;

/**
 * 任务范围枚举
 */
public enum TaskScope {
    FAMILY("FAMILY", "家庭任务"),
    CLASS("CLASS", "班级任务"),
    GROUP("GROUP", "小组任务"),
    INDIVIDUAL("INDIVIDUAL", "个人任务");

    private final String code;
    private final String name;

    TaskScope(String code, String name) {
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