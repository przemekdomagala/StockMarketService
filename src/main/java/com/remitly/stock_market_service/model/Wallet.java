package com.remitly.stock_market_service.model;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Wallet implements Persistable<String> {

    @Id
    private String id;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<WalletStock> stocks = new ArrayList<>();

    @Transient
    private boolean isNew;

    public Wallet() {}

    public Wallet(String id) {
        this.id = id;
        this.isNew = true;
    }

    @Override
    public String getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }

    public List<WalletStock> getStocks() { return stocks; }
}
