package com.remitly.stock_market_service.init;

import com.remitly.stock_market_service.model.Stock;
import com.remitly.stock_market_service.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BankInitializer implements CommandLineRunner {

    @Autowired
    private StockRepository stockRepository;

    @Override
    public void run(String... args){
        stockRepository.save(new Stock("Apple Inc.", 1000));
    }
}
