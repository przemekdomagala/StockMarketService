package com.remitly.stock_market_service.controller;

import com.remitly.stock_market_service.dto.LogResponse;
import com.remitly.stock_market_service.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
public class LogController {

    private final WalletService walletService;

    @Autowired
    public LogController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public LogResponse getLog() {
        return walletService.getAllLogs();
    }
}
