package com.mobility.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Station {
    @Id
    Integer id;
    String name;
    @ManyToOne
    @JoinColumn(name = "line_id")
    Line line;
}
