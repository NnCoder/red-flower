package com.redflower.flower.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.redflower.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 黑花消除记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("black_flower_elimination")
public class BlackFlowerElimination extends BaseEntity {

    /**
     * 消除号
     */
    @TableField("elimination_no")
    private String eliminationNo;

    /**
     * 目标用户ID(被消除黑花的用户)
     */
    @TableField("target_user_id")
    private Long targetUserId;

    /**
     * 操作者ID(家长)
     */
    @TableField("operator_user_id")
    private Long operatorUserId;

    /**
     * 消除数量
     */
    @TableField("amount")
    private Integer amount;

    /**
     * 消除原因
     */
    @TableField("reason")
    private String reason;

    /**
     * 家庭ID
     */
    @TableField("family_id")
    private Long familyId;
}