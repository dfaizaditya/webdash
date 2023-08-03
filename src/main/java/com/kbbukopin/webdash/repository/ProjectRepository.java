package com.kbbukopin.webdash.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.Tuple;

import org.apache.commons.collections4.map.LinkedMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kbbukopin.webdash.dto.TechPlatformDTO;
import com.kbbukopin.webdash.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(value = "SELECT p FROM Project p WHERE " +
            "p.id = :id AND " +
            "p.month = :month AND " +
            "p.unit = :unit AND " +
            "p.period.id = :period_id")
    Optional<Project> getProjectByPrimaryKey(@Param("id") Long id,
                                            @Param("month") String month,
                                            @Param("unit") String unit,
                                            @Param("period_id") Long period_id);

    @Query(value = "SELECT EXISTS(SELECT FROM project p " +
            "WHERE p.id = :id AND " +
            "p.month = :month AND " +
            "p.unit = :unit AND " +
            "p.period_id = :period_id)", nativeQuery = true)
    Boolean existsByPrimaryKey(@Param("id") Long id,
                               @Param("month") String month,
                               @Param("unit") String unit,
                               @Param("period_id") Long period_id);

    @Modifying
    @Query(value = "DELETE FROM Project p WHERE " +
            "p.id = :id AND " +
            "p.month = :month AND " +
            "p.unit = :unit AND " +
            "p.period.id = :period_id")
    void deleteByPrimaryKey(@Param("id") Long id,
                            @Param("month") String month,
                            @Param("unit") String unit,
                            @Param("period_id") Long period_id);

    @Modifying
    @Query(value = "DELETE FROM Project p WHERE " +
            "p.id IN :ids AND " +
            "p.month IN :months AND " +
            "p.unit IN :units AND " +
            "p.period.id IN :projectPeriodIds")
    void deleteProjectEntries(@Param("ids") Iterable<Long> ids,
                              @Param("months") Iterable<String> months,
                              @Param("units") Iterable<String> units,
                              @Param("projectPeriodIds") Iterable<Long> projectPeriodIds);

    @Query(value = "SELECT p.type FROM Project p WHERE " +
                    "p.period_id = :period_id AND " +
                    "(:month is null or p.month like '%'||:month||'%')", nativeQuery = true)
    List<String> getColumnTypeList(@Param("period_id") Long period_id,
                    @Param("month") String month);

    @Query(value = "SELECT p.info1 FROM Project p WHERE " +
                    "p.period_id = :period_id AND " +
                    "(:month is null or p.month like '%'||:month||'%')", nativeQuery = true)
    List<String> getColumnCompleteList(@Param("period_id") Long period_id,
                    @Param("month") String month);

    @Query(value = "SELECT p.unit FROM Project p WHERE " +
                    "p.period_id = :period_id AND " +
                    "(:month is null or p.month like '%'||:month||'%')", nativeQuery = true)
    List<String> getColumnUnitList(@Param("period_id") Long period_id,
                    @Param("month") String month);

    @Query(value = "SELECT p.category FROM Project p WHERE " +
                    "p.period_id = :period_id AND " +
                    "(:month is null or p.month like '%'||:month||'%')", nativeQuery = true)
    List<String> getColumnCategoryList(@Param("period_id") Long period_id,
                    @Param("month") String month);


                    @Query(value = "SELECT p.documentation FROM Project p WHERE " +
                    "p.period_id = :period_id AND " +
                    "(:month is null or p.month like '%'||:month||'%')", nativeQuery = true)
    List<String> getColumnDocumentationList(@Param("period_id") Long period_id,
                    @Param("month") String month);

    @Query(value = "SELECT p.type, COUNT(*) AS count " +
                    "FROM Project p " +
                    "WHERE p.period_id = :period_id AND " +
                    "(:month is null or p.month like '%'||:month||'%') AND " +
                    "p.status = 'Rollout/Solved' " +
                    "GROUP BY p.type", nativeQuery = true)
    List<Object[]> getRolloutStatusCounts(@Param("period_id") Long period_id,
                    @Param("month") String month);

    @Query(value = "SELECT p.unit, COUNT(*) AS count " +
                    "FROM Project p " +
                    "WHERE p.period_id = :period_id AND " +
                    "(:month is null or p.month like '%'||:month||'%') AND " +
                    "p.status = 'Rollout/Solved' " +
                    "GROUP BY p.unit", nativeQuery = true)
    List<Object[]> getRolloutUnitCounts(@Param("period_id") Long period_id,
                    @Param("month") String month);

    @Query(value = "SELECT ap.name AS name, COUNT(ptp.project_id) AS project_count FROM app_platform ap " +
                    "JOIN project_app_platform ptp ON ap.id = ptp.app_platform_id " +
                    "AND ptp.project_period_id = :period_id " +
                    "AND (:month is null or ptp.project_month like '%'||:month||'%') " +
                    "GROUP BY ap.name ORDER BY project_count", nativeQuery = true)
    List<Object[]> getColumnAppPlatformList(@Param("period_id") Long period_id, @Param("month") String month);

    @Query(value = "SELECT t.name AS name, COUNT(ptp.project_id) AS project_count FROM tech_platform t " +
                    "JOIN project_tech_platform ptp ON t.id = ptp.tech_platform_id " +
                    "AND ptp.project_period_id = :period_id " +
                    "AND (:month is null or ptp.project_month like '%'||:month||'%') " +
                    "GROUP BY t.name ORDER BY project_count", nativeQuery = true)
    List<Object[]> getColumnTechPlatformList(@Param("period_id") Long period_id, @Param("month") String month);

    @Query(value = "SELECT p.* FROM Project p WHERE " +
            "(:period_id IS NULL OR p.period_id = :period_id) AND " +
            "(:month IS NULL OR p.month LIKE '%'||:month||'%') AND " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER('%'||:name||'%')) AND " +
            "(:info1 IS NULL OR LOWER(p.info1) LIKE LOWER(:info1)) AND " +
            "(:unit IS NULL OR LOWER(p.unit) LIKE LOWER(:unit)) AND " +
            "(:type IS NULL OR LOWER(p.type) LIKE LOWER(:type)) AND " +
            "(:category IS NULL OR LOWER(p.category) LIKE LOWER('%'||:category||'%')) " +
            "ORDER BY p.updated_at DESC, p.created_at DESC", nativeQuery = true)
    List<Project> searchProjects(
            @Param("period_id") Long period_id,
            @Param("month") String month,
            @Param("name") String name,
            @Param("info1") String info1,
            @Param("unit") String unit,
            @Param("type") String type,
            @Param("category") String category);

    @Query(value = "SELECT p.* " +
            "FROM (SELECT DISTINCT ON (p1.id, p1.unit, p1.info1) p1.* " +
                "FROM project p1 WHERE " +
                "p1.period_id = 1 AND " +
                "p1.month IN (:rangeMonth) " +
                "ORDER BY p1.id, p1.unit, p1.info1, CASE LOWER(p1.month) " +
                    "WHEN 'Januari' THEN 1 " +
                    "WHEN 'Februari' THEN 2 " +
                    "WHEN 'Maret' THEN 3 " +
                    "WHEN 'April' THEN 4 " +
                    "WHEN 'Mei' THEN 5 " +
                    "WHEN 'Juni' THEN 6 " +
                    "WHEN 'Juli' THEN 7 " +
                    "WHEN 'Agustus' THEN 8 " +
                    "WHEN 'September' THEN 9 " +
                    "WHEN 'Oktober' THEN 10 " +
                    "WHEN 'November' THEN 11 " +
                    "WHEN 'Desember' THEN 12 " +
                    "END DESC" +
            ") AS p " +
            "WHERE period_id = :period_id AND " +
            "LOWER(info1) LIKE LOWER('%'||'Finished%'||:typeOfFinished||'%') AND " +
            "LOWER(p.category) LIKE ANY (ARRAY['%proyek%', '%insiden%']) AND " +
            "LOWER(p.type) LIKE ANY (ARRAY['%in house%', 'join dev']) " +
            "ORDER BY p.updated_at DESC, p.created_at DESC", nativeQuery = true)
    List<Project> getFinishedProject(
            @Param("period_id") Long period_id,
            @Param("rangeMonth") Iterable<String> rangeMonth,
            @Param("typeOfFinished") String typeOfFinished);


    @Query(value="SELECT " +
            "COALESCE(SUM(CASE WHEN LOWER(p.info1) LIKE 'finished%overdue%' THEN 1 ELSE 0 END), 0) AS \"Overdue\", " +
            "COALESCE(SUM(CASE WHEN LOWER(p.info1) LIKE 'finished%on time%' THEN 1 ELSE 0 END), 0) AS \"On Time\", " +
            "COALESCE(SUM(CASE WHEN LOWER(p.info1) LIKE 'finished%ahead%' THEN 1 ELSE 0 END), 0) AS \"Ahead\", " +
            "COALESCE(SUM(CASE WHEN LOWER(p.info1) LIKE 'finished%' AND LOWER(p.info1) NOT LIKE 'finished%N/A%' THEN 1 ELSE 0 END), 0) as \"Total\" " +
            "FROM (SELECT DISTINCT ON (p1.id, p1.unit, p1.info1) p1.* " +
                "FROM project p1 WHERE " +
                "p1.period_id = 1 AND " +
                "p1.month IN (:rangeMonth) " +
                "ORDER BY p1.id, p1.unit, p1.info1, CASE LOWER(p1.month) " +
                    "WHEN 'Januari' THEN 1 " +
                    "WHEN 'Februari' THEN 2 " +
                    "WHEN 'Maret' THEN 3 " +
                    "WHEN 'April' THEN 4 " +
                    "WHEN 'Mei' THEN 5 " +
                    "WHEN 'Juni' THEN 6 " +
                    "WHEN 'Juli' THEN 7 " +
                    "WHEN 'Agustus' THEN 8 " +
                    "WHEN 'September' THEN 9 " +
                    "WHEN 'Oktober' THEN 10 " +
                    "WHEN 'November' THEN 11 " +
                    "WHEN 'Desember' THEN 12 " +
                    "END DESC" +
                ") AS p " +
            "WHERE type LIKE '%'||CASE WHEN :type = 'Insiden' THEN '%' ELSE :type END||'%' AND " +
            "category = :category AND " +
            "period_id = :period_id AND " +
            "type NOT LIKE '%Outsource'", nativeQuery = true)
    LinkedMap<String,String> getCountProject(@Param("period_id") Long period_id,
                                             @Param("rangeMonth") Iterable<String> rangeMonth,
                                             @Param("category") String category,
                                             @Param("type") String type);

