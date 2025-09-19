package com.redflower.flower;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 红花服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class FlowerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowerApplication.class, args);
    }
}