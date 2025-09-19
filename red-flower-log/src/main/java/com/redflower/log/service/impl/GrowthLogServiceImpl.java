package com.redflower.log.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.redflower.common.result.PageResult;
import com.redflower.log.dto.ActivityLogDTO;
import com.redflower.log.dto.TimelineQuery;
import com.redflower.log.entity.ActivityLog;
import com.redflower.log.entity.GrowthReport;
import com.redflower.log.entity.Milestone;
import com.redflower.log.mapper.ActivityLogMapper;
import com.redflower.log.mapper.GrowthReportMapper;
import com.redflower.log.mapper.MilestoneMapper;
import com.redflower.log.service.GrowthLogService;
import com.redflower.log.vo.GrowthReportVO;
import com.redflower.log.vo.TimelineVO;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 成长日志服务实现
 */
@Service
@RequiredArgsConstructor
public class GrowthLogServiceImpl implements GrowthLogService {

    private final ActivityLogMapper activityLogMapper;
    private final MilestoneMapper milestoneMapper;
    private final GrowthReportMapper growthReportMapper;

    @Override
    @Async
    public void recordActivity(ActivityLogDTO logDTO) {
        ActivityLog activityLog = new ActivityLog();
        BeanUtil.copyProperties(logDTO, activityLog);
        activityLog.setCreateTime(LocalDateTime.now());
        activityLogMapper.insert(activityLog);

        // 检查里程碑触发条件
        checkAndUnlockMilestone(logDTO.getUserId(), logDTO.getActivityType());
    }

    @Override
    public PageResult<TimelineVO> generateTimeline(TimelineQuery query) {
        LambdaQueryWrapper<ActivityLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityLog::getUserId, query.getUserId())
               .ge(query.getStartDate() != null, ActivityLog::getCreateTime, query.getStartDate())
               .le(query.getEndDate() != null, ActivityLog::getCreateTime, query.getEndDate())
               .eq(query.getActivityType() != null, ActivityLog::getActivityType, query.getActivityType())
               .eq(query.getActivityScope() != null, ActivityLog::getActivityScope, query.getActivityScope())
               .orderByDesc(ActivityLog::getCreateTime);

        Page<ActivityLog> page = new Page<>(query.getPage(), query.getSize());
        Page<ActivityLog> result = activityLogMapper.selectPage(page, wrapper);

        List<TimelineVO> timelineList = result.getRecords().stream()
                .map(log -> {
                    TimelineVO vo = new TimelineVO();
                    BeanUtil.copyProperties(log, vo);
                    return vo;
                }).collect(Collectors.toList());

        return new PageResult<>(timelineList, result.getTotal(),
                               result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional
    public void checkAndUnlockMilestone(Long userId, String milestoneType) {
        // 检查是否已解锁该类型里程碑
        LambdaQueryWrapper<Milestone> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Milestone::getUserId, userId)
               .eq(Milestone::getType, milestoneType);

        Milestone existMilestone = milestoneMapper.selectOne(wrapper);
        if (existMilestone != null) {
            return; // 已解锁
        }

        // 获取用户统计数据并检查解锁条件
        boolean shouldUnlock = checkMilestoneCondition(userId, milestoneType);

        if (shouldUnlock) {
            Milestone milestone = new Milestone();
            milestone.setUserId(userId);
            milestone.setType(milestoneType);
            milestone.setTitle(getMilestoneTitle(milestoneType));
            milestone.setDescription(getMilestoneDescription(milestoneType));
            milestone.setIconUrl(getMilestoneIcon(milestoneType));
            milestone.setBadgeColor(getMilestoneBadgeColor(milestoneType));
            milestone.setUnlockCondition(getMilestoneCondition(milestoneType));
            milestone.setUnlockTime(LocalDateTime.now());
            milestone.setIsSpecial(isSpecialMilestone(milestoneType) ? 1 : 0);
            milestone.setCreateTime(LocalDateTime.now());

            milestoneMapper.insert(milestone);

            // 记录里程碑解锁活动
            ActivityLogDTO logDTO = new ActivityLogDTO();
            logDTO.setUserId(userId);
            logDTO.setActivityType("MILESTONE_REACH");
            logDTO.setTitle("解锁里程碑：" + milestone.getTitle());
            logDTO.setDescription("恭喜解锁新里程碑！");
            logDTO.setRelatedId(milestone.getId());
            logDTO.setRelatedType("MILESTONE");
            recordActivity(logDTO);
        }
    }

    @Override
    public GrowthReportVO generateGrowthReport(Long userId, String reportType,
                                             LocalDate startDate, LocalDate endDate) {
        // 查询时间段内的活动数据
        LambdaQueryWrapper<ActivityLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityLog::getUserId, userId)
               .ge(ActivityLog::getCreateTime, startDate.atStartOfDay())
               .le(ActivityLog::getCreateTime, endDate.atTime(23, 59, 59));

        List<ActivityLog> activities = activityLogMapper.selectList(wrapper);

        // 统计各项指标
        Map<String, Object> reportData = generateReportData(activities, reportType);
        String summary = generateReportSummary(reportData, reportType);

