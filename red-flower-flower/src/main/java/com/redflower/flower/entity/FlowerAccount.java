package com.redflower.flower.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.redflower.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 红花账户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("flower_account")
public class FlowerAccount extends BaseEntity {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 红花余额
     */
    @TableField("red_flower_balance")
    private Integer redFlowerBalance;

    /**
     * 黑花余额
     */
    @TableField("black_flower_balance")
    private Integer blackFlowerBalance;

    /**
     * 累计获得
     */
    @TableField("total_earned")
    private Integer totalEarned;

    /**
     * 累计消费
     */
    @TableField("total_spent")
    private Integer totalSpent;
}