package com.remitly.stock_market_service.controller;

import com.remitly.stock_market_service.model.Stock;
import com.remitly.stock_market_service.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService){
        this.stockService = stockService;
    }

    @GetMapping
    public List<Stock> getAllStocks(){
        return stockService.getAllStocks();
    }
}
