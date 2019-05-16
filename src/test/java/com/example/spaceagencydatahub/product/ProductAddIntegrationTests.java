package com.example.spaceagencydatahub.product;


import com.example.spaceagencydatahub.domain.mission.MissionRepository;
import com.example.spaceagencydatahub.domain.mission.model.ImageryType;
import com.example.spaceagencydatahub.domain.mission.model.Mission;
import com.example.spaceagencydatahub.domain.product.ProductRepository;
import com.example.spaceagencydatahub.domain.product.model.Coordinate;
import com.example.spaceagencydatahub.domain.product.model.CreateProductDto;
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

import static com.example.spaceagencydatahub.util.DataLoader.getCoordinateFootprintForProduct;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductAddIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String url = "/products";
    private static final String managerName = "manager";

    @Before
    public void setUp() {
        productRepository.deleteAll();
        missionRepository.deleteAll();
        Mission mission = new Mission(1L, "Mission 1 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        Mission mission1 = new Mission(2L, "Mission 2 - TEST", ImageryType.Hyperspectral, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        missionRepository.save(mission);
        missionRepository.save(mission1);

    }

    @WithMockUser(value = managerName)
    @Test
    public void shouldAddProduct() throws Exception {
        Mission mission = missionRepository.findByName("Mission 1 - TEST").get();
        CreateProductDto createProductDto = new CreateProductDto(mission.getId(), Instant.now(), getCoordinateFootprintForProduct(), 10.00, "url");
        long sizeBeforeAdd = productRepository.count();
        mvc.perform(
                post(url).content(objectMapper.writeValueAsString(createProductDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andDo(print()).andReturn();
        Assert.assertEquals(sizeBeforeAdd + 1, productRepository.count());
    }

    @WithMockUser(value = managerName)
    @Test
    public void shouldNotAddProductWhenMissionIdNotExist() throws Exception {
        CreateProductDto createProductDto = new CreateProductDto(-1L, Instant.now(), getCoordinateFootprintForProduct(), 10.00, "url");
        long sizeBeforeAdd = productRepository.count();
        mvc.perform(
                post(url).content(objectMapper.writeValueAsString(createProductDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andDo(print()).andReturn();
        Assert.assertEquals(sizeBeforeAdd, productRepository.count());
    }


}
