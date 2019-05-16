package com.example.spaceagencydatahub.util;


import com.example.spaceagencydatahub.domain.mission.MissionRepository;
import com.example.spaceagencydatahub.domain.mission.model.ImageryType;
import com.example.spaceagencydatahub.domain.mission.model.Mission;
import com.example.spaceagencydatahub.domain.order.ProductOrderRepository;
import com.example.spaceagencydatahub.domain.product.ProductRepository;
import com.example.spaceagencydatahub.domain.product.model.Coordinate;
import com.example.spaceagencydatahub.domain.product.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    MissionRepository missionRepository;

    @Autowired
    ProductOrderRepository productOrderRepository;

    @Override
    public void run(String... args) {
        Mission mission = new Mission(1L, "Mission 1 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        Mission mission1 = new Mission(2L, "Mission 2 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        Mission mission2 = new Mission(3L, "Mission 3 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        Mission mission3 = new Mission(4L, "Mission 4 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        missionRepository.save(mission);
        missionRepository.save(mission1);
        missionRepository.save(mission2);
        missionRepository.save(mission3);

        Product product = new Product(1L, mission, Instant.now(), getCoordinateFootprintForProduct(), 60.00, "http://addrestoPhoto.com");
        Product product1 = new Product(2L, mission, Instant.now(), getCoordinateFootprintForProduct(), 60.00, "http://addrestoPhoto.com");
        Product product2 = new Product(3L, mission, Instant.now(), getCoordinateFootprintForProduct(), 60.00, "http://addrestoPhoto.com");
        Product product3 = new Product(4L, mission, Instant.now(), getCoordinateFootprintForProduct(), 60.00, "http://addrestoPhoto.com");

        productRepository.save(product);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
    }

    public static List<Coordinate> getCoordinateFootprintForProduct() {
        List<Coordinate> result = new ArrayList<>();
        result.add(new Coordinate(5, 5));
        result.add(new Coordinate(5, -5));
        result.add(new Coordinate(-5, 5));
        result.add(new Coordinate(-5, -5));
        return result;
    }

}
