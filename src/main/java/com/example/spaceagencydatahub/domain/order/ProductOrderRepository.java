package com.example.spaceagencydatahub.domain.order;

import com.example.spaceagencydatahub.domain.order.model.ProductOrder;
import com.example.spaceagencydatahub.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
    List<ProductOrder> findByUserName(String userName);

    List<ProductOrder> findByOrderedProduct(Product product);
}
