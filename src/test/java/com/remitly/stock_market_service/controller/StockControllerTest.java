package com.remitly.stock_market_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.remitly.stock_market_service.dto.SetStocksRequest;
import com.remitly.stock_market_service.dto.StockDto;
import com.remitly.stock_market_service.dto.StocksResponse;
import com.remitly.stock_market_service.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean StockService stockService;

    @Test
    void getStocks_returnsWrappedList() throws Exception {
        when(stockService.getAllStocks()).thenReturn(
            new StocksResponse(List.of(new StockDto("Apple Inc.", 1000), new StockDto("Tesla", 50)))
        );

        mockMvc.perform(get("/stocks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stocks").isArray())
            .andExpect(jsonPath("$.stocks[0].name").value("Apple Inc."))
            .andExpect(jsonPath("$.stocks[0].quantity").value(1000))
            .andExpect(jsonPath("$.stocks[1].name").value("Tesla"))
            .andExpect(jsonPath("$.stocks[1].quantity").value(50));
    }

    @Test
    void getStocks_emptyBank_returnsEmptyList() throws Exception {
        when(stockService.getAllStocks()).thenReturn(new StocksResponse(List.of()));

        mockMvc.perform(get("/stocks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stocks").isEmpty());
    }

    @Test
    void postStocks_validBody_returns200() throws Exception {
        var request = new SetStocksRequest(List.of(new StockDto("Apple Inc.", 500)));

        mockMvc.perform(post("/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        verify(stockService).setStocks(any(SetStocksRequest.class));
    }
}
