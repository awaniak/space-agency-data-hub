package com.example.spaceagencydatahub.domain.product.model;



import com.example.spaceagencydatahub.domain.mission.model.Mission;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Mission mission;

    private Instant acquisitionDate;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Coordinate> footprint;

    private double price;

    private String url;
}
