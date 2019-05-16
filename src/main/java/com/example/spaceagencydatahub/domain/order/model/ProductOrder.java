package com.example.spaceagencydatahub.domain.order.model;

import com.example.spaceagencydatahub.domain.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String userName;

    @ManyToOne()
    private Product orderedProduct;

    @CreationTimestamp
    private Instant orderedDate;
}
