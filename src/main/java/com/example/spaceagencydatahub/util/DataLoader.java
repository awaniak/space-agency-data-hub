package com.example.spaceagencydatahub.util;


import com.example.spaceagencydatahub.domain.mission.MissionRepository;
import com.example.spaceagencydatahub.domain.mission.MissionService;
import com.example.spaceagencydatahub.domain.mission.model.ImageryType;
import com.example.spaceagencydatahub.domain.mission.model.Mission;
import com.example.spaceagencydatahub.domain.mission.model.MissionPayload;
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

    @Autowired
    ProductRepository productRepository;

    @Autowired
    MissionService missionService;

    @Override
    public void run(String... args) {
        MissionPayload mission = new MissionPayload( "Mission 1", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        MissionPayload mission1 = new MissionPayload("Mission 2", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        MissionPayload mission2 = new MissionPayload("Mission 3", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        MissionPayload mission3 = new MissionPayload("Mission 4", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        missionService.add(mission);
        missionService.add(mission1);
        missionService.add(mission2);
        missionService.add(mission3);
    }

}
