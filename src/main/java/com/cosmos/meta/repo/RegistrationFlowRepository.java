package com.cosmos.meta.repo;

import com.cosmos.meta.model.RegistrationFlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationFlowRepository extends JpaRepository<RegistrationFlowEntity, Long> {

    RegistrationFlowEntity findByIsCurrentStateAndSenderId(boolean isCurrentState,String senderId);

    List<RegistrationFlowEntity> findAllByLatestFlowAndSenderId(boolean isLatestFlow, String senderId);

    @Query(value = "update tbl_registration_flow set is_current_state = false, latest_flow = false\n" +
            "where sender_id = :senderId and latest_flow = true", nativeQuery = true)
    void deleteOldRegistrationFlows(@Param("senderId") String senderId);
}
