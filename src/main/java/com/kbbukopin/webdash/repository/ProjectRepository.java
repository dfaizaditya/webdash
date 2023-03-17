package com.kbbukopin.webdash.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kbbukopin.webdash.entity.Project;;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(value="SELECT p.type FROM Project p", nativeQuery = true)
    List<String> getColumnTypeList();

    @Query(value="SELECT p.info1 FROM Project p", nativeQuery = true)
    List<String> getColumnCompleteList();

    @Query(value="SELECT p.unit FROM Project p", nativeQuery = true)
    List<String> getColumnUnitList();
}
