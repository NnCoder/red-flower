package com.redflower.log.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 活动日志DTO
 */
@Data
public class ActivityLogDTO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "活动类型不能为空")
    private String activityType;

    private String activityScope;

    @NotBlank(message = "活动标题不能为空")
    private String title;

    private String description;

    private Long relatedId;

    private String relatedType;

    private String metadata;
}