package com.kbbukopin.webdash.repository;

import com.kbbukopin.webdash.entity.NonProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NonProjectRepository extends JpaRepository<NonProject, Long> {

    @Query(value = "SELECT np.* FROM non_project np WHERE " +
            "(:period_id IS NULL OR np.period_id = :period_id) AND " +
            "(:month IS NULL OR np.month LIKE '%'||:month||'%') AND " +
            "(:name IS NULL OR LOWER(np.name) LIKE LOWER('%'||:name||'%')) AND " +
            "(:unit IS NULL OR LOWER(np.unit) LIKE LOWER('%'||:unit||'%')) " +
            "ORDER BY np.id DESC", nativeQuery = true)
    List<NonProject> searchNonProjects(
            @Param("period_id") Long period_id,
            @Param("month") String month,
            @Param("name") String name,
            @Param("unit") String unit);

    @Query(value = "SELECT np FROM NonProject np WHERE " +
            "np.id = :id")
    Optional<NonProject> getNonProjectByPrimaryKey(@Param("id") Long id);

    @Query(value = "SELECT EXISTS(SELECT FROM non_project np " +
            "WHERE np.unit = :unit AND " +
            "np.name = :name AND " +
            "np.month = :month AND " +
            "np.period_id = :period_id)", nativeQuery = true)
    Boolean existsByData(@Param("unit") String unit,
                         @Param("name") String name,
                         @Param("month") String month,
                         @Param("period_id") Long period_id);

    @Query(value = "SELECT EXISTS(SELECT FROM non_project np " +
            "WHERE np.id != :id AND " +
            "np.unit = :unit AND " +
            "np.name = :name AND " +
            "np.month = :month AND " +
            "np.period_id = :period_id)", nativeQuery = true)
    Boolean existsByDataWithoutItself(@Param("id") Long id,
                                      @Param("unit") String unit,
                                      @Param("name") String name,
                                      @Param("month") String month,
                                      @Param("period_id") Long period_id);

    @Modifying
    @Query(value = "DELETE FROM NonProject np WHERE " +
            "np.id IN :ids")
    void deleteNonProjectEntries(@Param("ids") Iterable<Long> ids);

    @Query(value = "SELECT np.* " +
            "FROM (SELECT DISTINCT ON (np1.unit, np1.name) np1.* " +
                "FROM non_project np1 WHERE " +
                "np1.period_id = 1 AND " +
                "np1.month IN (:rangeMonth) " +
                "ORDER BY np1.unit, np1.name, CASE LOWER(np1.month) " +
                    "WHEN 'januari' THEN 1 " +
                    "WHEN 'februari' THEN 2 " +
                    "WHEN 'maret' THEN 3 " +
                    "WHEN 'april' THEN 4 " +
                    "WHEN 'mei' THEN 5 " +
                    "WHEN 'juni' THEN 6 " +
                    "WHEN 'juli' THEN 7 " +
                    "WHEN 'agustus' THEN 8 " +
                    "WHEN 'september' THEN 9 " +
                    "WHEN 'oktober' THEN 10 " +
                    "WHEN 'november' THEN 11 " +
                    "WHEN 'desember' THEN 12 " +
                    "END DESC" +
                ") AS np " +
            "WHERE np.period_id = :period_id " +
            "ORDER BY np.id DESC", nativeQuery = true)
    List<NonProject> exportNonProjects(@Param("period_id") Long period_id,
                                 @Param("rangeMonth") Iterable<String> rangeMonth);

}
