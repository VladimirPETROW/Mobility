package com.mobility.repository;

import com.mobility.entity.Demand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DemandRepository extends JpaRepository<Demand, Long> {

    @Query(value = "select distinct date_plan" +
            " from demand as d left join work as w" +
            " on d.id = w.dem_id" +
            " where w.dem_id is null" +
            " order by date_plan",
            nativeQuery = true)
    List<Date> findDistributeDates();

    List<Demand> findAllByDatePlan(LocalDate date_plan);

    List<Demand> findAllByDatePlanAndStatusIn(LocalDate date_plan, String[] status);
}
