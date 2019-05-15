package com.example.spaceagencydatahub.domain.mission;


import com.example.spaceagencydatahub.domain.mission.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MissionRepository  extends JpaRepository<Mission, Long> {
    Optional<Mission> findByName(String name);
}
