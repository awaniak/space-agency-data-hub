package com.example.spaceagencydatahub.domain.mission.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MissionPayload {

    private String name;
    private ImageryType imageryType;
    private Instant startDate;
    private Instant finishDate;

}
