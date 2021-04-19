package com.demo.sd.app.controller;

import com.demo.sd.app.model.Stock;
import com.demo.sd.app.model.User;
import com.demo.sd.app.service.StockService;
import com.demo.sd.app.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User add(@RequestBody User user) {
        return userService.add(user);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User auth(@PathVariable int id) {
        return userService.auth(id);
    }

    @GetMapping("/{id}/buy")
    public void buy(@PathVariable int id, @RequestParam String company, @RequestParam Long count){
        userService.buy(id, company, count);
    }
    @GetMapping("/{id}/sell")
    public void sell(@PathVariable int id, @RequestParam String company, @RequestParam Long count){
        userService.sell(id, company, count);
    }
}
