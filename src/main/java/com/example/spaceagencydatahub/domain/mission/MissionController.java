package com.example.spaceagencydatahub.domain.mission;

import com.example.spaceagencydatahub.domain.mission.model.Mission;
import com.example.spaceagencydatahub.domain.mission.model.MissionPayload;
import com.example.spaceagencydatahub.util.ValidationResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/missions")
public class MissionController {

    private MissionService missionService;
    private MissionValidator missionValidator;

    public MissionController(MissionService missionService, MissionValidator missionValidator) {
        this.missionService = missionService;
        this.missionValidator = missionValidator;
    }

    @GetMapping
    public ResponseEntity getMissions() {
        return ResponseEntity.status(HttpStatus.OK).body(missionService.findAll());
    }

    @PostMapping
    @RolesAllowed("MANAGER")
    public ResponseEntity addMission(@RequestBody MissionPayload newMission) {
        ValidationResult validationResult = missionValidator.checkIfAddMissionValid(newMission);
        if (validationResult.isError()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResult.getMessage());
        }
        Mission mission = missionService.add(newMission);
        return ResponseEntity.status(HttpStatus.CREATED).body(mission);
    }

    @PutMapping
    @RolesAllowed("MANAGER")
    public ResponseEntity editMission(@RequestBody Mission missionToEdit) {
        ValidationResult validationResult = missionValidator.checkIfEditMissionValid(missionToEdit);
        if (validationResult.isError()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(validationResult.getMessage());
        }
        Mission mission = missionService.edit(missionToEdit);
        return ResponseEntity.status(HttpStatus.OK).body(mission);
    }

    @DeleteMapping(path = "/{missionId}")
    @RolesAllowed("MANAGER")
    public ResponseEntity removeMission(@PathVariable Long missionId) {
        ValidationResult validationResult = missionValidator.checkIfRemoveMissionValid(missionId);
        if (validationResult.isError()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResult.getMessage());
        }
        missionService.remove(missionId);
        return ResponseEntity.status(HttpStatus.OK).body("Mission removed; ");
    }

}
