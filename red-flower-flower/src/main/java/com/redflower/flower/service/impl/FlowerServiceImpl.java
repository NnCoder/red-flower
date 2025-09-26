package com.redflower.flower.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.redflower.common.exception.BusinessException;
import com.redflower.flower.dto.BlackFlowerEliminationDTO;
import com.redflower.flower.dto.FlowerTransferDTO;
import com.redflower.flower.dto.RewardExchangeDTO;
import com.redflower.flower.entity.*;
import com.redflower.flower.mapper.*;
import com.redflower.flower.service.FlowerService;
import com.redflower.flower.vo.FlowerAccountVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 红花服务实现
 */
@Service
@RequiredArgsConstructor
public class FlowerServiceImpl implements FlowerService {

    private final FlowerAccountMapper flowerAccountMapper;
    private final FlowerTransactionMapper flowerTransactionMapper;
    private final FlowerTransferMapper flowerTransferMapper;
    private final RewardItemMapper rewardItemMapper;
    private final RewardExchangeMapper rewardExchangeMapper;
    private final BlackFlowerEliminationMapper blackFlowerEliminationMapper;

    @Override
    public FlowerAccountVO getFlowerAccount(Long userId) {
        FlowerAccount account = getAccountBalance(userId);
        if (account == null) {
            createFlowerAccount(userId);
            account = getAccountBalance(userId);
        }

        FlowerAccountVO vo = new FlowerAccountVO();
        vo.setUserId(userId);
        vo.setRedFlowerBalance(account.getRedFlowerBalance());
        vo.setBlackFlowerBalance(account.getBlackFlowerBalance());
        vo.setTotalEarned(account.getTotalEarned());
        vo.setTotalSpent(account.getTotalSpent());
        return vo;
    }

    @Override
    public void createFlowerAccount(Long userId) {
        FlowerAccount account = new FlowerAccount();
        account.setUserId(userId);
        account.setRedFlowerBalance(0);
        account.setBlackFlowerBalance(0);
        account.setTotalEarned(0);
        account.setTotalSpent(0);
        account.setCreateTime(LocalDateTime.now());
        flowerAccountMapper.insert(account);
    }

    @Override
    @Transactional
    public void transferFlowers(FlowerTransferDTO dto) {
        // 检查转出用户余额
        FlowerAccount fromAccount = getAccountBalance(dto.getFromUserId());
        if (fromAccount == null || fromAccount.getRedFlowerBalance() < dto.getAmount()) {
            throw new BusinessException("红花余额不足");
        }

        // 检查转入用户账户
        FlowerAccount toAccount = getAccountBalance(dto.getToUserId());
        if (toAccount == null) {
            createFlowerAccount(dto.getToUserId());
            toAccount = getAccountBalance(dto.getToUserId());
        }

        // 扣减转出用户红花
        fromAccount.setRedFlowerBalance(fromAccount.getRedFlowerBalance() - dto.getAmount());
        fromAccount.setUpdateTime(LocalDateTime.now());
        flowerAccountMapper.updateById(fromAccount);

        // 增加转入用户红花
        toAccount.setRedFlowerBalance(toAccount.getRedFlowerBalance() + dto.getAmount());
        toAccount.setTotalEarned(toAccount.getTotalEarned() + dto.getAmount());
        toAccount.setUpdateTime(LocalDateTime.now());
        flowerAccountMapper.updateById(toAccount);

        // 记录转账流水
        FlowerTransfer transfer = new FlowerTransfer();
        transfer.setTransferNo(IdUtil.fastSimpleUUID());
        transfer.setFromUserId(dto.getFromUserId());
        transfer.setToUserId(dto.getToUserId());
        transfer.setAmount(dto.getAmount());
        transfer.setStatus("SUCCESS");
        transfer.setRemark(dto.getRemark());
        transfer.setCreateTime(LocalDateTime.now());
        flowerTransferMapper.insert(transfer);

        // 记录交易流水
        recordTransaction(dto.getFromUserId(), "TRANSFER_OUT", "RED", -dto.getAmount(),
                fromAccount.getRedFlowerBalance(), transfer.getId(), "TRANSFER", "转出红花");
        recordTransaction(dto.getToUserId(), "TRANSFER_IN", "RED", dto.getAmount(),
                toAccount.getRedFlowerBalance(), transfer.getId(), "TRANSFER", "转入红花");
    }

