package com.kbbukopin.webdash.repository;

import com.kbbukopin.webdash.entity.AppPlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppPlatformRepository extends JpaRepository<AppPlatform, Long> {

    @Query(value = "SELECT EXISTS(SELECT FROM app_platform ap " +
            "WHERE ap.name = :name)", nativeQuery = true)
    Boolean existsByName(@Param("name") String name);

    @Query(value = "SELECT ap.id as id, ap.name as name, ap.created_at as created_at, ap.updated_at as updated_at FROM app_platform ap " +
            "WHERE ap.name = :name", nativeQuery = true)
    AppPlatform getByName(@Param("name") String name);

    @Modifying
    @Query(value = "DELETE FROM app_platform ap " +
            "WHERE ap.id NOT IN (SELECT pap.app_platform_id FROM project_app_platform pap)", nativeQuery = true)
    void deleteAppPlatformNotExistOnPivot();
}
