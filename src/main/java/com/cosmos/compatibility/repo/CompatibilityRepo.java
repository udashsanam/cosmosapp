package com.cosmos.compatibility.repo;

import com.cosmos.compatibility.entity.Compatibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompatibilityRepo extends JpaRepository<Compatibility, Long> {
    List<Compatibility> findAllByDeviceIdOrderByCreatedAtDesc(String deviceId);
}
