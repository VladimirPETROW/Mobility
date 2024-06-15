package com.mobility.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    String username;
    @Column(nullable = false)
    String password;
    @Column(nullable = false)
    Boolean enabled;
    @ElementCollection
    @CollectionTable(name = "authorities", joinColumns = @JoinColumn(name = "username", nullable = false))
    @Column(name = "authority", nullable = false)
    List<String> authority;
}
