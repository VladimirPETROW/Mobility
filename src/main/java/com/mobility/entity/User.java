package com.mobility.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    String username;
    @Column(nullable = false)
    String password;
    @Column(nullable = false)
    Boolean enabled;
}
