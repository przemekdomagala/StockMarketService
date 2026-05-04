package com.remitly.stock_market_service.service;

import com.remitly.stock_market_service.dto.LogEntryDto;
import com.remitly.stock_market_service.dto.LogResponse;
import com.remitly.stock_market_service.dto.StockDto;
import com.remitly.stock_market_service.dto.WalletResponse;
import com.remitly.stock_market_service.model.LogEntry;
import com.remitly.stock_market_service.model.Stock;
import com.remitly.stock_market_service.model.Wallet;
import com.remitly.stock_market_service.model.WalletStock;
import com.remitly.stock_market_service.repository.LogRepository;
import com.remitly.stock_market_service.repository.StockRepository;
import com.remitly.stock_market_service.repository.WalletRepository;
import com.remitly.stock_market_service.repository.WalletStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletStockRepository walletStockRepository;
    private final StockRepository stockRepository;
    private final LogRepository logRepository;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository,
                             WalletStockRepository walletStockRepository,
                             StockRepository stockRepository,
                             LogRepository logRepository) {
        this.walletRepository = walletRepository;
        this.walletStockRepository = walletStockRepository;
        this.stockRepository = stockRepository;
        this.logRepository = logRepository;
    }

    @Override
    public WalletResponse getWalletById(String id) {
        Wallet wallet = walletRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));
        return toWalletResponse(wallet);
    }

    @Override
    public int getStockQuantityInWallet(String walletId, String stockName) {
        return walletRepository.findById(walletId)
            .flatMap(wallet -> walletStockRepository.findByWalletAndStockName(wallet, stockName))
            .map(WalletStock::getQuantity)
            .orElse(0);
    }

    @Override
    @Transactional
    public void trade(String walletId, String stockName, String type) {
        Stock stock = stockRepository.findByNameForUpdate(stockName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found"));

        Wallet wallet = walletRepository.findById(walletId)
            .orElseGet(() -> walletRepository.save(new Wallet(walletId)));

        if ("buy".equals(type)) {
            if (stock.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock not available in bank");
            }
            stock.setQuantity(stock.getQuantity() - 1);
            stockRepository.save(stock);

            WalletStock holding = walletStockRepository.findByWalletAndStockName(wallet, stockName)
                .orElse(new WalletStock(wallet, stockName, 0));
            holding.setQuantity(holding.getQuantity() + 1);
            walletStockRepository.save(holding);

        } else if ("sell".equals(type)) {
            WalletStock holding = walletStockRepository.findByWalletAndStockName(wallet, stockName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock not in wallet"));
            if (holding.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock not in wallet");
            }
            holding.setQuantity(holding.getQuantity() - 1);
            walletStockRepository.save(holding);

            stock.setQuantity(stock.getQuantity() + 1);
            stockRepository.save(stock);

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type, must be 'buy' or 'sell'");
        }

        logRepository.save(new LogEntry(type, walletId, stockName));
    }

    @Override
    public LogResponse getAllLogs() {
        List<LogEntryDto> entries = logRepository.findAllByOrderByIdAsc().stream()
            .map(e -> new LogEntryDto(e.getType(), e.getWalletId(), e.getStockName()))
            .toList();
        return new LogResponse(entries);
    }

    private WalletResponse toWalletResponse(Wallet wallet) {
        List<StockDto> stocks = wallet.getStocks().stream()
            .map(ws -> new StockDto(ws.getStockName(), ws.getQuantity()))
            .toList();
        return new WalletResponse(wallet.getId(), stocks);
    }
}
