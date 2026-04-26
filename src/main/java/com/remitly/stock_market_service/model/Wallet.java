package com.remitly.stock_market_service.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
public class Wallet{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    private List<Stock> stocks;

    public Wallet(){

    }

    public Wallet(List<Stock> stocks){
        this.stocks = stocks;
    }

    List<Stock> getStocks(){
        return this.stocks;
    }
}
