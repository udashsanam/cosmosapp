package com.cosmos.user.repo;

import com.cosmos.user.entity.PackageSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PackageSubscriptionRepo extends JpaRepository<PackageSubscription, Long> {

    @Query(value = "select * from tbl_package_subscription where fk_user_id=:x and subs_status=1 and expire_at >= CURRENT_TIMESTAMP order by created_at asc", nativeQuery = true)
    List<PackageSubscription> findUnExpirePackageByUserId(@Param("x") Long userId);

    @Query(value = "select * from tbl_package_subscription where fk_user_id=:x and subs_status=1 and expire_at >= CURRENT_TIMESTAMP order by created_at asc limit 0,1 ", nativeQuery = true)
    PackageSubscription findOldestPackageByUserId(@Param("x") Long userId);
}