        // 保存报告记录
        GrowthReport report = new GrowthReport();
        report.setUserId(userId);
        report.setReportType(reportType);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setReportData(JSON.toJSONString(reportData));
        report.setSummary(summary);
        report.setCreateTime(LocalDateTime.now());
        growthReportMapper.insert(report);

        // 转换为VO
        GrowthReportVO vo = new GrowthReportVO();
        BeanUtil.copyProperties(report, vo);
        vo.setReportData(reportData);
        return vo;
    }

    @Override
    public byte[] exportGrowthReportPdf(Long userId, Long reportId) {
        // 获取报告数据
        GrowthReport report = growthReportMapper.selectById(reportId);
        if (report == null || !report.getUserId().equals(userId)) {
            throw new RuntimeException("报告不存在或无权限访问");
        }

        // 生成PDF（这里简化实现，实际项目中需要使用PDF库）
        return generatePdfReport(report);
    }

    @Override
    public PageResult<Object> getUserMilestones(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Milestone> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Milestone::getUserId, userId)
               .orderByDesc(Milestone::getUnlockTime);

        Page<Milestone> milestones = milestoneMapper.selectPage(new Page<>(page, size), wrapper);

        return new PageResult<>(milestones.getRecords(), milestones.getTotal(),
                              milestones.getCurrent(), milestones.getSize());
    }

    private boolean checkMilestoneCondition(Long userId, String milestoneType) {
        // 根据里程碑类型检查条件
        switch (milestoneType) {
            case "FIRST_TASK":
                return getTaskCompletedCount(userId) >= 1;
            case "FLOWER_MILESTONE_10":
                return getTotalFlowersEarned(userId) >= 10;
            case "CONSECUTIVE_DAYS_7":
                return getConsecutiveDays(userId) >= 7;
            default:
                return false;
        }
    }

    private int getTaskCompletedCount(Long userId) {
        LambdaQueryWrapper<ActivityLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityLog::getUserId, userId)
               .eq(ActivityLog::getActivityType, "TASK_COMPLETE");
        return Math.toIntExact(activityLogMapper.selectCount(wrapper));
    }

    private int getTotalFlowersEarned(Long userId) {
        // 这里需要调用红花服务获取数据，简化返回固定值
        return 15;
    }

    private int getConsecutiveDays(Long userId) {
        // 计算连续天数，简化返回固定值
        return 7;
    }

    private Map<String, Object> generateReportData(List<ActivityLog> activities, String reportType) {
        Map<String, Object> data = new HashMap<>();

        // 按活动类型统计
        Map<String, Long> activityTypeCount = activities.stream()
                .collect(Collectors.groupingBy(ActivityLog::getActivityType, Collectors.counting()));

        data.put("activityTypeCount", activityTypeCount);
        data.put("totalActivities", activities.size());
        data.put("activeDays", activities.stream()
                .map(log -> log.getCreateTime().toLocalDate())
                .distinct().count());

        return data;
    }

    private String generateReportSummary(Map<String, Object> reportData, String reportType) {
        return String.format("本%s共记录了%d项活动，活跃天数%d天。",
                getReportTypeName(reportType),
                reportData.get("totalActivities"),
                reportData.get("activeDays"));
    }

    private byte[] generatePdfReport(GrowthReport report) {
        // 简化实现，实际需要使用iText等PDF库
        String content = String.format("成长报告\n\n用户ID: %d\n报告类型: %s\n开始日期: %s\n结束日期: %s\n\n%s",
                report.getUserId(), report.getReportType(),
                report.getStartDate(), report.getEndDate(), report.getSummary());
        return content.getBytes();
    }

    private String getMilestoneTitle(String type) {
        switch (type) {
            case "FIRST_TASK": return "初次尝试";
            case "FLOWER_MILESTONE_10": return "小有成就";
            case "CONSECUTIVE_DAYS_7": return "坚持不懈";
            default: return "成就解锁";
        }
    }

    private String getMilestoneDescription(String type) {
        switch (type) {
            case "FIRST_TASK": return "完成了第一个任务";
            case "FLOWER_MILESTONE_10": return "累计获得10朵红花";
            case "CONSECUTIVE_DAYS_7": return "连续7天保持活跃";
            default: return "解锁了新成就";
        }
    }

    private String getMilestoneIcon(String type) {
        return "/icons/milestone-" + type.toLowerCase() + ".png";
    }

    private String getMilestoneBadgeColor(String type) {
        switch (type) {
            case "FIRST_TASK": return "#4CAF50";
            case "FLOWER_MILESTONE_10": return "#FF9800";
            case "CONSECUTIVE_DAYS_7": return "#2196F3";
            default: return "#9C27B0";
        }
    }

    private String getMilestoneCondition(String type) {
        switch (type) {
            case "FIRST_TASK": return "完成1个任务";
            case "FLOWER_MILESTONE_10": return "获得10朵红花";
            case "CONSECUTIVE_DAYS_7": return "连续活跃7天";
            default: return "满足特定条件";
        }
    }

    private boolean isSpecialMilestone(String type) {
        return "CONSECUTIVE_DAYS_7".equals(type);
    }

    private String getReportTypeName(String reportType) {
        switch (reportType) {
            case "DAILY": return "日";
            case "WEEKLY": return "周";
            case "MONTHLY": return "月";
            case "YEARLY": return "年";
            default: return "期间";
        }
    }
}