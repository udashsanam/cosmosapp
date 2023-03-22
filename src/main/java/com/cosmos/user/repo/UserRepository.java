package com.cosmos.user.repo;

import com.cosmos.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findByUserId(Long id);

    User findByDeviceId(String deviceId);

    @Query(value = "select * from tbl_users where user_type=:type", nativeQuery = true)
    List<User> getAllUserByUserType(@Param("type") String userType);

    @Query(value = "select * from tbl_users where user_id=:id", nativeQuery = true)
    User getUserbyId(@Param("id") Long id);

    @Query(value = "select * from tbl_users where device_id=:deviceId", nativeQuery = true)
    User getUserbyDeviceId(@Param("deviceId") String deviceId);

    List<User> findAllByDateOfBirthAndBirthTime(String dateOfBirth, String birthTime);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User u SET u.profileImageUrl = ?2 WHERE u.userId = ?1")
    void updateProfileImage(Long userId, String imgUrl);

    @Query(value = "SELECT " +
            "us.user_id as userId,us.first_name as firstName, us.last_name as lastName, us.country, us.gender, us.img_url as image " +
            "FROM tbl_users us " +
            "INNER JOIN tbl_ritual_enquiry eq ON us.user_id = eq.fk_end_user_id GROUP BY us.user_id;", nativeQuery = true)
    List<Map<String, Object>> findAllUserWithRitualInquiry();
}
