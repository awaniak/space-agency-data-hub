package com.example.spaceagencydatahub.mission;

import com.example.spaceagencydatahub.domain.mission.MissionRepository;
import com.example.spaceagencydatahub.domain.mission.model.ImageryType;
import com.example.spaceagencydatahub.domain.mission.model.Mission;
import com.example.spaceagencydatahub.domain.product.ProductRepository;
import com.example.spaceagencydatahub.domain.product.model.Product;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MissionDeleteIntegrationTests {


    @Autowired
    private MockMvc mvc;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ProductRepository productRepository;

    private static final String url = "/missions";
    private static final String managerName = "manager";


    @Before
    public void setUp() {
        productRepository.deleteAll();
        missionRepository.deleteAll();
    }

    @WithMockUser(value = managerName)
    @Test
    public void shouldRemoveMission() throws Exception {
        Mission mission = new Mission(1L, "Mission 1 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        Mission mission1 = new Mission(2L, "Mission 2 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        missionRepository.save(mission);
        missionRepository.save(mission1);
        long sizeBeforeRemove = missionRepository.count();
        Mission missionToRemove = missionRepository.findByName(mission.getName()).get();
        mvc.perform(
                delete(url + "/" + missionToRemove.getId()))
                .andExpect(status().isOk()).andDo(print());
        Assert.assertEquals(sizeBeforeRemove - 1, missionRepository.count());
    }

    @WithMockUser(value = managerName)
    @Test
    public void shouldNotRemoveMissionWhenProductsExist() throws Exception {
        Mission mission = new Mission(1L, "Mission 1 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        missionRepository.save(mission);
        mission = missionRepository.findByName("Mission 1 - TEST").get();
        Product product = new Product(1L, mission, Instant.now(), new ArrayList<>(), 20, "url");
        productRepository.save(product);
        Optional<Mission> savedMission = missionRepository.findByName(mission.getName());

        long sizeBeforeRemove = missionRepository.count();
        mvc.perform(
                delete(url + "/" + savedMission.get().getId()))
                .andExpect(status().isBadRequest()).andDo(print());
        Assert.assertEquals(sizeBeforeRemove, missionRepository.count());
    }

}
