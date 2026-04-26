package com.remitly.stock_market_service.service;

import com.remitly.stock_market_service.model.Stock;

import java.util.List;

public interface StockService {
    List<Stock> getAllStocks();
}
