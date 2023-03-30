package com.cosmos.login.repo;

import com.cosmos.login.entity.AppUser;
import com.cosmos.videochat.dto.AppUserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepo extends JpaRepository<AppUser, Long> {
    @Query("select a from AppUser a where a.email = ?1")
    AppUser findByEmail(String email);
    AppUser findByUserId(Long id);
//    @Query(value = "DELETE FROM tbl_users WHERE user_id = ?1", nativeQuery = true)
//    @Modifying
//    @Transactional
//	void delete(Long id);

    @Query(value = "select tb.user_type from tbl_users tb where tb.user_id = ?1", nativeQuery = true)
    String findUserType(Long id);


    @Query("select new com.cosmos.videochat.dto.AppUserDto(a.userId, concat(a.firstName, a.lastName) ) from AppUser a where a.userId = ?1")
    AppUserDto findAppUserDtoById(Long userId);
}
