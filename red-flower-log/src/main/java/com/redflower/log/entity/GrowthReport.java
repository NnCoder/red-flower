package com.redflower.log.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.redflower.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 成长报告实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("growth_report")
public class GrowthReport extends BaseEntity {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 报告类型
     */
    @TableField("report_type")
    private String reportType;

    /**
     * 开始日期
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 报告数据
     */
    @TableField("report_data")
    private String reportData;

    /**
     * 报告摘要
     */
    @TableField("summary")
    private String summary;
}