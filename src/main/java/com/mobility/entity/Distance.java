package com.mobility.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Distance {
    @Id
    Integer id;
    @ManyToOne
    @JoinColumn(name = "st_first_id")
    Station stFirst;
    @ManyToOne
    @JoinColumn(name = "st_second_id")
    Station stSecond;
    @Column(name = "pas_min")
    Integer pasMin;
    @Column(name = "emp_min")
    Integer empMin;
}
