package com.remitly.stock_market_service.model;

import jakarta.persistence.*;

@Entity
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String walletId;
    private String stockName;

    public LogEntry() {}

    public LogEntry(String type, String walletId, String stockName) {
        this.type = type;
        this.walletId = walletId;
        this.stockName = stockName;
    }

    public String getType() { return type; }
    public String getWalletId() { return walletId; }
    public String getStockName() { return stockName; }
}
