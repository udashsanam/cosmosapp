package com.cosmos.meta.repo;

import com.cosmos.meta.model.RegistrationFlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationFlowRepository extends JpaRepository<RegistrationFlowEntity, Long> {

    RegistrationFlowEntity findByIsCurrentStateAndSenderId(boolean isCurrentState,String senderId);

    List<RegistrationFlowEntity> findAllByLatestFlowAndSenderId(boolean isLatestFlow, String senderId);
}
