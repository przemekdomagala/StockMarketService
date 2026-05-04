package com.remitly.stock_market_service.controller;

import com.remitly.stock_market_service.dto.TradeRequest;
import com.remitly.stock_market_service.dto.WalletResponse;
import com.remitly.stock_market_service.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/{walletId}")
    public WalletResponse getWalletById(@PathVariable String walletId) {
        return walletService.getWalletById(walletId);
    }

    @GetMapping("/{walletId}/stocks/{stockName}")
    public int getStockQuantity(@PathVariable String walletId, @PathVariable String stockName) {
        return walletService.getStockQuantityInWallet(walletId, stockName);
    }

    @PostMapping("/{walletId}/stocks/{stockName}")
    public void trade(@PathVariable String walletId,
                      @PathVariable String stockName,
                      @RequestBody TradeRequest request) {
        walletService.trade(walletId, stockName, request.type());
    }
}
