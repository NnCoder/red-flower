package com.redflower.flower.controller;

import com.redflower.common.result.Result;
import com.redflower.flower.dto.FlowerTransferDTO;
import com.redflower.flower.service.FlowerService;
import com.redflower.flower.vo.FlowerAccountVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 红花控制器
 */
@Tag(name = "红花管理", description = "红花账户、转账、交易管理")
@RestController
@RequestMapping("/api/flower")
@RequiredArgsConstructor
public class FlowerController {

    private final FlowerService flowerService;

    @Operation(summary = "获取红花账户")
    @GetMapping("/account/{userId}")
    public Result<FlowerAccountVO> getFlowerAccount(@PathVariable Long userId) {
        FlowerAccountVO account = flowerService.getFlowerAccount(userId);
        return Result.success(account);
    }

    @Operation(summary = "红花转账")
    @PostMapping("/transfer")
    public Result<Void> transferFlowers(@Valid @RequestBody FlowerTransferDTO dto) {
        flowerService.transferFlowers(dto);
        return Result.success("转账成功");
    }

    @Operation(summary = "奖励红花")
    @PostMapping("/reward")
    public Result<Void> rewardFlowers(@RequestParam Long userId,
                                    @RequestParam Integer amount,
                                    @RequestParam Long taskId,
                                    @RequestParam String description) {
        flowerService.rewardFlowers(userId, amount, taskId, description);
        return Result.success("奖励成功");
    }

    @Operation(summary = "增加黑花")
    @PostMapping("/black")
    public Result<Void> addBlackFlowers(@RequestParam Long userId,
                                      @RequestParam Integer amount,
                                      @RequestParam String reason) {
        flowerService.addBlackFlowers(userId, amount, reason);
        return Result.success("添加成功");
    }
}