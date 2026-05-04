package com.remitly.stock_market_service.integration;

import com.remitly.stock_market_service.dto.*;
import com.remitly.stock_market_service.repository.LogRepository;
import com.remitly.stock_market_service.repository.StockRepository;
import com.remitly.stock_market_service.repository.WalletRepository;
import com.remitly.stock_market_service.repository.WalletStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TradeIntegrationTest {

    @LocalServerPort int port;
    @Autowired TestRestTemplate rest;
    @Autowired StockRepository stockRepository;
    @Autowired WalletRepository walletRepository;
    @Autowired WalletStockRepository walletStockRepository;
    @Autowired LogRepository logRepository;

    private String base;

    @BeforeEach
    void setUp() {
        base = "http://localhost:" + port;
        logRepository.deleteAll();
        walletStockRepository.deleteAll();
        walletRepository.deleteAll();
        stockRepository.deleteAll();

        rest.postForEntity(base + "/stocks",
            new SetStocksRequest(List.of(new StockDto("Apple Inc.", 10))),
            Void.class);
    }

    // ── GET /stocks ──────────────────────────────────────────────────────────

    @Test
    void getStocks_returnsCurrentBankState() {
        ResponseEntity<StocksResponse> resp = rest.getForEntity(base + "/stocks", StocksResponse.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().stocks()).hasSize(1);
        assertThat(resp.getBody().stocks().get(0).name()).isEqualTo("Apple Inc.");
        assertThat(resp.getBody().stocks().get(0).quantity()).isEqualTo(10);
    }

    // ── POST /stocks ──────────────────────────────────────────────────────────

    @Test
    void postStocks_replacesBankState() {
        rest.postForEntity(base + "/stocks",
            new SetStocksRequest(List.of(new StockDto("Tesla", 5), new StockDto("Google", 20))),
            Void.class);

        StocksResponse resp = rest.getForObject(base + "/stocks", StocksResponse.class);
        assertThat(resp.stocks()).hasSize(2);
        assertThat(resp.stocks()).extracting(StockDto::name).containsExactlyInAnyOrder("Tesla", "Google");
    }

    // ── POST /wallets/{id}/stocks/{name} — BUY ───────────────────────────────

    @Test
    void buy_decrementsBankAndCreatesWallet() {
        ResponseEntity<Void> resp = rest.postForEntity(
            base + "/wallets/alice/stocks/Apple Inc.",
            new TradeRequest("buy"), Void.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        StocksResponse bank = rest.getForObject(base + "/stocks", StocksResponse.class);
        assertThat(bank.stocks().get(0).quantity()).isEqualTo(9);

        WalletResponse wallet = rest.getForObject(base + "/wallets/alice", WalletResponse.class);
        assertThat(wallet.id()).isEqualTo("alice");
        assertThat(wallet.stocks()).hasSize(1);
        assertThat(wallet.stocks().get(0).name()).isEqualTo("Apple Inc.");
        assertThat(wallet.stocks().get(0).quantity()).isEqualTo(1);
    }

    @Test
    void buy_multipleTimes_accumulatesInWallet() {
        rest.postForEntity(base + "/wallets/alice/stocks/Apple Inc.", new TradeRequest("buy"), Void.class);
        rest.postForEntity(base + "/wallets/alice/stocks/Apple Inc.", new TradeRequest("buy"), Void.class);
        rest.postForEntity(base + "/wallets/alice/stocks/Apple Inc.", new TradeRequest("buy"), Void.class);

        Integer qty = rest.getForObject(base + "/wallets/alice/stocks/Apple Inc.", Integer.class);
        assertThat(qty).isEqualTo(3);

        StocksResponse bank = rest.getForObject(base + "/stocks", StocksResponse.class);
        assertThat(bank.stocks().get(0).quantity()).isEqualTo(7);
    }

    @Test
    void buy_unknownStock_returns404() {
        ResponseEntity<Void> resp = rest.postForEntity(
            base + "/wallets/alice/stocks/NoSuchStock",
            new TradeRequest("buy"), Void.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void buy_bankHasNoStock_returns400() {
        ResponseEntity<Void> setupResp = rest.postForEntity(base + "/stocks",
                new SetStocksRequest(List.of(new StockDto("Apple Inc.", 0))), Void.class);

        assertThat(setupResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Void> resp = rest.postForEntity(
            base + "/wallets/alice/stocks/Apple Inc.",
            new TradeRequest("buy"), Void.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void buy_walletCreatedIfNotExisting() {
        rest.postForEntity(base + "/wallets/newwallet/stocks/Apple Inc.", new TradeRequest("buy"), Void.class);

        WalletResponse wallet = rest.getForObject(base + "/wallets/newwallet", WalletResponse.class);
        assertThat(wallet).isNotNull();
        assertThat(wallet.id()).isEqualTo("newwallet");
    }

    // ── POST /wallets/{id}/stocks/{name} — SELL ──────────────────────────────

    @Test
    void sell_incrementsBankAndDecrementsWallet() {
        rest.postForEntity(base + "/wallets/alice/stocks/Apple Inc.", new TradeRequest("buy"), Void.class);
        rest.postForEntity(base + "/wallets/alice/stocks/Apple Inc.", new TradeRequest("buy"), Void.class);

        ResponseEntity<Void> resp = rest.postForEntity(
            base + "/wallets/alice/stocks/Apple Inc.",
            new TradeRequest("sell"), Void.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        Integer walletQty = rest.getForObject(base + "/wallets/alice/stocks/Apple Inc.", Integer.class);
        assertThat(walletQty).isEqualTo(1);

        StocksResponse bank = rest.getForObject(base + "/stocks", StocksResponse.class);
        assertThat(bank.stocks().get(0).quantity()).isEqualTo(9);
    }

    @Test
    void sell_stockNotInWallet_returns400() {
        ResponseEntity<Void> resp = rest.postForEntity(
            base + "/wallets/alice/stocks/Apple Inc.",
            new TradeRequest("sell"), Void.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void sell_unknownStock_returns404() {
        ResponseEntity<Void> resp = rest.postForEntity(
            base + "/wallets/alice/stocks/NoSuchStock",
            new TradeRequest("sell"), Void.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ── GET /wallets/{id} ────────────────────────────────────────────────────

    @Test
    void getWallet_notFound_returns404() {
        ResponseEntity<WalletResponse> resp = rest.getForEntity(
            base + "/wallets/nobody", WalletResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getWallet_returnsAllHeldStocks() {
        rest.postForEntity(base + "/stocks",
            new SetStocksRequest(List.of(new StockDto("Apple Inc.", 10), new StockDto("Tesla", 10))),
            Void.class);

        rest.postForEntity(base + "/wallets/bob/stocks/Apple Inc.", new TradeRequest("buy"), Void.class);
        rest.postForEntity(base + "/wallets/bob/stocks/Tesla", new TradeRequest("buy"), Void.class);
        rest.postForEntity(base + "/wallets/bob/stocks/Tesla", new TradeRequest("buy"), Void.class);

        WalletResponse wallet = rest.getForObject(base + "/wallets/bob", WalletResponse.class);
        assertThat(wallet.stocks()).hasSize(2);
        assertThat(wallet.stocks()).extracting(StockDto::name).containsExactlyInAnyOrder("Apple Inc.", "Tesla");
    }

    // ── GET /wallets/{id}/stocks/{name} ──────────────────────────────────────

    @Test
    void getStockQuantity_afterBuysAndSells_correct() {
        rest.postForEntity(base + "/wallets/alice/stocks/Apple Inc.", new TradeRequest("buy"), Void.class);
        rest.postForEntity(base + "/wallets/alice/stocks/Apple Inc.", new TradeRequest("buy"), Void.class);
        rest.postForEntity(base + "/wallets/alice/stocks/Apple Inc.", new TradeRequest("sell"), Void.class);

        Integer qty = rest.getForObject(base + "/wallets/alice/stocks/Apple Inc.", Integer.class);
        assertThat(qty).isEqualTo(1);
    }

    @Test
    void getStockQuantity_unknownWallet_returnsZero() {
        Integer qty = rest.getForObject(base + "/wallets/ghost/stocks/Apple Inc.", Integer.class);
        assertThat(qty).isEqualTo(0);
    }

    // ── GET /log ──────────────────────────────────────────────────────────────

    @Test
    void log_recordsSuccessfulTradesInOrder() {
        rest.postForEntity(base + "/wallets/alice/stocks/Apple Inc.", new TradeRequest("buy"), Void.class);
        rest.postForEntity(base + "/wallets/alice/stocks/Apple Inc.", new TradeRequest("buy"), Void.class);
        rest.postForEntity(base + "/wallets/alice/stocks/Apple Inc.", new TradeRequest("sell"), Void.class);

        LogResponse log = rest.getForObject(base + "/log", LogResponse.class);
        assertThat(log.log()).hasSize(3);
        assertThat(log.log().get(0).type()).isEqualTo("buy");
        assertThat(log.log().get(1).type()).isEqualTo("buy");
        assertThat(log.log().get(2).type()).isEqualTo("sell");
        assertThat(log.log().get(0).walletId()).isEqualTo("alice");
        assertThat(log.log().get(0).stockName()).isEqualTo("Apple Inc.");
    }

    @Test
    void log_failedOperationsNotLogged() {
        // attempt to buy unknown stock — should NOT appear in log
        rest.postForEntity(base + "/wallets/alice/stocks/NoSuchStock", new TradeRequest("buy"), Void.class);

        LogResponse log = rest.getForObject(base + "/log", LogResponse.class);
        assertThat(log.log()).isEmpty();
    }

    @Test
    void log_bankOperationsNotLogged() {
        // POST /stocks is a bank operation, should NOT appear in log
        rest.postForEntity(base + "/stocks",
            new SetStocksRequest(List.of(new StockDto("Apple Inc.", 100))), Void.class);

        LogResponse log = rest.getForObject(base + "/log", LogResponse.class);
        assertThat(log.log()).isEmpty();
    }
}
