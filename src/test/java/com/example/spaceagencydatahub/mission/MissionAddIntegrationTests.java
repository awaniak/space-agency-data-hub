package com.example.spaceagencydatahub.mission;

import com.example.spaceagencydatahub.domain.mission.MissionRepository;
import com.example.spaceagencydatahub.domain.mission.model.ImageryType;
import com.example.spaceagencydatahub.domain.mission.model.Mission;
import com.example.spaceagencydatahub.domain.mission.model.CreateMissionDto;
import com.example.spaceagencydatahub.domain.product.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MissionAddIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String url = "/missions";
    private static final String managerName = "manager";


    @Before
    public void setUp() {
        productRepository.deleteAll();
        missionRepository.deleteAll();
    }

    @WithMockUser(value = managerName)
    @Test
    public void shouldAddMission() throws Exception {
        Mission exampleMission = new Mission(10L, "mission_test", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        CreateMissionDto createMissionDto = new CreateMissionDto(exampleMission.getName(), exampleMission.getImageryType(), exampleMission.getStartDate(), exampleMission.getFinishDate());
        long sizeBeforeAdd = missionRepository.count();
        mvc.perform(
                post(url).content(objectMapper.writeValueAsString(createMissionDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andDo(print());
        Optional<Mission> savedMission = missionRepository.findByName(exampleMission.getName());
        Assert.assertTrue(savedMission.isPresent());
        Assert.assertEquals(savedMission.get().getName(), exampleMission.getName());
        Assert.assertEquals(savedMission.get().getImageryType(), exampleMission.getImageryType());
        Assert.assertEquals(savedMission.get().getStartDate(), exampleMission.getStartDate());
        Assert.assertEquals(savedMission.get().getFinishDate(), exampleMission.getFinishDate());
        Assert.assertEquals(sizeBeforeAdd + 1, missionRepository.count());

    }

    @WithMockUser(value = managerName)
    @Test
    public void shouldNotAddMissionWhenMissionNameExists() throws Exception {
        Mission exampleMission = new Mission(10L, "mission_test", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        missionRepository.save(exampleMission);
        CreateMissionDto createMissionDto = new CreateMissionDto(exampleMission.getName(), exampleMission.getImageryType(), exampleMission.getStartDate(), exampleMission.getFinishDate());
        long sizeBeforeAdd = missionRepository.count();
        mvc.perform(
                post(url).content(objectMapper.writeValueAsString(createMissionDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andDo(print());
        Assert.assertEquals(sizeBeforeAdd, missionRepository.count());
    }
}