//    @Query(value="SELECT " +
//        "COALESCE(SUM(CASE WHEN LOWER(status) NOT LIKE 'rollout/solved' THEN 1 ELSE 0 END), 0) AS \"Not Done\", " +
//        "COALESCE(SUM(CASE WHEN LOWER(status) LIKE 'rollout/solved' AND lower(info1) LIKE 'finished%overdue%' THEN 1 ELSE 0 END), 0) AS \"Overdue\", " +
//        "COALESCE(SUM(CASE WHEN LOWER(status) LIKE 'rollout/solved' AND lower(info1) LIKE 'finished%on time%' THEN 1 ELSE 0 END), 0) AS \"On Time\", " +
//        "COALESCE(SUM(CASE WHEN LOWER(status) LIKE 'rollout/solved' AND lower(info1) LIKE 'finished%ahead%' THEN 1 ELSE 0 END), 0) AS \"Ahead\", " +
//        "COALESCE(SUM(CASE WHEN LOWER(status) NOT LIKE 'rollout/solved' OR (LOWER(status) LIKE 'rollout/solved' AND (LOWER(info1) LIKE 'finished%overdue%') OR LOWER(info1) LIKE LOWER('Finished%On Time%') OR LOWER(info1) LIKE LOWER('Finished%Ahead%')) THEN 1 ELSE 0 END), 0) as \"Total\" " +
//        "FROM project WHERE type LIKE '%'||CASE WHEN :type = 'Insiden' THEN '%' ELSE :type END||'%' AND category = :category AND month = :month AND period_id = :period_id", nativeQuery = true)
//    LinkedMap<String,String> getCountProject(@Param("period_id") Long period_id,
//                                             @Param("month") String month,
//                                             @Param("category") String category,
//                                             @Param("type") String type);

