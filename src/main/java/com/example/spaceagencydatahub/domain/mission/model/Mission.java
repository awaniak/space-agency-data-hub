package com.example.spaceagencydatahub.domain.mission.model;

import com.example.spaceagencydatahub.domain.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String name;
    private ImageryType imageryType;
    private Instant startDate;
    private Instant finishDate;

    public Mission(CreateMissionDto createMissionDto) {
        this.name = createMissionDto.getName();
        this.imageryType = createMissionDto.getImageryType();
        this.startDate = createMissionDto.getStartDate();
        this.finishDate = createMissionDto.getFinishDate();
    }

}
