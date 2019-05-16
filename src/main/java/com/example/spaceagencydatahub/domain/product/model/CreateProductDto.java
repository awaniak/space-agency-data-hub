package com.example.spaceagencydatahub.domain.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductDto {


    private Long missionId;

    private Instant acquisitionDate;

    private List<Coordinate> footprint;

    private double price;

    private String url;

}