//    @Query(value="SELECT a.total_project as \"Total Project\", a.total_selesai as \"Total Selesai\", a.selesai_cepat as \"Selesai Cepat\", a.selesai_overdue as \"Selesai Overdue\", CAST(SUM(CASE WHEN a.total_project = 0 OR a.total_selesai = 0 THEN 0 ELSE (((CAST(a.total_selesai AS FLOAT)/a.total_project)*1)+((CAST(a.selesai_cepat AS FLOAT)/a.total_selesai)*:average_ahead)-((CAST(a.selesai_overdue AS FLOAT)/a.total_selesai)*:average_overdue))*100 END) AS NUMERIC(4,0)) as \"KPI\" " +
//            "FROM (SELECT " +
//            "COALESCE(SUM(CASE WHEN LOWER(status) NOT LIKE 'rollout/solved' OR (LOWER(status) LIKE 'rollout/solved' AND (LOWER(info1) LIKE 'finished%overdue%' OR LOWER(info1) LIKE 'finished%on time%' OR LOWER(info1) LIKE 'finished%ahead%')) THEN 1 ELSE 0 END), 0) as total_project, " +
//            "COALESCE(SUM(CASE WHEN LOWER(status) LIKE 'rollout/solved' AND (LOWER(info1) LIKE 'finished%overdue%' OR LOWER(info1) LIKE 'finished%on time%' OR LOWER(info1) LIKE 'finished%ahead%') THEN 1 ELSE 0 END), 0) as total_selesai, " +
//            "COALESCE(SUM(CASE WHEN LOWER(status) LIKE 'rollout/solved' AND lower(info1) LIKE 'finished%ahead%' THEN 1 ELSE 0 END), 0) AS selesai_cepat, " +
//            "COALESCE(SUM(CASE WHEN LOWER(status) LIKE 'rollout/solved' AND lower(info1) LIKE 'finished%overdue%' THEN 1 ELSE 0 END), 0) AS selesai_overdue " +
//            "FROM project " +
//            "WHERE month = :month AND period_id = :period_id AND LOWER(category) IN :categories) as a " +
//            "GROUP BY a.total_project, a.total_selesai, a.selesai_cepat, a.selesai_overdue", nativeQuery = true)
//    LinkedMap<String, String> getTotalProject(@Param("period_id") Long period_id,
//                                              @Param("month") String month,
//                                              @Param("categories") Iterable<String> categories,
//                                              @Param("average_ahead") Double average_ahead,
//                                              @Param("average_overdue") Double average_overdue);

