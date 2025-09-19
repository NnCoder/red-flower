package com.redflower.log.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

/**
 * 成长报告VO
 */
@Data
public class GrowthReportVO {

    private Long id;

    private Long userId;

    private String reportType;

    private LocalDate startDate;

    private LocalDate endDate;

    private Map<String, Object> reportData;

    private String summary;
}