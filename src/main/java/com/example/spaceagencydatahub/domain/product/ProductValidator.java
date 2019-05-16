package com.example.spaceagencydatahub.domain.product;

import com.example.spaceagencydatahub.domain.mission.MissionService;
import com.example.spaceagencydatahub.domain.order.ProductOrderService;
import com.example.spaceagencydatahub.domain.product.model.CreateProductDto;
import com.example.spaceagencydatahub.domain.product.model.Product;
import com.example.spaceagencydatahub.util.ValidationResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductValidator {

    private MissionService missionService;

    private ProductService productService;

    private ProductOrderService productOrderService;

    public ProductValidator(MissionService missionService, ProductService productService, ProductOrderService productOrderService) {
        this.missionService = missionService;
        this.productService = productService;
        this.productOrderService = productOrderService;
    }

    ValidationResult checkIfAddProductValid(CreateProductDto createProductDto) {
        StringBuilder message = new StringBuilder();
        boolean isError = false;
        if (createProductDto.getMissionId() == null) {
            isError = true;
            message.append("Mission id is not provided; ");
        }
        if (!missionService.findById(createProductDto.getMissionId()).isPresent()) {
            isError = true;
            message.append("Mission with provided id does not exists; ");
        }
        if (createProductDto.getFootprint().size() != 4) {
            isError = true;
            message.append("Provided wrong coordinates; ");
        }
            return new ValidationResult(isError, message.toString());
    }


    ValidationResult checkIfRemoveProductValid(Long missionId) {
        StringBuilder message = new StringBuilder();
        boolean isError = false;
        Optional<Product> productToRemove = productService.findById(missionId);
        if (!productToRemove.isPresent()) {
            isError = true;
            message.append("Product with provided id does not exists; ");
        } else if (!productOrderService.findByProduct(productToRemove.get()).isEmpty()) {
            isError = true;
            message.append("Cannot remove product with orders; ");
        }

        return new ValidationResult(isError, message.toString());
    }

    ValidationResult checkIfOrderProductValid(List<Long> productsId) {
        StringBuilder message = new StringBuilder();
        boolean isError = false;
        for (Long productId : productsId) {
            if (!productService.findById(productId).isPresent()) {
                isError = true;
                message.append("Cannot find product with provided id: ").append(productId.toString());
            }
        }
        return new ValidationResult(isError, message.toString());
    }


}
