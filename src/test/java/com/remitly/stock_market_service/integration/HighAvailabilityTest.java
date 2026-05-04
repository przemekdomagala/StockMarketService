package com.remitly.stock_market_service.integration;

import com.remitly.stock_market_service.dto.SetStocksRequest;
import com.remitly.stock_market_service.dto.StockDto;
import com.remitly.stock_market_service.dto.StocksResponse;
import com.remitly.stock_market_service.dto.TradeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HA tests run against a live deployed service (nginx + 2 app replicas).
 *
 * Enable by passing: -Dha.test.url=http://localhost:8080
 *   ./mvnw test -Dha.test.url=http://localhost:8080
 */
@EnabledIfSystemProperty(named = "ha.test.url", matches = ".+")
class HighAvailabilityTest {

    private RestTemplate rest;
    private String base;

    @BeforeEach
    void setUp() {
        rest = new RestTemplate();
        base = System.getProperty("ha.test.url");

        rest.postForEntity(base + "/stocks",
            new SetStocksRequest(List.of(new StockDto("Apple Inc.", 100))),
            Void.class);
    }

    @Test
    void serviceIsHealthyBeforeChaos() {
        ResponseEntity<StocksResponse> resp = rest.getForEntity(base + "/stocks", StocksResponse.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().stocks()).isNotEmpty();
    }

    @Test
    void serviceRemainsAvailableAfterKillingOneInstance() throws InterruptedException {
        // verify service is up with 2 instances
        assertThat(rest.getForEntity(base + "/stocks", StocksResponse.class).getStatusCode())
            .isEqualTo(HttpStatus.OK);

        // kill whichever instance nginx routes this request to
        rest.postForEntity(base + "/chaos", null, Void.class);

        // give nginx time to detect the dead backend (max_fails + fail_timeout kicks in)
        Thread.sleep(1000);

        // service must still respond — the second instance is alive
        int successCount = 0;
        for (int i = 0; i < 10; i++) {
            try {
                ResponseEntity<StocksResponse> resp = rest.getForEntity(base + "/stocks", StocksResponse.class);
                if (resp.getStatusCode().is2xxSuccessful()) successCount++;
            } catch (Exception ignored) {}
        }

        // allow at most 1 failed request (nginx may route one request to dead instance
        // before marking it unavailable); the remaining 9 must succeed
        assertThat(successCount).isGreaterThanOrEqualTo(9);
    }

    @Test
    void dataIntegrityMaintainedAfterChaos() throws InterruptedException {
        // establish known state: alice buys 3 stocks
        rest.postForEntity(base + "/wallets/alice-ha/stocks/Apple Inc.", new TradeRequest("buy"), Void.class);
        rest.postForEntity(base + "/wallets/alice-ha/stocks/Apple Inc.", new TradeRequest("buy"), Void.class);
        rest.postForEntity(base + "/wallets/alice-ha/stocks/Apple Inc.", new TradeRequest("buy"), Void.class);

        rest.postForEntity(base + "/chaos", null, Void.class);
        Thread.sleep(1000);

        // data written before chaos must still be readable
        Integer qty = rest.getForObject(base + "/wallets/alice-ha/stocks/Apple Inc.", Integer.class);
        assertThat(qty).isEqualTo(3);
    }

    @Test
    void newOperationsWorkAfterChaos() throws InterruptedException {
        rest.postForEntity(base + "/chaos", null, Void.class);
        Thread.sleep(1000);

        // buy after chaos — must succeed
        ResponseEntity<Void> buyResp = rest.postForEntity(
            base + "/wallets/bob-ha/stocks/Apple Inc.",
            new TradeRequest("buy"), Void.class);
        assertThat(buyResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // read wallet state
        Integer qty = rest.getForObject(base + "/wallets/bob-ha/stocks/Apple Inc.", Integer.class);
        assertThat(qty).isEqualTo(1);
    }
}
