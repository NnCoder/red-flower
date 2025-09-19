package com.redflower.log.config;

import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ配置类
 */
@Configuration
public class RocketMQConfig {

    public static final String ACTIVITY_LOG_TOPIC = "red-flower-activity-log";
    public static final String MILESTONE_CHECK_TOPIC = "red-flower-milestone-check";
    public static final String REPORT_GENERATE_TOPIC = "red-flower-report-generate";

}