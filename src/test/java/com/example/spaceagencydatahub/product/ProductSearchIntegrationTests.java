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
import com.fasterxml.jackson.databind.SerializationFeature;
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
import java.util.stream.Collectors;

import static com.example.spaceagencydatahub.util.DataLoader.getCoordinateFootprintForProduct;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductSearchIntegrationTests {

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

    private static final String url = "/products";
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

        List<Product> products = productRepository.findAll();
        ProductOrder productOrder = new ProductOrder();
        productOrder.setUserName("customer");
        productOrder.setOrderedProduct(products.get(0));
        ProductOrder productOrder1 = new ProductOrder();
        productOrder1.setUserName("customer");
        productOrder1.setOrderedProduct(products.get(1));
        productOrderRepository.save(productOrder);
        productOrderRepository.save(productOrder1);
    }

    @WithMockUser(value = customerName)
    @Test
    public void shouldReturnAllProducts() throws Exception {

        MvcResult result = mvc.perform(
                get(url))
                .andExpect(status().isOk()).andDo(print()).andReturn();
        List<Product> productList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Product>>() {
        });
        Assert.assertFalse(productList.isEmpty());
        Assert.assertEquals(productRepository.count(), productList.size());
    }

    @WithMockUser(value = customerName)
    @Test
    public void shouldHideUrlOfProducts() throws Exception {

        MvcResult result = mvc.perform(
                get(url))
                .andExpect(status().isOk()).andDo(print()).andReturn();
        List<Product> productList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Product>>() {
        });
        List<String> listOfUrls = productList.stream().map(Product::getUrl).collect(Collectors.toList());
        long notEmptyUrls = listOfUrls.stream().filter(s -> !s.isEmpty()).count();
        long emptyUrls = listOfUrls.stream().filter(s -> s.isEmpty()).count();
        Assert.assertEquals(2, notEmptyUrls);
        Assert.assertEquals(3, emptyUrls);
    }

    @WithMockUser(value = customerName)
    @Test
    public void shouldReturnProductsByMissionName() throws Exception {

        MvcResult result = mvc.perform(
                get(url).param("missionName", "Mission 1 - TEST"))
                .andExpect(status().isOk()).andDo(print()).andReturn();
        List<Product> productList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Product>>() {
        });
        Assert.assertEquals(3, productList.size());
    }

    @WithMockUser(value = customerName)
    @Test
    public void shouldReturnProductsByProductType() throws Exception {

        MvcResult result = mvc.perform(
                get(url).param("productType", ImageryType.Hyperspectral.toString()))
                .andExpect(status().isOk()).andDo(print()).andReturn();
        List<Product> productList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Product>>() {
        });
        Assert.assertEquals(2, productList.size());
    }

    @WithMockUser(value = customerName)
    @Test
    public void shouldReturnProductsByDateAfter() throws Exception {

        MvcResult result = mvc.perform(
                get(url).param("dateAfter", Instant.now().minus(1, ChronoUnit.DAYS).toString()))
                .andExpect(status().isOk()).andDo(print()).andReturn();
        List<Product> productList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Product>>() {
        });
        Assert.assertEquals(4, productList.size());
    }

    @WithMockUser(value = customerName)
    @Test
    public void shouldReturnProductsByDateBefore() throws Exception {

        MvcResult result = mvc.perform(
                get(url).param("dateBefore", Instant.now().minus(1, ChronoUnit.DAYS).toString()))
                .andExpect(status().isOk()).andDo(print()).andReturn();
        List<Product> productList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Product>>() {
        });
        Assert.assertEquals(1, productList.size());
    }

    @WithMockUser(value = customerName)
    @Test
    public void shouldReturnProductsByDateBetween() throws Exception {

        MvcResult result = mvc.perform(
                get(url)
                        .param("dateAfter", Instant.now().minus(1, ChronoUnit.DAYS).toString())
                        .param("dateBefore", Instant.now().plus(1, ChronoUnit.DAYS).toString()))
                .andExpect(status().isOk()).andDo(print()).andReturn();
        List<Product> productList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Product>>() {
        });
        Assert.assertEquals(3, productList.size());
    }

    @WithMockUser(value = customerName)
    @Test
    public void shouldReturnProductsByMissionNameAndDateAfter() throws Exception {

        MvcResult result = mvc.perform(
                get(url)
                        .param("missionName", "Mission 1 - TEST")
                        .param("dateAfter", Instant.now().minus(1, ChronoUnit.DAYS).toString()))
                .andExpect(status().isOk()).andDo(print()).andReturn();
        List<Product> productList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Product>>() {
        });
        Assert.assertEquals(2, productList.size());
    }

}
