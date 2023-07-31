package com.kbbukopin.webdash.repository;

import com.kbbukopin.webdash.entity.DayOff;
import com.kbbukopin.webdash.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DayOffRepository extends JpaRepository<DayOff, Long> {
    @Query(value = "SELECT * FROM day_off WHERE " +
            "EXTRACT(YEAR FROM day_off.date) = :year " +
            "ORDER BY day_off.date ASC", nativeQuery = true)
    List<DayOff> getDayOffByYear(@Param("year") Long year);
}
