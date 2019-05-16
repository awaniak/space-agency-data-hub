package com.example.spaceagencydatahub.domain.mission;

import com.example.spaceagencydatahub.domain.mission.model.Mission;
import com.example.spaceagencydatahub.domain.mission.model.CreateMissionDto;
import com.example.spaceagencydatahub.domain.product.ProductService;
import com.example.spaceagencydatahub.util.ValidationResult;
import org.springframework.stereotype.Component;


@Component
public class MissionValidator {

    private MissionService missionService;

    private ProductService productService;

    public MissionValidator(MissionService missionService, ProductService productService) {
        this.missionService = missionService;
        this.productService = productService;
    }

    ValidationResult checkIfAddMissionValid(CreateMissionDto createMissionDto) {
        StringBuilder message = new StringBuilder();
        boolean isError = false;
        if (createMissionDto.getName() == null) {
            isError = true;
            message.append("Mission name is not provided; ");
        }
        if (missionService.findByName(createMissionDto.getName()).isPresent()) {
            isError = true;
            message.append("Mission with provided name exists; ");
        }
        return new ValidationResult(isError, message.toString());
    }

    ValidationResult checkIfEditMissionValid(Mission mission) {
        StringBuilder message = new StringBuilder();
        boolean isError = false;
        if (mission.getId() == null) {
            isError = true;
            message.append("Id is not provided; ");
        } else if (!missionService.findById(mission.getId()).isPresent()) {
            isError = true;
            message.append("Not found mission by id: ").append(mission.getId()).append("; ");
        }else if (mission.getName() == null) {
            isError = true;
            message.append("Mission name is not provided; ");
        }
        return new ValidationResult(isError, message.toString());

    }

    ValidationResult checkIfRemoveMissionValid(Long missionId) {
        StringBuilder message = new StringBuilder();
        boolean isError = false;
        if (!missionService.findById(missionId).isPresent()) {
            isError = true;
            message.append("Cannot find mission with provided id: ").append(missionId.toString());
        }
        else if (!productService.findByMissionId(missionId).isEmpty()){
            isError = true;
            message.append("Cannot remove mission with products; ");
        }
        return new ValidationResult(isError, message.toString());
    }

}
