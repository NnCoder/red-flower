package com.redflower.flower.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.redflower.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 奖励商品实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("reward_item")
public class RewardItem extends BaseEntity {

    /**
     * 奖励名称
     */
    @TableField("name")
    private String name;

    /**
     * 奖励描述
     */
    @TableField("description")
    private String description;

    /**
     * 图标URL
     */
    @TableField("icon_url")
    private String iconUrl;

    /**
     * 奖励分类
     */
    @TableField("category")
    private String category;

    /**
     * 红花消费
     */
    @TableField("red_flower_cost")
    private Integer redFlowerCost;

    /**
     * 黑花消费
     */
    @TableField("black_flower_cost")
    private Integer blackFlowerCost;

    /**
     * 创建者ID(家长/老师)
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 家庭ID(家庭奖励)
     */
    @TableField("family_id")
    private Long familyId;

    /**
     * 班级ID(班级奖励)
     */
    @TableField("class_id")
    private Long classId;

    /**
     * 范围
     */
    @TableField("scope")
    private String scope;

    /**
     * 是否有效
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 是否回收红花
     */
    @TableField("flower_recycle")
    private Boolean flowerRecycle;

    /**
     * 回收比例
     */
    @TableField("recycle_rate")
    private BigDecimal recycleRate;
}