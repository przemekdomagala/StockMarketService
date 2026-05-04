package com.remitly.stock_market_service.controller;

import com.remitly.stock_market_service.dto.SetStocksRequest;
import com.remitly.stock_market_service.dto.StocksResponse;
import com.remitly.stock_market_service.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public StocksResponse getAllStocks() {
        return stockService.getAllStocks();
    }

    @PostMapping
    public void setStocks(@RequestBody SetStocksRequest request) {
        stockService.setStocks(request);
    }
}
