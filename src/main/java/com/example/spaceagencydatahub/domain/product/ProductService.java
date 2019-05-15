package com.example.spaceagencydatahub.domain.product;

import com.example.spaceagencydatahub.domain.product.model.Product;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product add(Product product) {
        return productRepository.save(product);
    }

    public Product edit(Product product) {
        return productRepository.save(product);
    }

    public void remove(Long id) {
        productRepository.deleteById(id);
    }

}
