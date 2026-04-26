package com.remitly.stock_market_service.controller;

import com.remitly.stock_market_service.model.Wallet;
import com.remitly.stock_market_service.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService){
        this.walletService = walletService;
    }

    @GetMapping("/{id}")
    public Wallet getWalletById(@PathVariable Long id, WalletService walletService){
        return this.walletService.getWalletById(id);
    }
}
