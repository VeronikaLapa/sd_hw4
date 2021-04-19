package com.demo.sd.app.model;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import lombok.Data;


@Entity
@Table(name = "stock")
@Data
public class Stock {
    @Id
    @GeneratedValue
    private Integer id;

    @NotEmpty
    private String company;

    @Min(0)
    private long price;

    private long count;
}
