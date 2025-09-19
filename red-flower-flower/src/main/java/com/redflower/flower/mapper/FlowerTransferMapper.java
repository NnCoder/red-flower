package com.redflower.flower.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.redflower.flower.entity.FlowerTransfer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 红花转账记录Mapper
 */
@Mapper
public interface FlowerTransferMapper extends BaseMapper<FlowerTransfer> {
}