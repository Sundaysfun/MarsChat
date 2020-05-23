package com.ms.utils;

import com.ms.service.WebSocketServer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 灭霸创造者，每1分钟生成一个灭霸
 */
@Component
public class ThanosBuilder {
    @Scheduled(cron = "0/180 * * * * ?")
    public void build() {
        WebSocketServer.chooseThanos();
    }
}
