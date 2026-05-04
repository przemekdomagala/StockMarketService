package com.remitly.stock_market_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chaos")
public class ChaosController {

    private final InstanceKiller instanceKiller;

    @Autowired
    public ChaosController(InstanceKiller instanceKiller) {
        this.instanceKiller = instanceKiller;
    }

    @PostMapping
    public void chaos() {
        instanceKiller.kill();
    }
}
