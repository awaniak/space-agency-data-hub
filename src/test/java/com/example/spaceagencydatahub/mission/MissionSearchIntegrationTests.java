package com.example.spaceagencydatahub.mission;


import com.example.spaceagencydatahub.domain.mission.MissionRepository;
import com.example.spaceagencydatahub.domain.mission.model.ImageryType;
import com.example.spaceagencydatahub.domain.mission.model.Mission;
import com.example.spaceagencydatahub.domain.product.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MissionSearchIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String url = "/missions";
    private static final String managerName = "manager";


    @Before
    public void setUp() {
        productRepository.deleteAll();
        missionRepository.deleteAll();
        Mission mission = new Mission(1L, "Mission 1 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        Mission mission1 = new Mission(2L, "Mission 2 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        Mission mission2 = new Mission(3L, "Mission 3 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        Mission mission3 = new Mission(4L, "Mission 4 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        missionRepository.save(mission);
        missionRepository.save(mission1);
        missionRepository.save(mission2);
        missionRepository.save(mission3);
    }


    @WithMockUser(value = managerName)
    @Test
    public void shouldReturnAllMissions() throws Exception {
        MvcResult result = mvc.perform(
                get(url))
                .andExpect(status().isOk()).andDo(print()).andReturn();
        List<Mission> missionList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Mission>>() {
        });
        Assert.assertFalse(missionList.isEmpty());
        Assert.assertEquals(missionList.size(), missionRepository.count());
    }

}
