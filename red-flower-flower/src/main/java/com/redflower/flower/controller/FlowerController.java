package com.redflower.flower.controller;

import com.redflower.common.result.Result;
import com.redflower.flower.dto.BlackFlowerEliminationDTO;
import com.redflower.flower.dto.FlowerTransferDTO;
import com.redflower.flower.dto.RewardExchangeDTO;
import com.redflower.flower.entity.RewardItem;
import com.redflower.flower.service.FlowerService;
import com.redflower.flower.vo.FlowerAccountVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return Result.success();
    }

    @Operation(summary = "奖励红花")
    @PostMapping("/reward")
    public Result<Void> rewardFlowers(@RequestParam Long userId,
                                    @RequestParam Integer amount,
                                    @RequestParam Long taskId,
                                    @RequestParam String description) {
        flowerService.rewardFlowers(userId, amount, taskId, description);
        return Result.success();
    }

    @Operation(summary = "增加黑花")
    @PostMapping("/black")
    public Result<Void> addBlackFlowers(@RequestParam Long userId,
                                      @RequestParam Integer amount,
                                      @RequestParam String reason) {
        flowerService.addBlackFlowers(userId, amount, reason);
        return Result.success();
    }

    @PostMapping("/consume")
    @Operation(summary = "消费红花")
    public Result<Void> consumeFlowers(@RequestParam Long userId,
                                      @RequestParam(defaultValue = "0") Integer redFlowers,
                                      @RequestParam(defaultValue = "0") Integer blackFlowers,
                                      @RequestParam(required = false) Long rewardId) {
        flowerService.consumeFlowers(userId, redFlowers, blackFlowers, rewardId);
        return Result.success();
    }

    @PostMapping("/reward/exchange")
    @Operation(summary = "兑换奖励(支持红花回收)")
    public Result<Void> exchangeReward(@RequestBody RewardExchangeDTO dto) {
        flowerService.exchangeReward(dto);
        return Result.success();
    }

    @PostMapping("/black-flower/eliminate")
    @Operation(summary = "消除黑花")
    public Result<Void> eliminateBlackFlowers(@RequestBody BlackFlowerEliminationDTO dto,
                                             @RequestParam Long operatorUserId) {
        flowerService.eliminateBlackFlowers(dto, operatorUserId);
        return Result.success();
    }

    @GetMapping("/permission/black-flower-elimination")
    @Operation(summary = "检查黑花消除权限")
    public Result<Boolean> hasBlackFlowerEliminationPermission(@RequestParam Long userId,
                                                               @RequestParam Long familyId) {
        boolean hasPermission = flowerService.hasBlackFlowerEliminationPermission(userId, familyId);
        return Result.success(hasPermission);
    }

    @GetMapping("/rewards")
    @Operation(summary = "获取奖励商品列表")
    public Result<List<RewardItem>> getRewardItems(@RequestParam(required = false) Long familyId,
                                                   @RequestParam(required = false) Long classId) {
        List<RewardItem> rewards = flowerService.getRewardItems(familyId, classId);
        return Result.success(rewards);
    }

    @GetMapping("/reward/{rewardId}")
    @Operation(summary = "获取奖励商品详情")
    public Result<RewardItem> getRewardItem(@PathVariable Long rewardId) {
        RewardItem reward = flowerService.getRewardItem(rewardId);
        return Result.success(reward);
    }
}