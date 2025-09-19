package com.redflower.log.message;

import com.redflower.log.config.RocketMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 消息消费者
 */
@Slf4j
@Component
public class LogMessageConsumer {

    /**
     * 处理活动日志消息
     */
    @Component
    @RocketMQMessageListener(
            topic = RocketMQConfig.ACTIVITY_LOG_TOPIC,
            consumerGroup = "red-flower-log-activity-consumer"
    )
    public static class ActivityLogConsumer implements RocketMQListener<String> {
        @Override
        public void onMessage(String message) {
            log.info("接收到活动日志消息: {}", message);
            // 处理活动日志
        }
    }

    /**
     * 处理里程碑检查消息
     */
    @Component
    @RocketMQMessageListener(
            topic = RocketMQConfig.MILESTONE_CHECK_TOPIC,
            consumerGroup = "red-flower-log-milestone-consumer"
    )
    public static class MilestoneCheckConsumer implements RocketMQListener<String> {
        @Override
        public void onMessage(String message) {
            log.info("接收到里程碑检查消息: {}", message);
            // 处理里程碑检查
        }
    }

    /**
     * 处理报告生成消息
     */
    @Component
    @RocketMQMessageListener(
            topic = RocketMQConfig.REPORT_GENERATE_TOPIC,
            consumerGroup = "red-flower-log-report-consumer"
    )
    public static class ReportGenerateConsumer implements RocketMQListener<String> {
        @Override
        public void onMessage(String message) {
            log.info("接收到报告生成消息: {}", message);
            // 处理报告生成
        }
    }
}