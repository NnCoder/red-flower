package com.redflower.log.controller;

import com.redflower.common.result.PageResult;
import com.redflower.common.result.Result;
import com.redflower.log.dto.ActivityLogDTO;
import com.redflower.log.dto.TimelineQuery;
import com.redflower.log.service.GrowthLogService;
import com.redflower.log.vo.GrowthReportVO;
import com.redflower.log.vo.TimelineVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 成长日志控制器
 */
@Tag(name = "成长日志", description = "活动日志、时间线、里程碑、成长报告")
@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class GrowthLogController {

    private final GrowthLogService growthLogService;

    @Operation(summary = "记录活动日志")
    @PostMapping("/activity")
    public Result<Void> recordActivity(@Valid @RequestBody ActivityLogDTO dto) {
        growthLogService.recordActivity(dto);
        return Result.success("记录成功");
    }

    @Operation(summary = "生成时间线")
    @PostMapping("/timeline")
    public Result<PageResult<TimelineVO>> generateTimeline(@RequestBody TimelineQuery query) {
        PageResult<TimelineVO> timeline = growthLogService.generateTimeline(query);
        return Result.success(timeline);
    }

    @Operation(summary = "获取用户里程碑")
    @GetMapping("/milestones/{userId}")
    public Result<PageResult<Object>> getUserMilestones(@PathVariable Long userId,
                                                       @RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "20") Integer size) {
        PageResult<Object> milestones = growthLogService.getUserMilestones(userId, page, size);
        return Result.success(milestones);
    }

    @Operation(summary = "生成成长报告")
    @PostMapping("/report/{userId}")
    public Result<GrowthReportVO> generateGrowthReport(@PathVariable Long userId,
                                                      @RequestParam String reportType,
                                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        GrowthReportVO report = growthLogService.generateGrowthReport(userId, reportType, startDate, endDate);
        return Result.success(report);
    }

    @Operation(summary = "导出成长报告PDF")
    @GetMapping("/report/{userId}/{reportId}/pdf")
    public ResponseEntity<byte[]> exportGrowthReportPdf(@PathVariable Long userId,
                                                       @PathVariable Long reportId) {
        byte[] pdfBytes = growthLogService.exportGrowthReportPdf(userId, reportId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "growth-report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}