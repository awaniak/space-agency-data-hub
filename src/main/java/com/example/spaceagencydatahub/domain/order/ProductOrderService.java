package com.example.spaceagencydatahub.domain.order;

import com.example.spaceagencydatahub.domain.order.model.ProductOrder;
import com.example.spaceagencydatahub.domain.product.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductOrderService {

    ProductOrderRepository productOrderRepository;

    public ProductOrderService(ProductOrderRepository productOrderRepository) {
        this.productOrderRepository = productOrderRepository;
    }

    public List<ProductOrder> findByProduct(Product product) {
        return productOrderRepository.findByOrderedProduct(product);
    }

}
