package com.example.spaceagencydatahub.product;


import com.example.spaceagencydatahub.domain.mission.MissionRepository;
import com.example.spaceagencydatahub.domain.mission.model.ImageryType;
import com.example.spaceagencydatahub.domain.mission.model.Mission;
import com.example.spaceagencydatahub.domain.order.ProductOrderRepository;
import com.example.spaceagencydatahub.domain.order.model.ProductOrder;
import com.example.spaceagencydatahub.domain.product.ProductRepository;
import com.example.spaceagencydatahub.domain.product.model.Coordinate;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.example.spaceagencydatahub.util.DataLoader.getCoordinateFootprintForProduct;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductDeleteIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    private static final String url = "/products";
    private static final String customerName = "customer";

    @Before
    public void setUp() {
        productOrderRepository.deleteAll();
        productRepository.deleteAll();
        missionRepository.deleteAll();
        Mission mission = new Mission(1L, "Mission 1 - TEST", ImageryType.Panchromatic, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        Mission mission1 = new Mission(2L, "Mission 2 - TEST", ImageryType.Hyperspectral, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        missionRepository.save(mission);
        missionRepository.save(mission1);

        mission = missionRepository.findByName("Mission 1 - TEST").get();
        mission1 = missionRepository.findByName("Mission 2 - TEST").get();


        Product product = new Product(1L, mission, Instant.now(), getCoordinateFootprintForProduct(), 50.00, "product 1 url");
        Product product1 = new Product(2L, mission, Instant.now().plus(5, ChronoUnit.DAYS), getCoordinateFootprintForProduct(), 50.00, "product 1 url");
        Product product2 = new Product(3L, mission, Instant.now().minus(5, ChronoUnit.DAYS), getCoordinateFootprintForProduct(), 50.00, "product 1 url");
        Product product3 = new Product(4L, mission1, Instant.now(), getCoordinateFootprintForProduct(), 50.00, "product 1 url");
        Product product4 = new Product(5L, mission1, Instant.now(), getCoordinateFootprintForProduct(), 50.00, "product 1 url");

        productRepository.save(product);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        productRepository.save(product4);
    }

    @WithMockUser(value = customerName)
    @Test
    public void shouldDeleteProduct() throws Exception {
        Product product = productRepository.findAll().get(0);
        long sizeBeforeRemove = productRepository.count();
        mvc.perform(
                delete(url + "/" + product.getId().toString()))
                .andExpect(status().isOk()).andDo(print());
        Assert.assertEquals(sizeBeforeRemove - 1, productRepository.count());
    }

    @WithMockUser(value = customerName)
    @Test
    public void shouldNotDeleteProductWhenOrdersExist() throws Exception {
        Product product = productRepository.findAll().get(0);
        productOrderRepository.save(new ProductOrder(1L, "customer", product, Instant.now()));
        long sizeBeforeRemove = productRepository.count();
        mvc.perform(
                delete(url + "/" + product.getId().toString()))
                .andExpect(status().isBadRequest()).andDo(print());
        Assert.assertEquals(sizeBeforeRemove, productRepository.count());
    }


}
