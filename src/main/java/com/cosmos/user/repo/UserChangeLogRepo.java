package com.cosmos.user.repo;

import com.cosmos.user.entity.UserChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserChangeLogRepo extends JpaRepository<UserChangeLog, Long> {
    List<UserChangeLog> findAllByUserUserIdOrderByIdDesc(Long userId);
}
