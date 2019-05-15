package com.example.spaceagencydatahub.domain.mission;

import com.example.spaceagencydatahub.domain.mission.model.Mission;
import com.example.spaceagencydatahub.domain.mission.model.MissionPayload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MissionService {

    private MissionRepository missionRepository;

    public MissionService(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }

    public Mission add(MissionPayload missionPayload) {
        return missionRepository.save(new Mission(missionPayload));
    }

    public Mission edit(Mission mission) {
        return missionRepository.save(mission);
    }

    public void remove(Long id) {
        missionRepository.deleteById(id);
    }

    public Optional<Mission> findByName(String name) {
        return missionRepository.findByName(name);
    }

    public Optional<Mission> findById(Long id) {
        return missionRepository.findById(id);
    }

    public List<Mission> findAll() {
        return missionRepository.findAll();
    }
}
