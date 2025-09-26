package com.redflower.flower.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.redflower.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 奖励兑换记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("reward_exchange")
public class RewardExchange extends BaseEntity {

    /**
     * 兑换号
     */
    @TableField("exchange_no")
    private String exchangeNo;

    /**
     * 兑换用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 奖励ID
     */
    @TableField("reward_id")
    private Long rewardId;

    /**
     * 消费红花数
     */
    @TableField("red_flower_cost")
    private Integer redFlowerCost;

    /**
     * 消费黑花数
     */
    @TableField("black_flower_cost")
    private Integer blackFlowerCost;

    /**
     * 回收红花数
     */
    @TableField("recycle_amount")
    private Integer recycleAmount;

    /**
     * 回收给用户ID(奖励发布者)
     */
    @TableField("recycle_to_user_id")
    private Long recycleToUserId;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}