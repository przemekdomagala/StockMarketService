package com.remitly.stock_market_service.service;

import com.remitly.stock_market_service.dto.LogResponse;
import com.remitly.stock_market_service.dto.WalletResponse;

public interface WalletService {
    WalletResponse getWalletById(String id);
    int getStockQuantityInWallet(String walletId, String stockName);
    void trade(String walletId, String stockName, String type);
    LogResponse getAllLogs();
}
