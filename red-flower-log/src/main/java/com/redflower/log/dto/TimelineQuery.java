package com.redflower.log.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 时间线查询DTO
 */
@Data
public class TimelineQuery {

    private Long userId;

    private LocalDate startDate;

    private LocalDate endDate;

    private String activityType;

    private String activityScope;

    private Integer page = 1;

    private Integer size = 20;
}