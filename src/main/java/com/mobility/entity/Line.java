package com.mobility.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Line {
    @Id
    Integer id;
    String name;
}
