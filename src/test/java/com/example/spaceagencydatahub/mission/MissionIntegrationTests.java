package com.example.spaceagencydatahub.mission;


import com.example.spaceagencydatahub.domain.mission.MissionRepository;
import com.example.spaceagencydatahub.domain.mission.model.ImageryType;
import com.example.spaceagencydatahub.domain.mission.model.Mission;
import com.example.spaceagencydatahub.domain.mission.model.MissionPayload;
import com.example.spaceagencydatahub.domain.product.ProductRepository;
import com.example.spaceagencydatahub.domain.product.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MissionIntegrationTests {

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
        missionRepository.deleteAll();
    }

    @WithMockUser(value = managerName)
    @Test
    public void shouldAddMission() throws Exception {
        Mission exampleMission = new Mission(10L, "mission_test", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS), new ArrayList<>());
        MissionPayload missionPayload = new MissionPayload(exampleMission.getName(), exampleMission.getImageryType(), exampleMission.getStartDate(), exampleMission.getFinishDate());
        long sizeBeforeAdd = missionRepository.count();
        mvc.perform(
                post(url).content(objectMapper.writeValueAsString(missionPayload)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();
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
    public void shouldReturnAllMissions() throws Exception {
        setUpMissions();
        MvcResult result = mvc.perform(
                get(url))
                .andExpect(status().isOk()).andReturn();
        List<Mission> missionList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Mission>>() {
        });
        Assert.assertFalse(missionList.isEmpty());
        Assert.assertEquals(missionList.size(), missionRepository.count());
    }

    @WithMockUser(value = managerName)
    @Test
    public void shouldRemoveMission() throws Exception {
        setUpMissions();
        long sizeBeforeRemove = missionRepository.count();
        List<Mission> missions = missionRepository.findAll();
        Mission missionToRemove = missions.get(0);
        mvc.perform(
                delete(url + "/" + missionToRemove.getId()))
                .andExpect(status().isOk()).andReturn();
        Assert.assertEquals(sizeBeforeRemove - 1, missionRepository.count());
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
                .andExpect(status().isOk()).andReturn();
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
                .andExpect(status().isNotFound());
        Optional<Mission> missionAfterEdit = missionRepository.findById(missionId);
        Assert.assertTrue(missionAfterEdit.isPresent());
        Assert.assertEquals(oldName, missionAfterEdit.get().getName());
    }

    @WithMockUser(value = managerName)
    @Test
    public void shouldNotAddMissionWhenMissionNameExists() throws Exception {
        Mission exampleMission = new Mission(10L, "mission_test", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS), new ArrayList<>());
        missionRepository.save(exampleMission);
        MissionPayload missionPayload = new MissionPayload(exampleMission.getName(), exampleMission.getImageryType(), exampleMission.getStartDate(), exampleMission.getFinishDate());
        long sizeBeforeAdd = missionRepository.count();
        mvc.perform(
                post(url).content(objectMapper.writeValueAsString(missionPayload)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        Assert.assertEquals(sizeBeforeAdd, missionRepository.count());
    }

    @WithMockUser(value = managerName)
    @Test
    public void shouldNotRemoveMissionWhenProductsExist() throws Exception {
        Mission mission = new Mission(1L, "Mission 1 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS), new ArrayList<>());
        Product product = new Product(1L, mission, Instant.now(), new ArrayList<>(), 20, "url");
        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        mission.setProducts(products);
        missionRepository.save(mission);
        Optional<Mission> savedMission = missionRepository.findByName(mission.getName());

        long sizeBeforeRemove = missionRepository.count();
        mvc.perform(
                delete(url + "/" + savedMission.get().getId()))
                .andExpect(status().isBadRequest());
        Assert.assertEquals(sizeBeforeRemove, missionRepository.count());
    }



    private void setUpMissions() {
        Mission mission = new Mission(1L, "Mission 1 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS), new ArrayList<>());
        Mission mission1 = new Mission(2L, "Mission 2 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS), new ArrayList<>());
        Mission mission2 = new Mission(3L, "Mission 3 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS), new ArrayList<>());
        Mission mission3 = new Mission(4L, "Mission 4 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS), new ArrayList<>());
        missionRepository.save(mission);
        missionRepository.save(mission1);
        missionRepository.save(mission2);
        missionRepository.save(mission3);
    }


}