    @Override
    @Transactional
    public void rewardFlowers(Long userId, Integer amount, Long taskId, String description) {
        FlowerAccount account = getAccountBalance(userId);
        if (account == null) {
            createFlowerAccount(userId);
            account = getAccountBalance(userId);
        }

        // 增加红花余额
        account.setRedFlowerBalance(account.getRedFlowerBalance() + amount);
        account.setTotalEarned(account.getTotalEarned() + amount);
        account.setUpdateTime(LocalDateTime.now());
        flowerAccountMapper.updateById(account);

        // 记录交易流水
        recordTransaction(userId, "EARN", "RED", amount, account.getRedFlowerBalance(),
                taskId, "TASK", description);
    }

    @Override
    @Transactional
    public void consumeFlowers(Long userId, Integer redFlowers, Integer blackFlowers, Long rewardId) {
        FlowerAccount account = getAccountBalance(userId);
        if (account == null) {
            throw new BusinessException("账户不存在");
        }

        // 检查余额
        if (account.getRedFlowerBalance() < redFlowers || account.getBlackFlowerBalance() < blackFlowers) {
            throw new BusinessException("花朵余额不足");
        }

        // 扣减花朵
        account.setRedFlowerBalance(account.getRedFlowerBalance() - redFlowers);
        account.setBlackFlowerBalance(account.getBlackFlowerBalance() - blackFlowers);
        account.setTotalSpent(account.getTotalSpent() + redFlowers);
        account.setUpdateTime(LocalDateTime.now());
        flowerAccountMapper.updateById(account);

        // 记录交易流水
        if (redFlowers > 0) {
            recordTransaction(userId, "SPEND", "RED", -redFlowers, account.getRedFlowerBalance(),
                    rewardId, "REWARD", "消费红花");
        }
        if (blackFlowers > 0) {
            recordTransaction(userId, "SPEND", "BLACK", -blackFlowers, account.getBlackFlowerBalance(),
                    rewardId, "REWARD", "消费黑花");
        }
    }

    @Override
    @Transactional
    public void addBlackFlowers(Long userId, Integer amount, String reason) {
        FlowerAccount account = getAccountBalance(userId);
        if (account == null) {
            createFlowerAccount(userId);
            account = getAccountBalance(userId);
        }

        // 增加黑花
        account.setBlackFlowerBalance(account.getBlackFlowerBalance() + amount);
        account.setUpdateTime(LocalDateTime.now());
        flowerAccountMapper.updateById(account);

        // 记录交易流水
        recordTransaction(userId, "EARN", "BLACK", amount, account.getBlackFlowerBalance(),
                null, "PENALTY", reason);
    }

    @Override
    @Transactional
    public void exchangeReward(RewardExchangeDTO dto) {
        // 获取奖励信息
        RewardItem reward = getRewardItem(dto.getRewardId());
        if (reward == null || !reward.getIsActive()) {
            throw new BusinessException("奖励不存在或已下架");
        }

        // 检查用户余额
        FlowerAccount account = getAccountBalance(dto.getUserId());
        if (account == null) {
            throw new BusinessException("账户不存在");
        }

        if (account.getRedFlowerBalance() < reward.getRedFlowerCost() ||
            account.getBlackFlowerBalance() < reward.getBlackFlowerCost()) {
            throw new BusinessException("花朵余额不足");
        }

        // 扣减消费者花朵
        account.setRedFlowerBalance(account.getRedFlowerBalance() - reward.getRedFlowerCost());
        account.setBlackFlowerBalance(account.getBlackFlowerBalance() - reward.getBlackFlowerCost());
        account.setTotalSpent(account.getTotalSpent() + reward.getRedFlowerCost());
        account.setUpdateTime(LocalDateTime.now());
        flowerAccountMapper.updateById(account);

        // 计算回收红花数量
        Integer recycleAmount = 0;
        if (reward.getFlowerRecycle() && reward.getRedFlowerCost() > 0) {
            recycleAmount = reward.getRedFlowerCost() * reward.getRecycleRate().intValue();

            // 回收红花给奖励发布者
            FlowerAccount creatorAccount = getAccountBalance(reward.getCreatorId());
            if (creatorAccount != null) {
                creatorAccount.setRedFlowerBalance(creatorAccount.getRedFlowerBalance() + recycleAmount);
                creatorAccount.setUpdateTime(LocalDateTime.now());
                flowerAccountMapper.updateById(creatorAccount);

                // 记录奖励发布者收到回收红花的交易
                recordTransaction(reward.getCreatorId(), "RECYCLE", "RED", recycleAmount,
                        creatorAccount.getRedFlowerBalance(), dto.getRewardId(), "REWARD_RECYCLE", "奖励回收红花");
            }
        }

        // 记录兑换流水
        RewardExchange exchange = new RewardExchange();
        exchange.setExchangeNo(IdUtil.fastSimpleUUID());
        exchange.setUserId(dto.getUserId());
        exchange.setRewardId(dto.getRewardId());
        exchange.setRedFlowerCost(reward.getRedFlowerCost());
        exchange.setBlackFlowerCost(reward.getBlackFlowerCost());
        exchange.setRecycleAmount(recycleAmount);
        exchange.setRecycleToUserId(reward.getCreatorId());
        exchange.setStatus("COMPLETED");
        exchange.setRemark(dto.getRemark());
        exchange.setCreateTime(LocalDateTime.now());
        rewardExchangeMapper.insert(exchange);

        // 记录消费者交易流水
        if (reward.getRedFlowerCost() > 0) {
            recordTransaction(dto.getUserId(), "SPEND", "RED", -reward.getRedFlowerCost(),
                    account.getRedFlowerBalance(), exchange.getId(), "REWARD_EXCHANGE", "兑换奖励");
        }
        if (reward.getBlackFlowerCost() > 0) {
            recordTransaction(dto.getUserId(), "SPEND", "BLACK", -reward.getBlackFlowerCost(),
                    account.getBlackFlowerBalance(), exchange.getId(), "REWARD_EXCHANGE", "兑换奖励");
        }
    }

