package com.example.spaceagencydatahub.domain.mission;

import com.example.spaceagencydatahub.domain.mission.model.Mission;
import com.example.spaceagencydatahub.domain.mission.model.MissionPayload;
import com.example.spaceagencydatahub.util.ValidationResult;
import org.springframework.stereotype.Component;

import javax.validation.Validation;

@Component
public class MissionValidator {

    private MissionService missionService;

    public MissionValidator(MissionService missionService) {
        this.missionService = missionService;
    }

    ValidationResult checkIfAddMissionValid(MissionPayload missionPayload) {
        if (missionService.findByName(missionPayload.getName()).isPresent()) {
            return new ValidationResult(true, "Mission with provided name exists");
        }
        return new ValidationResult(false, "");
    }

    ValidationResult checkIfEditMissionValid(Mission mission) {
        if (mission.getId() == null) {
            return new ValidationResult(true, "Id is not provided");
        } else {
            return new ValidationResult(false, "");
        }
    }

    ValidationResult checkIfRemoveMissionValid(Long missionId) {
        if (!missionService.findById(missionId).isPresent()) {
            return new ValidationResult(true, "Cannot find mission with provided id: " + missionId);
        } else {
            return new ValidationResult(false, "");
        }
    }

}
