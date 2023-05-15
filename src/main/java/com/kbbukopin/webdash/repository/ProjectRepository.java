package com.kbbukopin.webdash.repository;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.map.LinkedMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kbbukopin.webdash.entity.Project;;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(value = "SELECT p FROM Project p WHERE " +
                    "p.id = :id AND p.month = :month")
    Optional<Project> getProjectByIdAndMonth(@Param("id") Long id,
                                    @Param("month") String month);

    @Query(value = "SELECT EXISTS(SELECT FROM project p " +
            "WHERE p.id = :id AND p.month = :month)", nativeQuery = true)
    Boolean existsByIdAndMonth(@Param("id") Long id,
                       @Param("month") String month);

    @Modifying
    @Query(value = "DELETE FROM Project p WHERE " +
            "p.id = :id AND p.month = :month")
    void deleteByIdAndMonth(@Param("id") Long id,
                            @Param("month") String month);

    @Query(value = "SELECT p.type FROM Project p WHERE " +
            "p.month = :month", nativeQuery = true)
    List<String> getColumnTypeList(@Param("month") String month);

    @Query(value = "SELECT p.info1 FROM Project p WHERE " +
            "p.month = :month", nativeQuery = true)
    List<String> getColumnCompleteList(@Param("month") String month);

    @Query(value = "SELECT p.unit FROM Project p WHERE " +
            "p.month = :month", nativeQuery = true)
    List<String> getColumnUnitList(@Param("month") String month);

    @Query("SELECT p FROM Project p WHERE " +
            "(:month is null or p.month like %:month%) and " +
            "(:name is null or lower(p.name) like %:name%) and " +
            "(:unit is null or lower(p.unit) like %:unit%) and " +
            "(:category is null or lower(p.category) like %:category%)")
    List<Project> searchProjects(@Param("month") String month,
            @Param("name") String name,
            @Param("unit") String unit,
            @Param("category") String category);

    @Query(value="SELECT " +
        "COALESCE(SUM(CASE WHEN LOWER(status) NOT LIKE 'rollout/solved' THEN 1 ELSE 0 END), 0) AS \"Not Done\", " +
        "COALESCE(SUM(CASE WHEN LOWER(status) LIKE 'rollout/solved' AND lower(info1) LIKE 'finished%overdue%' THEN 1 ELSE 0 END), 0) AS \"Overdue\", " +
        "COALESCE(SUM(CASE WHEN LOWER(status) LIKE 'rollout/solved' AND lower(info1) LIKE 'finished%on time%' THEN 1 ELSE 0 END), 0) AS \"On Time\", " +
        "COALESCE(SUM(CASE WHEN LOWER(status) LIKE 'rollout/solved' AND lower(info1) LIKE 'finished%ahead%' THEN 1 ELSE 0 END), 0) AS \"Ahead\", " +
        "COALESCE(SUM(CASE WHEN LOWER(status) NOT LIKE 'rollout/solved' OR (LOWER(status) LIKE 'rollout/solved' AND (LOWER(info1) LIKE 'finished%overdue%') OR LOWER(info1) LIKE LOWER('Finished%On Time%') OR LOWER(info1) LIKE LOWER('Finished%Ahead%')) THEN 1 ELSE 0 END), 0) as \"Total\" " +
        "FROM project WHERE type LIKE '%'||CASE WHEN :type = 'Insiden' THEN '%' ELSE :type END||'%' AND category = :category AND month = :month", nativeQuery = true)
    LinkedMap<String,String> getCountProject(@Param("category") String category,
                 @Param("type") String type,
                 @Param("month") String month);

    @Query(value="SELECT a.total_project as \"Total Project\", a.total_selesai as \"Total Selesai\", a.selesai_cepat as \"Selesai Cepat\", a.selesai_overdue as \"Selesai Overdue\", CAST(SUM(CASE WHEN a.total_project = 0 OR a.total_selesai = 0 THEN 0 ELSE ((CAST(a.total_selesai AS FLOAT)/a.total_project)+(CAST(a.selesai_cepat AS FLOAT)/a.total_selesai)-(CAST(a.selesai_overdue AS FLOAT)/a.total_selesai))*100 END) AS NUMERIC(4,0)) as \"KPI\" " +
            "FROM (SELECT " +
            "COALESCE(SUM(CASE WHEN LOWER(status) NOT LIKE 'rollout/solved' OR (LOWER(status) LIKE 'rollout/solved' AND (LOWER(info1) LIKE 'finished%overdue%' OR LOWER(info1) LIKE 'finished%on time%' OR LOWER(info1) LIKE 'finished%ahead%')) THEN 1 ELSE 0 END), 0) as total_project, " +
            "COALESCE(SUM(CASE WHEN LOWER(status) LIKE 'rollout/solved' AND (LOWER(info1) LIKE 'finished%overdue%' OR LOWER(info1) LIKE 'finished%on time%' OR LOWER(info1) LIKE 'finished%ahead%') THEN 1 ELSE 0 END), 0) as total_selesai, " +
            "COALESCE(SUM(CASE WHEN LOWER(status) LIKE 'rollout/solved' AND lower(info1) LIKE 'finished%ahead%' THEN 1 ELSE 0 END), 0) AS selesai_cepat, " +
            "COALESCE(SUM(CASE WHEN LOWER(status) LIKE 'rollout/solved' AND lower(info1) LIKE 'finished%overdue%' THEN 1 ELSE 0 END), 0) AS selesai_overdue " +
            "FROM project " +
            "WHERE month = :month) as a " +
            "GROUP BY a.total_project, a.total_selesai, a.selesai_cepat, a.selesai_overdue", nativeQuery = true)
    LinkedMap<String, String> getTotalProject(@Param("month") String month);

}
