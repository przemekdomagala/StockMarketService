package com.remitly.stock_market_service.repository;

import com.remitly.stock_market_service.model.Wallet;
import com.remitly.stock_market_service.model.WalletStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletStockRepository extends JpaRepository<WalletStock, Long> {
    Optional<WalletStock> findByWalletAndStockName(Wallet wallet, String stockName);
}
