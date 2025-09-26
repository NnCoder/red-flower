package com.redflower.flower.dto;

import lombok.Data;

/**
 * 奖励兑换DTO
 */
@Data
public class RewardExchangeDTO {

    /**
     * 奖励ID
     */
    private Long rewardId;

    /**
     * 兑换用户ID
     */
    private Long userId;

    /**
     * 备注
     */
    private String remark;
}