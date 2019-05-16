package com.example.spaceagencydatahub.domain.product;

import com.example.spaceagencydatahub.domain.mission.model.ImageryType;
import com.example.spaceagencydatahub.domain.mission.model.Mission;
import com.example.spaceagencydatahub.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByMission_Name(String missionName);
    List<Product> findByMission_ImageryType(ImageryType imageryType);
    List<Product> findByAcquisitionDateAfter(Instant date);
    List<Product> findByAcquisitionDateBefore(Instant date);
    List<Product> findByMission_Id(Long id);
}
