package com.remitly.stock_market_service.controller;

import com.remitly.stock_market_service.dto.LogEntryDto;
import com.remitly.stock_market_service.dto.LogResponse;
import com.remitly.stock_market_service.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LogController.class)
class LogControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean WalletService walletService;

    @Test
    void getLog_returnsLogWithSnakeCaseFields() throws Exception {
        when(walletService.getAllLogs()).thenReturn(new LogResponse(List.of(
            new LogEntryDto("buy", "wallet-1", "Apple Inc."),
            new LogEntryDto("sell", "wallet-2", "Tesla")
        )));

        mockMvc.perform(get("/log"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.log").isArray())
            .andExpect(jsonPath("$.log[0].type").value("buy"))
            .andExpect(jsonPath("$.log[0].wallet_id").value("wallet-1"))
            .andExpect(jsonPath("$.log[0].stock_name").value("Apple Inc."))
            .andExpect(jsonPath("$.log[1].type").value("sell"))
            .andExpect(jsonPath("$.log[1].wallet_id").value("wallet-2"))
            .andExpect(jsonPath("$.log[1].stock_name").value("Tesla"));
    }

    @Test
    void getLog_empty_returnsEmptyList() throws Exception {
        when(walletService.getAllLogs()).thenReturn(new LogResponse(List.of()));

        mockMvc.perform(get("/log"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.log").isEmpty());
    }
}
