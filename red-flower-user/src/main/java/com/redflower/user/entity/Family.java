package com.redflower.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.redflower.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 家庭实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("family")
public class Family extends BaseEntity {

    /**
     * 家庭名称
     */
    @TableField("name")
    private String name;

    /**
     * 家庭描述
     */
    @TableField("description")
    private String description;

    /**
     * 创建者ID
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 红花总量
     */
    @TableField("flower_total")
    private Integer flowerTotal;
}