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

    @Query(value = "SELECT EXISTS(SELECT FROM project_pic pp " +
            "WHERE pp.pic_id = :pic_id " +
            "AND((pp.project_id != :project_id) OR (pp.project_id = :project_id AND pp.project_month != :project_month)))", nativeQuery = true)
    Boolean existProjectsByPicIdExceptItself(@Param("project_id") Long project_id,
                                             @Param("project_month") String project_month,
                                             @Param("pic_id") Long pic_id);

    @Modifying
    @Query(value = "DELETE FROM pic p WHERE id NOT IN " +
            "(SELECT pp.pic_id FROM project_pic pp)", nativeQuery = true)
    void deletePicNotExistOnPivot();
}
