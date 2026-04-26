package com.remitly.stock_market_service.service;

import com.remitly.stock_market_service.model.Stock;
import com.remitly.stock_market_service.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockServiceImpl implements StockService{

    private final StockRepository stockRepository;

    @Autowired
    public StockServiceImpl(StockRepository stockRepository){
        this.stockRepository = stockRepository;
    }

    @Override
    public List<Stock> getAllStocks(){
        return stockRepository.findAll();
    }
}
