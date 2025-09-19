package com.redflower.log.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 时间线VO
 */
@Data
public class TimelineVO {

    private Long id;

    private String activityType;

    private String activityScope;

    private String title;

    private String description;

    private String metadata;

    private LocalDateTime createTime;
}