package com.example.spaceagencydatahub.domain.mission;

import com.example.spaceagencydatahub.domain.mission.model.Mission;
import com.example.spaceagencydatahub.domain.mission.model.MissionPayload;
import com.example.spaceagencydatahub.util.ValidationResult;
import org.springframework.stereotype.Component;


@Component
public class MissionValidator {

    private MissionService missionService;

    public MissionValidator(MissionService missionService) {
        this.missionService = missionService;
    }

    ValidationResult checkIfAddMissionValid(MissionPayload missionPayload) {
        StringBuilder message = new StringBuilder();
        boolean isError = false;
        if (missionPayload.getName() == null) {
            isError = true;
            message.append("Mission name is not provided; ");
        }
        if (missionPayload.getStartDate() == null) {
            isError = true;
            message.append("Start date is not provided; ");
        }
        if (missionPayload.getFinishDate() == null) {
            isError = true;
            message.append("Finish date is not provided; ");
        }
        if (missionPayload.getImageryType() == null) {
            isError = true;
            message.append("Imagery type is not provided; ");
        }
        if (missionService.findByName(missionPayload.getName()).isPresent()) {
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
        } else if (mission.getStartDate() == null) {
            isError = true;
            message.append("Start date is not provided; ");
        } else if (mission.getFinishDate() == null) {
            isError = true;
            message.append("Finish date is not provided; ");
        } else if (mission.getImageryType() == null) {
            isError = true;
            message.append("Imagery type is not provided; ");
        }
        return new ValidationResult(isError, message.toString());

    }

    ValidationResult checkIfRemoveMissionValid(Long missionId) {
        StringBuilder message = new StringBuilder();
        boolean isError = false;
        if (!missionService.findById(missionId).isPresent()) {
            isError = true;
            message.append("Cannot find mission with provided id: ").append(missionId.toString());
        } else if (!missionService.findById(missionId).get().getProducts().isEmpty()){
            isError = true;
            message.append("Cannot remove mission with products; ");
        }
        return new ValidationResult(isError, message.toString());
    }

}
