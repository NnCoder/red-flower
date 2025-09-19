package com.redflower.log.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.redflower.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动日志实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("activity_log")
public class ActivityLog extends BaseEntity {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 活动类型
     */
    @TableField("activity_type")
    private String activityType;

    /**
     * 活动范围
     */
    @TableField("activity_scope")
    private String activityScope;

    /**
     * 活动标题
     */
    @TableField("title")
    private String title;

    /**
     * 活动描述
     */
    @TableField("description")
    private String description;

    /**
     * 关联业务ID
     */
    @TableField("related_id")
    private Long relatedId;

    /**
     * 关联业务类型
     */
    @TableField("related_type")
    private String relatedType;

    /**
     * 扩展数据
     */
    @TableField("metadata")
    private String metadata;
}