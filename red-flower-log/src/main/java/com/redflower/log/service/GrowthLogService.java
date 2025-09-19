package com.redflower.log.service;

import com.redflower.common.result.PageResult;
import com.redflower.log.dto.ActivityLogDTO;
import com.redflower.log.dto.TimelineQuery;
import com.redflower.log.vo.GrowthReportVO;
import com.redflower.log.vo.TimelineVO;

import java.time.LocalDate;

/**
 * 成长日志服务接口
 */
public interface GrowthLogService {

    /**
     * 记录活动日志
     */
    void recordActivity(ActivityLogDTO logDTO);

    /**
     * 生成时间线
     */
    PageResult<TimelineVO> generateTimeline(TimelineQuery query);

    /**
     * 检查并解锁里程碑
     */
    void checkAndUnlockMilestone(Long userId, String milestoneType);

    /**
     * 生成成长报告
     */
    GrowthReportVO generateGrowthReport(Long userId, String reportType,
                                       LocalDate startDate, LocalDate endDate);

    /**
     * 导出成长报告PDF
     */
    byte[] exportGrowthReportPdf(Long userId, Long reportId);

    /**
     * 获取用户里程碑列表
     */
    PageResult<Object> getUserMilestones(Long userId, Integer page, Integer size);
}