package com.kbbukopin.webdash.repository;

import com.kbbukopin.webdash.entity.Pic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PicRepository extends JpaRepository<Pic, Long> {

    @Query(value = "SELECT EXISTS(SELECT FROM pic p " +
            "WHERE p.name = :name)", nativeQuery = true)
    Boolean existsByName(@Param("name") String name);

    @Query(value = "SELECT p.id as id, p.name as name, p.created_at as created_at, p.updated_at as updated_at FROM pic p " +
            "WHERE p.name = :name", nativeQuery = true)
    Pic getByName(@Param("name") String name);

    @Modifying
    @Query(value = "DELETE FROM pic p " +
            "WHERE p.id NOT IN (" +
                "SELECT pp.pic_id FROM project_pic pp " +
                "UNION " +
                "SELECT npp.pic_id FROM non_project_pic npp" +
            ")", nativeQuery = true)
    void deletePicNotExistOnPivot();
}
