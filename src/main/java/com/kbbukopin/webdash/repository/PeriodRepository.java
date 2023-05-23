package com.kbbukopin.webdash.repository;

import com.kbbukopin.webdash.entity.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PeriodRepository extends JpaRepository<Period, Long>  {

    @Query(value = "SELECT EXISTS(SELECT FROM period p WHERE " +
            "p.year = :year)", nativeQuery = true)
    Boolean existsByYear(@Param("year") Long year);

    @Query(value = "SELECT p FROM Period p WHERE " +
            "p.year = :year")
    Period getPeriodByYear(@Param("year") Long year);

}
