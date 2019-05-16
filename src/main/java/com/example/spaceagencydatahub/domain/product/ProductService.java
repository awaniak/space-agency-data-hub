package com.example.spaceagencydatahub.domain.product;

import com.example.spaceagencydatahub.domain.mission.MissionService;
import com.example.spaceagencydatahub.domain.mission.model.ImageryType;
import com.example.spaceagencydatahub.domain.mission.model.Mission;
import com.example.spaceagencydatahub.domain.order.ProductOrderRepository;
import com.example.spaceagencydatahub.domain.order.model.ProductOrder;
import com.example.spaceagencydatahub.domain.product.model.Coordinate;
import com.example.spaceagencydatahub.domain.product.model.CreateProductDto;
import com.example.spaceagencydatahub.domain.product.model.Product;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private ProductRepository productRepository;

    private MissionService missionService;

    private ProductOrderRepository productOrderRepository;

    public ProductService(ProductRepository productRepository, MissionService missionService, ProductOrderRepository productOrderRepository) {
        this.productRepository = productRepository;
        this.missionService = missionService;
        this.productOrderRepository = productOrderRepository;
    }

    public void remove(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> search(String missionName, ImageryType productType, Instant dateAfter, Instant dateBefore, Coordinate coordinate) {
        List<Product> result = new ArrayList<>(productRepository.findAll());
        if (missionName != null) {
            List<Product> productsByMissionName = productRepository.findByMission_Name(missionName);
            result = result.stream().filter(productsByMissionName::contains).collect(Collectors.toList());
        }
        if (productType != null) {
            List<Product> productsByProductType = productRepository.findByMission_ImageryType(productType);
            result = result.stream().filter(productsByProductType::contains).collect(Collectors.toList());
        }
        if (dateAfter != null) {
            List<Product> productByAcquisitionDateAfter = productRepository.findByAcquisitionDateAfter(dateAfter);
            result = result.stream().filter(productByAcquisitionDateAfter::contains).collect(Collectors.toList());
        }
        if (dateBefore != null) {
            List<Product> productByAcquisitionDateBefore = productRepository.findByAcquisitionDateBefore(dateBefore);
            result = result.stream().filter(productByAcquisitionDateBefore::contains).collect(Collectors.toList());
        }
        return result;
    }

    public Product createProduct(CreateProductDto createProductDto) {
        Product product = new Product();
        Optional<Mission> mission = missionService.findById(createProductDto.getMissionId());
        if (!mission.isPresent()) {
            throw  new NoSuchElementException("Cannot find mission by id: " + createProductDto.getMissionId().toString());
        }
        product.setMission(mission.get());
        product.setAcquisitionDate(createProductDto.getAcquisitionDate());
        product.setFootprint(createProductDto.getFootprint());
        product.setPrice(createProductDto.getPrice());
        product.setUrl(createProductDto.getUrl());
        return productRepository.save(product);
    }

    public List<Product> findByMissionId(Long id) {
        return productRepository.findByMission_Id(id);
    }

    public void order(List<Long> productsId, String name) {
        for(Long productId: productsId) {
            ProductOrder productOrder = new ProductOrder();
            Optional<Product> product = productRepository.findById(productId);
            if (!product.isPresent()) {
                throw new NoSuchElementException("Cannot find product by id: " + productId.toString());
            }
            productOrder.setUserName(name);
            productOrder.setOrderedProduct(product.get());
            productOrderRepository.save(productOrder);
        }
    }

    public List<Product> processProductNotOrderedByUser(List<Product> products, String name) {
        List<Product> userProduct = productOrderRepository.findByUserName(name).stream().map(ProductOrder::getOrderedProduct).collect(Collectors.toList());
        products.forEach(product -> {
            if (!userProduct.contains(product)) {
                product.setUrl("");
            }
        });
        return products;
    }

    public Optional<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }
}