//    @Query(value="SELECT a.total_project as \"Total Project\", a.total_ontime as \"Total Ontime\", a.selesai_cepat as \"Selesai Cepat\", a.selesai_overdue as \"Selesai Overdue\", CAST(SUM(CASE WHEN a.total_project = 0 THEN 0 ELSE (((CAST(a.total_ontime AS FLOAT)/a.total_project)*1)+((CAST(a.selesai_cepat AS FLOAT)/a.total_project)*:average_ahead)+((CAST(a.selesai_overdue AS FLOAT)/a.total_project)*:average_overdue))*100 END) AS NUMERIC(4,1)) as \"KPI\" " +
//            "FROM " +
//                "(SELECT " +
//                    "COALESCE(SUM(CASE WHEN LOWER(p.info1) LIKE 'finished%' AND LOWER(p.info1) NOT LIKE 'finished%N/A%' THEN 1 ELSE 0 END), 0) as total_project, " +
//                    "COALESCE(SUM(CASE WHEN LOWER(p.info1) LIKE 'finished%on time%' THEN 1 ELSE 0 END), 0) as total_ontime,  " +
//                    "COALESCE(SUM(CASE WHEN LOWER(p.info1) LIKE 'finished%ahead%' THEN 1 ELSE 0 END), 0) AS selesai_cepat, " +
//                    "COALESCE(SUM(CASE WHEN LOWER(p.info1) LIKE 'finished%overdue%' THEN 1 ELSE 0 END), 0) AS selesai_overdue " +
//                        "FROM (SELECT DISTINCT ON (p1.id) p1.* " +
//                            "FROM project p1 WHERE " +
//                            "p1.period_id = 1 AND " +
//                            "p1.month IN (:rangeMonth) " +
//                            "ORDER BY p1.id, CASE LOWER(p1.month) " +
//                                "WHEN 'Januari' THEN 1 " +
//                                "WHEN 'Februari' THEN 2 " +
//                                "WHEN 'Maret' THEN 3 " +
//                                "WHEN 'April' THEN 4 " +
//                                "WHEN 'Mei' THEN 5 " +
//                                "WHEN 'Juni' THEN 6 " +
//                                "WHEN 'Juli' THEN 7 " +
//                                "WHEN 'Agustus' THEN 8 " +
//                                "WHEN 'September' THEN 9 " +
//                                "WHEN 'Oktober' THEN 10 " +
//                                "WHEN 'November' THEN 11 " +
//                                "WHEN 'Desember' THEN 12 " +
//                                "END DESC" +
//                        ") AS p " +
//                    "WHERE p.period_id = :period_id AND " +
//                    "LOWER(p.category) IN :categories AND " +
//                    "LOWER(p.type) LIKE ANY (ARRAY['%in house%', 'join dev']) AND " +
//                    "LOWER(p.type) NOT LIKE '%outsource%'" +
//                ") as a " +
//            "GROUP BY a.total_project, a.total_ontime, a.selesai_cepat, a.selesai_overdue", nativeQuery = true)
//    LinkedMap<String, String> getTotalProject(@Param("period_id") Long period_id,
//                                              @Param("rangeMonth") Iterable<String> rangeMonth,
//                                              @Param("categories") Iterable<String> categories,
//                                              @Param("average_ahead") Double average_ahead,
//                                              @Param("average_overdue") Double average_overdue);

}