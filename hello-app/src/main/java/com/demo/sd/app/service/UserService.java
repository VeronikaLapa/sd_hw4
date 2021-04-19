package com.demo.sd.app.service;

import com.demo.sd.app.model.Stock;
import com.demo.sd.app.model.User;
import com.demo.sd.app.repository.StockRepository;
import com.demo.sd.app.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final StockService stockService;
    private final StockRepository stockRepository;

    public UserService(UserRepository userRepository, StockService stockService, StockRepository stockRepository) {
        this.userRepository = userRepository;
        this.stockService = stockService;
        this.stockRepository = stockRepository;
    }

    public User add(User user) {
        return this.userRepository.save(user);
    }

    public List<User> getAll() {
        return this.userRepository.findAll();
    }

    public User auth(int id) {
        return this.userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found")
        );
    }

    private void addNewStocksToUser(String company, Long count, List<Stock> stocks, long currentPrice) {
        var stockExists = false;
        for (var currentStock : stocks) {
            if (currentStock.getCompany().equals(company)) {
                currentStock.setCount(currentStock.getCount() + count);
                stockExists = true;
                break;
            }
        }
        if (!stockExists) {
            var newStock = new Stock();
            newStock.setCount(count);
            newStock.setCompany(company);
            newStock.setPrice(currentPrice);
            stocks.add(newStock);
        }
    }

    public void buy(int userId, String comp, long amount) {
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found")
        );
        Stock stock = this.stockService.findByName(comp);
        if (amount > stock.getCount()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not enough stocks of company");
        }
        long price = stock.getPrice() * amount;
        if (price > user.getMoney()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not enough money");
        }
        stock.setCount(stock.getCount() - amount);
        addNewStocksToUser(comp, amount, user.getStocks(), stock.getPrice());
        user.setMoney(user.getMoney() - price);
        this.stockRepository.save(stock);
        this.userRepository.save(user);
    }

    public void sell(int userId, String comp, long amount) {
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found")
        );
        Stock stock = this.stockService.findByName(comp);
        long price = stock.getPrice() * amount;
        for (var currentStock : user.getStocks()) {
            if (currentStock.getCompany().equals(comp)) {
                long cnt = currentStock.getCount();
                if (cnt < amount) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not enough stocks of company");
                }
                currentStock.setCount(cnt - amount);
                user.setMoney(user.getMoney() + price);
                this.userRepository.save(user);
                stock.setCount(stock.getCount() + amount);
                return;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not enough stocks of company");
    }

}
