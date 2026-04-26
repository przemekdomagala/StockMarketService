package com.remitly.stock_market_service.service;

import com.remitly.stock_market_service.model.Wallet;
import com.remitly.stock_market_service.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository){
        this.walletRepository = walletRepository;
    }

    @Override
    public Wallet getWalletById(long id) {
        Optional<Wallet> optionalWallet = walletRepository.findById(id);
        return optionalWallet.orElse(null);
    }
}
