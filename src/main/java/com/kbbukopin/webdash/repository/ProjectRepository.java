package com.kbbukopin.webdash.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kbbukopin.webdash.entity.Project;;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(value = "SELECT p.type FROM Project p", nativeQuery = true)
    List<String> getColumnTypeList();

    @Query(value = "SELECT p.info1 FROM Project p", nativeQuery = true)
    List<String> getColumnCompleteList();

    @Query(value = "SELECT p.unit FROM Project p", nativeQuery = true)
    List<String> getColumnUnitList();

    @Query("SELECT p FROM Project p WHERE " +
            "(:name is null or lower(p.name) like %:name%) and " +
            "(:unit is null or lower(p.unit) like %:unit%) and " +
            "(:category is null or lower(p.category) like %:category%)")
    List<Project> searchProjects(@Param("name") String name,
            @Param("unit") String unit,
            @Param("category") String category);

}
