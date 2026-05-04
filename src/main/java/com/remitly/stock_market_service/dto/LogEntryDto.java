package com.remitly.stock_market_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LogEntryDto(
    String type,
    @JsonProperty("wallet_id") String walletId,
    @JsonProperty("stock_name") String stockName
) {}
