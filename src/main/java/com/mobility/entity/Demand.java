package com.mobility.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Demand {
    @Id
    Long id;
    @ManyToOne
    @JoinColumn(name = "pas_id")
    Passenger pas;
    @Column(name = "date_plan")
    LocalDate datePlan;
    @Column(name = "time_plan")
    LocalTime timePlan;
    @Column(name = "time_begin")
    LocalTime timeBegin;
    @Column(name = "time_end")
    LocalTime timeEnd;
    String cat;
    String status;
    @Column(name = "emp_m")
    Integer empM;
    @Column(name = "emp_f")
    Integer empF;
    LocalTime duration;
    @ManyToOne
    @JoinColumn(name = "st_begin_id")
    Station stBegin;
    @ManyToOne
    @JoinColumn(name = "st_end_id")
    Station stEnd;
    @Column(nullable = true)
    Long track;
    @Column(name = "count_pas")
    Integer countPas;
    @Column(name = "note_begin")
    String noteBegin;
    @Column(name = "note_end")
    String noteEnd;
    String cargo;
    String note;
    String src;
    @Column(name = "date_src")
    LocalDateTime dateSrc;
    @ManyToMany
    @JoinTable(name = "work", joinColumns = {@JoinColumn(name = "dem_id")}, inverseJoinColumns = {@JoinColumn(name = "emp_id")})
    List<Employee> emp;

    public Set<String> getAreas() {
        Set<String> areas = new HashSet<>(emp.size());
        for (Employee employee : emp) {
            areas.add(employee.area);
        }
        return areas;
    }
}
