package com.remitly.stock_market_service;

import com.remitly.stock_market_service.controller.InstanceKiller;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StockMarketServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockMarketServiceApplication.class, args);
    }

    @Bean
    public InstanceKiller instanceKiller() {
        return () -> Runtime.getRuntime().halt(1);
    }
}
