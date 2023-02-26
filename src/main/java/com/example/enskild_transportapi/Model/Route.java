package com.example.enskild_transportapi.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
public class Route {

    @Id //primary key
    @GeneratedValue(strategy = GenerationType.AUTO) // AUTO_INCREMENT
    private long id;

    private String origin;
    private String destination;
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<Step> steps;

    private String estimatedTime;
    private String weather;
    private String trafficDelay;
    private Boolean isFavoured;
    private String transportMode;
}
