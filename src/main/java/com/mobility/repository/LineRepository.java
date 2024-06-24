package com.mobility.repository;

import com.mobility.entity.Employee;
import com.mobility.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineRepository extends JpaRepository<Line, Long> {

}
