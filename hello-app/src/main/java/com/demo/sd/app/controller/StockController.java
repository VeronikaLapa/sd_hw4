package com.demo.sd.app.controller;

import com.demo.sd.app.model.Stock;
import com.demo.sd.app.service.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public Stock add(@RequestBody Stock stock) {
        return stockService.add(stock);
    }

    @GetMapping
    public List<Stock> allStocks() {
        return stockService.getAll();
    }

    @GetMapping("/price/{id}")
    public void updatePrice(@PathVariable int id, @RequestParam long price) {
        stockService.updatePrice(id, price);
    }
}
