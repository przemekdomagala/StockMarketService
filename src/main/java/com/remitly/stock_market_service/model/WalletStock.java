package com.remitly.stock_market_service.model;

import jakarta.persistence.*;

@Entity
public class WalletStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false)
    private String stockName;

    private int quantity;

    public WalletStock() {}

    public WalletStock(Wallet wallet, String stockName, int quantity) {
        this.wallet = wallet;
        this.stockName = stockName;
        this.quantity = quantity;
    }

    public Wallet getWallet() { return wallet; }
    public String getStockName() { return stockName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
