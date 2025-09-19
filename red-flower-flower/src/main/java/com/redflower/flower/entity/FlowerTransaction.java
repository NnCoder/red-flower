package com.redflower.flower.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.redflower.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 红花交易记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("flower_transaction")
public class FlowerTransaction extends BaseEntity {

    /**
     * 交易号
     */
    @TableField("transaction_no")
    private String transactionNo;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 交易类型
     */
    @TableField("type")
    private String type;

    /**
     * 花朵类型
     */
    @TableField("flower_type")
    private String flowerType;

    /**
     * 交易数量
     */
    @TableField("amount")
    private Integer amount;

    /**
     * 交易后余额
     */
    @TableField("balance_after")
    private Integer balanceAfter;

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
     * 交易描述
     */
    @TableField("description")
    private String description;
}