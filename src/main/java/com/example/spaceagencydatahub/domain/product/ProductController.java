package com.example.spaceagencydatahub.domain.product;


import com.example.spaceagencydatahub.domain.mission.model.ImageryType;
import com.example.spaceagencydatahub.domain.product.model.Coordinate;
import com.example.spaceagencydatahub.domain.product.model.CreateProductDto;
import com.example.spaceagencydatahub.domain.product.model.Product;
import com.example.spaceagencydatahub.util.ValidationResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private ProductService productService;

    private ProductValidator productValidator;

    public ProductController(ProductService productService, ProductValidator productValidator) {
        this.productService = productService;
        this.productValidator = productValidator;
    }


    @GetMapping
    @RolesAllowed({"MANAGER", "CUSTOMER"})
    public ResponseEntity searchForProducts(@RequestParam(required = false) String missionName,
                                            @RequestParam(required = false) ImageryType productType,
                                            @RequestParam(required = false) Instant dateAfter,
                                            @RequestParam(required = false) Instant dateBefore,
                                            @RequestParam(required = false)Coordinate coordinate,
                                            Principal principal) {
        List<Product> products = productService.search(missionName, productType, dateAfter, dateBefore, coordinate);
        products = productService.processProductNotOrderedByUser(products, principal.getName());
        return ResponseEntity.ok(products);
    }

    @PostMapping
    @RolesAllowed("MANAGER")
    public ResponseEntity addProduct(@RequestBody CreateProductDto createProductDto) {
        ValidationResult validationResult = productValidator.checkIfAddProductValid(createProductDto);
        if (validationResult.isError()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResult.getMessage());
        }
        Product product = productService.createProduct(createProductDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @DeleteMapping("/{productId}")
    @RolesAllowed("MANAGER")
    public ResponseEntity deleteProduct(@PathVariable Long productId) {
        ValidationResult validationResult = productValidator.checkIfRemoveProductValid(productId);
        if (validationResult.isError()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResult.getMessage());
        }
        productService.remove(productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/order")
    @RolesAllowed("CUSTOMER")
    public ResponseEntity orderProduct(@RequestParam List<Long> productsId, Principal principal) {
        ValidationResult validationResult = productValidator.checkIfOrderProductValid(productsId);
        if (validationResult.isError()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResult.getMessage());
        }
        productService.order(productsId, principal.getName());
        return ResponseEntity.ok().build();
    }

}
