package com.kbbukopin.webdash.repository;

import com.kbbukopin.webdash.entity.UserSponsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserSponsorRepository extends JpaRepository<UserSponsor, Long> {

    @Query(value = "SELECT EXISTS(SELECT FROM user_sponsor us " +
            "WHERE us.name = :name)", nativeQuery = true)
    Boolean existsByName(@Param("name") String name);


    @Query(value = "SELECT us.id as id, us.name as name, us.created_at as created_at, us.updated_at as updated_at FROM user_sponsor us " +
            "WHERE us.name = :name", nativeQuery = true)
    UserSponsor getByName(@Param("name") String name);

    @Modifying
    @Query(value = "DELETE FROM user_sponsor us " +
            "WHERE us.id NOT IN (SELECT pus.user_sponsor_id FROM project_user_sponsor pus)", nativeQuery = true)
    void deleteUserSponsorNotExistOnPivot();

}
