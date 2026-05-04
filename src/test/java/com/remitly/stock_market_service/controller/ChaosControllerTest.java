package com.remitly.stock_market_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChaosController.class)
class ChaosControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean InstanceKiller instanceKiller;

    @Test
    void postChaos_invokesKiller() throws Exception {
        mockMvc.perform(post("/chaos"))
            .andExpect(status().isOk());

        verify(instanceKiller).kill();
    }
}
