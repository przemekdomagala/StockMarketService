package com.remitly.stock_market_service.service;

import com.remitly.stock_market_service.dto.SetStocksRequest;
import com.remitly.stock_market_service.dto.StockDto;
import com.remitly.stock_market_service.dto.StocksResponse;
import com.remitly.stock_market_service.model.Stock;
import com.remitly.stock_market_service.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Autowired
    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public StocksResponse getAllStocks() {
        List<StockDto> stocks = stockRepository.findAll().stream()
            .map(s -> new StockDto(s.getName(), s.getQuantity()))
            .toList();
        return new StocksResponse(stocks);
    }

    @Override
    @Transactional
    public void setStocks(SetStocksRequest request) {
        stockRepository.deleteAllInBatch();

        List<Stock> newStocks = request.stocks().stream()
                .map(dto -> new Stock(dto.name(), dto.quantity()))
                .toList();
        stockRepository.saveAll(newStocks);
    }
}
