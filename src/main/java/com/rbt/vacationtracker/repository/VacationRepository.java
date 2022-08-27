package com.rbt.vacationtracker.repository;

import com.rbt.vacationtracker.entity.VacationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VacationRepository extends JpaRepository<VacationEntity, Long> {

    @Query("SELECT v FROM VacationEntity v WHERE v.year=:year AND v.days=:days")
    VacationEntity findVacationByYearAndDays(@Param("year") Integer year, @Param("days") Integer days);
}
