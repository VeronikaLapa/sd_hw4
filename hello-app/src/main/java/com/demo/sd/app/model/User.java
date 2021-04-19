package com.demo.sd.app.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
@Table(name = "user")
@Data
public class User {
    @Id
    @GeneratedValue
    private Integer id;

    @NotEmpty
    @Column(unique = true)
    private String login;

    @Min(0)
    private long money;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Stock> stocks;

}
