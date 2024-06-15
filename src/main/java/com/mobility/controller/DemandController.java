package com.mobility.controller;

import com.mobility.entity.Demand;
import com.mobility.repository.DemandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demand")
public class DemandController {

    @Autowired
    DemandRepository repository;

    @PostMapping("/add")
    public void add(@RequestBody Demand demand) {
        demand.setId(null);
        repository.save(demand);
    }

}
