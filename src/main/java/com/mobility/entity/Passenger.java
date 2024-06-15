package com.mobility.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Passenger {
    @Id
    Long id;
    String name;
    String tel;
    Character gender;
    String cat;
    String note;
    Boolean pcm;
}
