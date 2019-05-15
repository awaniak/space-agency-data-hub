package com.example.spaceagencydatahub.domain.mission.model;

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

    public Mission(MissionPayload missionPayload) {
        this.name = missionPayload.getName();
        this.imageryType = missionPayload.getImageryType();
        this.startDate = missionPayload.getStartDate();
        this.finishDate = missionPayload.getFinishDate();
    }

}