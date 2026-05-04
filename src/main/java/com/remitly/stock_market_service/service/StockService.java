package com.remitly.stock_market_service.service;

import com.remitly.stock_market_service.dto.SetStocksRequest;
import com.remitly.stock_market_service.dto.StocksResponse;

public interface StockService {
    StocksResponse getAllStocks();
    void setStocks(SetStocksRequest request);
}
