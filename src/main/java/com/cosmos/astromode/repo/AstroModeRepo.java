package com.cosmos.astromode.repo;

import com.cosmos.astromode.enitity.AstroModeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AstroModeRepo extends JpaRepository<AstroModeEntity, Long> {
    AstroModeEntity findByEmail(String emaail);
    AstroModeEntity findByUserId(Long userId);
}
