package com.example.spaceagencydatahub.util;


import com.example.spaceagencydatahub.domain.mission.MissionRepository;
import com.example.spaceagencydatahub.domain.mission.model.ImageryType;
import com.example.spaceagencydatahub.domain.mission.model.Mission;
import com.example.spaceagencydatahub.domain.product.ProductRepository;
import com.example.spaceagencydatahub.domain.product.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class DataLoader implements CommandLineRunner {

    private static Logger LOG = LoggerFactory
            .getLogger(DataLoader.class);

    @Autowired
    ProductRepository productRepository;

    @Autowired
    MissionRepository missionRepository;

    @Override
    public void run(String... args) throws Exception {
        Mission mission = new Mission(1L, "Mission 1", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        Product product = new Product();

        LOG.info("Loaded users");
    }

}
