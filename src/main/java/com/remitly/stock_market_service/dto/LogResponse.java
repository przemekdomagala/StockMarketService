package com.remitly.stock_market_service.dto;

import java.util.List;

public record LogResponse(List<LogEntryDto> log) {}
