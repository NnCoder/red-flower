package com.redflower.log.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.redflower.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 里程碑实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("milestone")
public class Milestone extends BaseEntity {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 里程碑类型
     */
    @TableField("type")
    private String type;

    /**
     * 里程碑标题
     */
    @TableField("title")
    private String title;

    /**
     * 里程碑描述
     */
    @TableField("description")
    private String description;

    /**
     * 图标URL
     */
    @TableField("icon_url")
    private String iconUrl;

    /**
     * 徽章颜色
     */
    @TableField("badge_color")
    private String badgeColor;

    /**
     * 解锁条件
     */
    @TableField("unlock_condition")
    private String unlockCondition;

    /**
     * 解锁时间
     */
    @TableField("unlock_time")
    private LocalDateTime unlockTime;

    /**
     * 是否特殊里程碑
     */
    @TableField("is_special")
    private Integer isSpecial;
}