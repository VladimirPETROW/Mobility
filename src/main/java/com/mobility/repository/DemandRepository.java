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

    @Query(value = "select distinct date_plan from demand order by date_plan",
             nativeQuery = true)
    List<Date> findDistributeDates();

    @Query(value = "select distinct status from demand order by status",
            nativeQuery = true)
    List<String> findStatuses();

    List<Demand> findAllByOrderById();

    Demand findById(long id);

    List<Demand> findAllByDatePlan(LocalDate datePlan);

}
