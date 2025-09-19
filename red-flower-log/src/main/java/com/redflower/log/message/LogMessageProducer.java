package com.redflower.log.message;

import com.redflower.log.config.RocketMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息生产者
 */
@Slf4j
@Component
public class LogMessageProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送活动日志消息
     */
    public void sendActivityLogMessage(Object message) {
        try {
            rocketMQTemplate.convertAndSend(RocketMQConfig.ACTIVITY_LOG_TOPIC, message);
            log.info("发送活动日志消息成功: {}", message);
        } catch (Exception e) {
            log.error("发送活动日志消息失败", e);
        }
    }

    /**
     * 发送里程碑检查消息
     */
    public void sendMilestoneCheckMessage(Object message) {
        try {
            rocketMQTemplate.convertAndSend(RocketMQConfig.MILESTONE_CHECK_TOPIC, message);
            log.info("发送里程碑检查消息成功: {}", message);
        } catch (Exception e) {
            log.error("发送里程碑检查消息失败", e);
        }
    }

    /**
     * 发送报告生成消息
     */
    public void sendReportGenerateMessage(Object message) {
        try {
            rocketMQTemplate.convertAndSend(RocketMQConfig.REPORT_GENERATE_TOPIC, message);
            log.info("发送报告生成消息成功: {}", message);
        } catch (Exception e) {
            log.error("发送报告生成消息失败", e);
        }
    }
}