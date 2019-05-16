package com.example.spaceagencydatahub.mission;


import com.example.spaceagencydatahub.domain.mission.MissionRepository;
import com.example.spaceagencydatahub.domain.mission.model.ImageryType;
import com.example.spaceagencydatahub.domain.mission.model.Mission;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MissionEditIntegrationTests {

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
    }


    @WithMockUser(value = managerName)
    @Test
    public void shouldEditMission() throws Exception {
        setUpMissions();
        long sizeBeforeEdit = missionRepository.count();
        List<Mission> missions = missionRepository.findAll();
        Mission missionToEdit = missions.get(0);
        String nameBeforeEdit = missionToEdit.getName();
        String newName = "new-name-test";
        missionToEdit.setName(newName);
        mvc.perform(
                put(url).content(objectMapper.writeValueAsString(missionToEdit)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(print()).andReturn();
        Optional<Mission> missionAfterEdit = missionRepository.findById(missionToEdit.getId());
        Assert.assertTrue(missionAfterEdit.isPresent());
        Assert.assertNotEquals(nameBeforeEdit, missionAfterEdit.get().getName());
        Assert.assertEquals(newName, missionAfterEdit.get().getName());
        Assert.assertEquals(missionToEdit.getStartDate(), missionAfterEdit.get().getStartDate());
        Assert.assertEquals(missionToEdit.getFinishDate(), missionAfterEdit.get().getFinishDate());
        Assert.assertEquals(missionToEdit.getImageryType(), missionAfterEdit.get().getImageryType());
        Assert.assertEquals(sizeBeforeEdit, missionRepository.count());
    }

    @WithMockUser(value = managerName)
    @Test
    public void shouldNotEditMissionWhenIdIsNotProvided() throws Exception{
        setUpMissions();
        List<Mission> missions = missionRepository.findAll();
        Mission missionToEdit = missions.get(0);
        String oldName = missionToEdit.getName();
        Long missionId = missionToEdit.getId();
        missionToEdit.setName("new-name-test");
        missionToEdit.setId(null);
        mvc.perform(
                put(url).content(objectMapper.writeValueAsString(missionToEdit)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andDo(print());
        Optional<Mission> missionAfterEdit = missionRepository.findById(missionId);
        Assert.assertTrue(missionAfterEdit.isPresent());
        Assert.assertEquals(oldName, missionAfterEdit.get().getName());
    }


    private void setUpMissions() {
        Mission mission = new Mission(1L, "Mission 1 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        Mission mission1 = new Mission(2L, "Mission 2 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        Mission mission2 = new Mission(3L, "Mission 3 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        Mission mission3 = new Mission(4L, "Mission 4 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        missionRepository.save(mission);
        missionRepository.save(mission1);
        missionRepository.save(mission2);
        missionRepository.save(mission3);
    }


}
