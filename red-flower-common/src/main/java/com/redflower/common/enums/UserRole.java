package com.redflower.common.enums;

/**
 * 用户角色枚举
 */
public enum UserRole {
    ADMIN("ADMIN", "管理员"),
    PARENT("PARENT", "家长"),
    CHILD("CHILD", "孩子"),
    TEACHER("TEACHER", "老师"),
    STUDENT("STUDENT", "学生");

    private final String code;
    private final String name;

    UserRole(String code, String name) {
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