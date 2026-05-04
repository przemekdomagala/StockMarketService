package com.remitly.stock_market_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.remitly.stock_market_service.dto.StockDto;
import com.remitly.stock_market_service.dto.TradeRequest;
import com.remitly.stock_market_service.dto.WalletResponse;
import com.remitly.stock_market_service.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean WalletService walletService;

    @Test
    void getWallet_existing_returnsWalletWithStocks() throws Exception {
        when(walletService.getWalletById("wallet-1")).thenReturn(
            new WalletResponse("wallet-1", List.of(new StockDto("Apple Inc.", 3)))
        );

        mockMvc.perform(get("/wallets/wallet-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("wallet-1"))
            .andExpect(jsonPath("$.stocks[0].name").value("Apple Inc."))
            .andExpect(jsonPath("$.stocks[0].quantity").value(3));
    }

    @Test
    void getWallet_notFound_returns404() throws Exception {
        when(walletService.getWalletById("missing"))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/wallets/missing"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getStockQuantity_returnsNumber() throws Exception {
        when(walletService.getStockQuantityInWallet("wallet-1", "Apple Inc.")).thenReturn(7);

        mockMvc.perform(get("/wallets/wallet-1/stocks/Apple Inc."))
            .andExpect(status().isOk())
            .andExpect(content().string("7"));
    }

    @Test
    void getStockQuantity_unknownWalletOrStock_returnsZero() throws Exception {
        when(walletService.getStockQuantityInWallet("ghost", "Unknown")).thenReturn(0);

        mockMvc.perform(get("/wallets/ghost/stocks/Unknown"))
            .andExpect(status().isOk())
            .andExpect(content().string("0"));
    }

    @Test
    void trade_buy_returns200() throws Exception {
        mockMvc.perform(post("/wallets/wallet-1/stocks/Apple Inc.")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TradeRequest("buy"))))
            .andExpect(status().isOk());

        verify(walletService).trade("wallet-1", "Apple Inc.", "buy");
    }

    @Test
    void trade_sell_returns200() throws Exception {
        mockMvc.perform(post("/wallets/wallet-1/stocks/Apple Inc.")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TradeRequest("sell"))))
            .andExpect(status().isOk());

        verify(walletService).trade("wallet-1", "Apple Inc.", "sell");
    }

    @Test
    void trade_stockNotFound_returns404() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found"))
            .when(walletService).trade(any(), eq("Unknown"), any());

        mockMvc.perform(post("/wallets/wallet-1/stocks/Unknown")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TradeRequest("buy"))))
            .andExpect(status().isNotFound());
    }

    @Test
    void trade_bankOutOfStock_returns400() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock not available in bank"))
            .when(walletService).trade(any(), any(), eq("buy"));

        mockMvc.perform(post("/wallets/wallet-1/stocks/Apple Inc.")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TradeRequest("buy"))))
            .andExpect(status().isBadRequest());
    }

    @Test
    void trade_walletOutOfStock_returns400() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock not in wallet"))
            .when(walletService).trade(any(), any(), eq("sell"));

        mockMvc.perform(post("/wallets/wallet-1/stocks/Apple Inc.")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TradeRequest("sell"))))
            .andExpect(status().isBadRequest());
    }
}
