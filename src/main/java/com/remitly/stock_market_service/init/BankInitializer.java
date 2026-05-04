package com.remitly.stock_market_service.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BankInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) {
        // Bank starts empty per spec — use POST /stocks to populate
    }
}
