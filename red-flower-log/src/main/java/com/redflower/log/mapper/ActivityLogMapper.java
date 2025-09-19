package com.redflower.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.redflower.log.entity.ActivityLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 活动日志Mapper
 */
@Mapper
public interface ActivityLogMapper extends BaseMapper<ActivityLog> {
}