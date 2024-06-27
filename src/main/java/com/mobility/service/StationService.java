package com.mobility.service;

import com.mobility.entity.Station;
import com.mobility.repository.StationRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log
@Service
public class StationService {

    @Autowired
    StationRepository stationRepository;

    public List<Station> findAll() {
        List<Station> stations = stationRepository.findAll();
        return stations;
    }

    public Station findById(long id) {
        Station station = stationRepository.findById(id);
        return station;
    }

}
