package com.cosmos.login.repo;

import com.cosmos.login.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AppUserRepo extends JpaRepository<AppUser, Long> {
    AppUser findByEmail(String email);
    AppUser findByUserId(Long id);
//    @Query(value = "DELETE FROM tbl_users WHERE user_id = ?1", nativeQuery = true)
//    @Modifying
//    @Transactional
//	void delete(Long id);
}
