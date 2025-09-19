package com.redflower.flower.service;

import com.redflower.flower.dto.FlowerTransferDTO;
import com.redflower.flower.entity.FlowerAccount;
import com.redflower.flower.vo.FlowerAccountVO;

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
}