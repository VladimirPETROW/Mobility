package com.mobility.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Employee {
    @Id
    Long id;
    @OneToOne
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = true)
    User user;
    @Column(name = "name_full")
    String nameFull;
    @Column(name = "name_short")
    String nameShort;
    Character gender;
    String area;
    String shift;
    String rank;
    @Column(name = "time_begin")
    LocalTime timeBegin;
    @Column(name = "time_end")
    LocalTime timeEnd;
    @Column(name = "tel_work")
    String telWork;
    @Column(name = "tel_pers")
    String telPers;
    String num;
    Boolean easy;
    @ManyToMany(mappedBy = "emp")
    List<Demand> demand;
}
