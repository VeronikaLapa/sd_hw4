package com.demo.sd.app.model;

import java.util.List;

public class User {

    private Integer id;


    private String login;

    private long money;

    private List<Stock> stocks;

    public Integer getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public long getMoney() {
        return money;
    }

    public List<Stock> getStocks() {
        return stocks;
    }
}
