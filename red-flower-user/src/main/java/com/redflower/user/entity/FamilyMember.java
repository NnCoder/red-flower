package com.redflower.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.redflower.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 家庭成员实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("family_member")
public class FamilyMember extends BaseEntity {

    /**
     * 家庭ID
     */
    @TableField("family_id")
    private Long familyId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 家庭角色
     */
    @TableField("role")
    private String role;

    /**
     * 关系
     */
    @TableField("relation")
    private String relation;

    /**
     * 加入时间
     */
    @TableField("join_time")
    private LocalDateTime joinTime;
}