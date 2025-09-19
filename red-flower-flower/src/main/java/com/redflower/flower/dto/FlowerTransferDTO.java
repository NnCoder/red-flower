package com.redflower.flower.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 红花转账DTO
 */
@Data
public class FlowerTransferDTO {

    @NotNull(message = "转出用户不能为空")
    private Long fromUserId;

    @NotNull(message = "转入用户不能为空")
    private Long toUserId;

    @NotNull(message = "转账数量不能为空")
    @Positive(message = "转账数量必须大于0")
    private Integer amount;

    private String remark;
}