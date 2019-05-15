package com.example.spaceagencydatahub.domain.product;

import com.example.spaceagencydatahub.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
