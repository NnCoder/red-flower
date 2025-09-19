package com.redflower.flower.vo;

import lombok.Data;

/**
 * 红花账户VO
 */
@Data
public class FlowerAccountVO {

    private Long userId;

    private Integer redFlowerBalance;

    private Integer blackFlowerBalance;

    private Integer totalEarned;

    private Integer totalSpent;
}