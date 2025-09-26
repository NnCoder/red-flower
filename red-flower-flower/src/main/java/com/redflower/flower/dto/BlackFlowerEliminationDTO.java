package com.redflower.flower.dto;

import lombok.Data;

/**
 * 黑花消除DTO
 */
@Data
public class BlackFlowerEliminationDTO {

    /**
     * 目标用户ID(被消除黑花的用户)
     */
    private Long targetUserId;

    /**
     * 消除数量
     */
    private Integer amount;

    /**
     * 消除原因
     */
    private String reason;

    /**
     * 家庭ID
     */
    private Long familyId;
}