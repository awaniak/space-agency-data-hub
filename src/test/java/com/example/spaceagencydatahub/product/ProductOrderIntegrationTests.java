package com.example.spaceagencydatahub.product;


import com.example.spaceagencydatahub.domain.mission.MissionRepository;
import com.example.spaceagencydatahub.domain.mission.model.ImageryType;
import com.example.spaceagencydatahub.domain.mission.model.Mission;
import com.example.spaceagencydatahub.domain.order.ProductOrderRepository;
import com.example.spaceagencydatahub.domain.order.model.ProductOrder;
import com.example.spaceagencydatahub.domain.product.ProductRepository;
import com.example.spaceagencydatahub.domain.product.model.Coordinate;
import com.example.spaceagencydatahub.domain.product.model.CreateProductDto;
import com.example.spaceagencydatahub.domain.product.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
public class ProductOrderIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String url = "/products/order";
    private static final String customerName = "customer";

    @Before
    public void setUp() {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
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
    public void shouldOrderProduct() throws Exception {
        List<Product> products = productRepository.findAll();
        Product product1 = products.get(0);
        Product product2 = products.get(1);
        mvc.perform(
                get(url).param("productsId", product1.getId().toString(), product2.getId().toString()))
                .andExpect(status().isOk()).andDo(print());
        List<ProductOrder> productOrders = productOrderRepository.findByUserName(customerName);
        Assert.assertEquals(2, productOrders.size());
    }

    @WithMockUser(value = customerName)
    @Test
    public void shouldNotOrderProductWhenProductIdNotExist() throws Exception {
        mvc.perform(
                get(url).param("productsId", "-1"))
                .andExpect(status().isBadRequest()).andDo(print());
    }


}
