package com.redflower.flower.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.redflower.flower.entity.FlowerTransaction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 红花交易记录Mapper
 */
@Mapper
public interface FlowerTransactionMapper extends BaseMapper<FlowerTransaction> {
}