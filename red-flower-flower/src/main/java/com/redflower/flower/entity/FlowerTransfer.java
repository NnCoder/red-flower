package com.redflower.flower.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.redflower.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 红花转账记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("flower_transfer")
public class FlowerTransfer extends BaseEntity {

    /**
     * 转账号
     */
    @TableField("transfer_no")
    private String transferNo;

    /**
     * 转出用户ID
     */
    @TableField("from_user_id")
    private Long fromUserId;

    /**
     * 转入用户ID
     */
    @TableField("to_user_id")
    private Long toUserId;

    /**
     * 转账数量
     */
    @TableField("amount")
    private Integer amount;

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