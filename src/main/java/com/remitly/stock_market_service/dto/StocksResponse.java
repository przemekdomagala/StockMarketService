package com.remitly.stock_market_service.dto;

import java.util.List;

public record StocksResponse(List<StockDto> stocks) {}
