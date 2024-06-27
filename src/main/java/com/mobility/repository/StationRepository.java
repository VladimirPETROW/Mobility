package com.mobility.repository;

import com.mobility.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    @Query(value = "select * from station order by name, line_id",
            nativeQuery = true)
    List<Station> findAll();

    Station findById(long id);

}
