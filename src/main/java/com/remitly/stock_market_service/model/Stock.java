package com.remitly.stock_market_service.model;

import jakarta.persistence.*;

@Entity
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable = false)
    private String name;
    private int quantity;

    public Stock(){

    }

    public Stock(String name, int quantity){
        this.name = name;
        this.quantity = quantity;
    }

    //getters
    public String getName(){ return name; }
    public int getQuantity(){ return quantity; }
}
