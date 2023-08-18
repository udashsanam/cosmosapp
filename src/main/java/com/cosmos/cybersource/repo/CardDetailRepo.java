package com.cosmos.cybersource.repo;

import com.cosmos.cybersource.entity.CardDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardDetailRepo extends JpaRepository<CardDetailEntity, Long> {

    List<CardDetailEntity> findAllByDeviceIdOrderByCreatedDateDesc(String deviceId);
}
