package com.mobility.repository;

import com.mobility.entity.Demand;
import com.mobility.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface DemandRepository extends JpaRepository<Demand, Long> {

    @Query(value = "select distinct date_plan from demand",
            nativeQuery = true)
    List<Date> findDistributeDates();

    List<Demand> findAllByDatePlan(LocalDate datePlan);

}
