package com.kbbukopin.webdash.repository;

import com.kbbukopin.webdash.entity.DayOff;
import com.kbbukopin.webdash.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DayOffRepository extends JpaRepository<DayOff, Long> {

    @Query(value = "SELECT * FROM day_off " +
            "ORDER BY date DESC, updated_at DESC, created_at DESC", nativeQuery = true)
    List<DayOff> getAllDayOff();

    @Query(value = "SELECT do FROM DayOff do WHERE do.date = :date")
    Optional<DayOff> getDayOffByPrimaryKey(@Param("date") LocalDate date);

    @Query(value = "SELECT EXISTS(SELECT FROM day_off " +
            "WHERE day_off.date = :date)", nativeQuery = true)
    Boolean existsByPrimaryKey(@Param("date") LocalDate date);

    @Query(value = "SELECT * FROM day_off WHERE " +
            "EXTRACT(YEAR FROM day_off.date) = :year " +
            "ORDER BY day_off.date ASC", nativeQuery = true)
    List<DayOff> getDayOffByYear(@Param("year") Long year);

    @Modifying
    @Query(value = "DELETE FROM DayOff do WHERE " +
            "do.date IN :dates")
    void deleteDayOffEntries(@Param("dates") Iterable<LocalDate> dates);
}
