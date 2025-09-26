package com.redflower.flower.service;

import com.redflower.flower.dto.BlackFlowerEliminationDTO;
import com.redflower.flower.dto.FlowerTransferDTO;
import com.redflower.flower.dto.RewardExchangeDTO;
import com.redflower.flower.entity.FlowerAccount;
import com.redflower.flower.entity.RewardItem;
import com.redflower.flower.vo.FlowerAccountVO;

import java.util.List;

/**
 * 红花服务接口
 */
public interface FlowerService {

    /**
     * 获取用户红花账户
     */
    FlowerAccountVO getFlowerAccount(Long userId);

    /**
     * 创建红花账户
     */
    void createFlowerAccount(Long userId);

    /**
     * 红花转账
     */
    void transferFlowers(FlowerTransferDTO dto);

    /**
     * 奖励红花
     */
    void rewardFlowers(Long userId, Integer amount, Long taskId, String description);

    /**
     * 消费红花
     */
    void consumeFlowers(Long userId, Integer redFlowers, Integer blackFlowers, Long rewardId);

    /**
     * 增加黑花
     */
    void addBlackFlowers(Long userId, Integer amount, String reason);

    /**
     * 获取账户余额
     */
    FlowerAccount getAccountBalance(Long userId);

    /**
     * 兑换奖励(支持红花回收)
     */
    void exchangeReward(RewardExchangeDTO dto);

    /**
     * 消除黑花
     */
    void eliminateBlackFlowers(BlackFlowerEliminationDTO dto, Long operatorUserId);

    /**
     * 检查用户是否有黑花消除权限
     */
    boolean hasBlackFlowerEliminationPermission(Long userId, Long familyId);

    /**
     * 获取奖励商品列表
     */
    List<RewardItem> getRewardItems(Long familyId, Long classId);

    /**
     * 获取奖励商品详情
     */
    RewardItem getRewardItem(Long rewardId);
}