    @Override
    @Transactional
    public void eliminateBlackFlowers(BlackFlowerEliminationDTO dto, Long operatorUserId) {
        // 检查权限
        if (!hasBlackFlowerEliminationPermission(operatorUserId, dto.getFamilyId())) {
            throw new BusinessException("无权限消除黑花");
        }

        // 检查目标用户账户
        FlowerAccount account = getAccountBalance(dto.getTargetUserId());
        if (account == null) {
            throw new BusinessException("目标用户账户不存在");
        }

        if (account.getBlackFlowerBalance() < dto.getAmount()) {
            throw new BusinessException("黑花余额不足");
        }

        // 扣减黑花
        account.setBlackFlowerBalance(account.getBlackFlowerBalance() - dto.getAmount());
        account.setUpdateTime(LocalDateTime.now());
        flowerAccountMapper.updateById(account);

        // 记录消除流水
        BlackFlowerElimination elimination = new BlackFlowerElimination();
        elimination.setEliminationNo(IdUtil.fastSimpleUUID());
        elimination.setTargetUserId(dto.getTargetUserId());
        elimination.setOperatorUserId(operatorUserId);
        elimination.setAmount(dto.getAmount());
        elimination.setReason(dto.getReason());
        elimination.setFamilyId(dto.getFamilyId());
        elimination.setCreateTime(LocalDateTime.now());
        blackFlowerEliminationMapper.insert(elimination);

        // 记录交易流水
        recordTransaction(dto.getTargetUserId(), "ELIMINATE", "BLACK", -dto.getAmount(),
                account.getBlackFlowerBalance(), elimination.getId(), "BLACK_ELIMINATION", "消除黑花：" + dto.getReason());
    }

    @Override
    public boolean hasBlackFlowerEliminationPermission(Long userId, Long familyId) {
        // 简化版权限检查，实际应该查询family_permission表
        // 这里假设家长都有权限
        return true; // TODO: 实现真正的权限检查
    }

    @Override
    public List<RewardItem> getRewardItems(Long familyId, Long classId) {
        LambdaQueryWrapper<RewardItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RewardItem::getIsActive, true);
        if (familyId != null) {
            wrapper.eq(RewardItem::getFamilyId, familyId);
        }
        if (classId != null) {
            wrapper.eq(RewardItem::getClassId, classId);
        }
        wrapper.orderByDesc(RewardItem::getCreateTime);
        return rewardItemMapper.selectList(wrapper);
    }

    @Override
    public RewardItem getRewardItem(Long rewardId) {
        return rewardItemMapper.selectById(rewardId);
    }

    @Override
    public FlowerAccount getAccountBalance(Long userId) {
        LambdaQueryWrapper<FlowerAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FlowerAccount::getUserId, userId);
        return flowerAccountMapper.selectOne(wrapper);
    }

    private void recordTransaction(Long userId, String type, String flowerType, Integer amount,
                                 Integer balanceAfter, Long relatedId, String relatedType, String description) {
        FlowerTransaction transaction = new FlowerTransaction();
        transaction.setTransactionNo(IdUtil.fastSimpleUUID());
        transaction.setUserId(userId);
        transaction.setType(type);
        transaction.setFlowerType(flowerType);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setRelatedId(relatedId);
        transaction.setRelatedType(relatedType);
        transaction.setDescription(description);
        transaction.setCreateTime(LocalDateTime.now());
        flowerTransactionMapper.insert(transaction);
    }
}