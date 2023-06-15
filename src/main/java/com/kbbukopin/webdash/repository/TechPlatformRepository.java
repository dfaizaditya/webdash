package com.kbbukopin.webdash.repository;

import com.kbbukopin.webdash.entity.TechPlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TechPlatformRepository extends JpaRepository<TechPlatform, Long> {

    @Query(value = "SELECT EXISTS(SELECT FROM tech_platform tp " +
            "WHERE tp.name = :name)", nativeQuery = true)
    Boolean existsByName(@Param("name") String name);

    @Query(value = "SELECT tp.id as id, tp.name as name, tp.created_at as created_at, tp.updated_at as updated_at FROM tech_platform tp " +
            "WHERE tp.name = :name", nativeQuery = true)
    TechPlatform getByName(@Param("name") String name);

    @Query(value = "SELECT EXISTS(SELECT FROM project_tech_platform ptp " +
            "WHERE ptp.tech_platform_id = :tech_platform_id " +
            "AND((ptp.project_id != :project_id) OR (ptp.project_id = :project_id AND ptp.project_month != :project_month)))", nativeQuery = true)
    Boolean existProjectsByTechPlatformIdExceptItself(@Param("project_id") Long project_id,
                                                     @Param("project_month") String project_month,
                                                     @Param("tech_platform_id") Long tech_platform_id);

    @Modifying
    @Query(value = "DELETE FROM tech_platform tp WHERE id NOT IN " +
            "(SELECT ptp.tech_platform_id FROM project_tech_platform ptp)", nativeQuery = true)
    void deleteTechPlatformNotExistOnPivot();
}
