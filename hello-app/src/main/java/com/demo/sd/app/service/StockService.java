package com.demo.sd.app.service;

import com.demo.sd.app.model.Stock;
import com.demo.sd.app.repository.StockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StockService {
    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public Stock add(Stock stock) {
        return stockRepository.save(stock);
    }

    public List<Stock> getAll() {
        return stockRepository.findAll();
    }

    public Stock findByName(String comp) {
        return stockRepository.findByCompany(comp).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown Stock")
        );
    }

    public Stock updatePrice(int id, long price) {
        var stock = this.stockRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown stock")
        );
        stock.setPrice(price);
        return stockRepository.save(stock);
    }
}
