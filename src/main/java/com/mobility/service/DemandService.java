package com.mobility.service;

import com.mobility.demand.Distributor;
import com.mobility.demand.timeline.Timeline;
import com.mobility.entity.Demand;
import com.mobility.entity.Distance;
import com.mobility.entity.Employee;
import com.mobility.repository.DemandRepository;
import com.mobility.repository.DistanceRepository;
import com.mobility.repository.EmployeeRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Log
@Service
public class DemandService {
    @Autowired
    private DistanceRepository distanceRepository;

    @Autowired
    DemandRepository demandRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    public List<Demand> findAll() {
        List<Demand> demands = demandRepository.findAllByOrderById();
        return demands;
    }

    public Demand findById(long id) {
        Demand demand = demandRepository.findById(id);
        return demand;
    }

    public void save(Demand demand) {
        demandRepository.save(demand);
    }

    public List<String> getStatuses() {
        List<String> statuses = demandRepository.findStatuses();
        return statuses;
    }

    HashMap<Integer, HashMap<Integer, Integer>> getDistances() {
        List<Distance> distances = distanceRepository.findAll();
        HashMap<Integer, HashMap<Integer, Integer>> dists = new HashMap<>();
        for (Distance distance : distances) {
            int stFirst = distance.getStFirst().getId();
            int stSecond = distance.getStSecond().getId();
            //First
            HashMap<Integer, Integer> stDists = dists.get(stFirst);
            if (stDists == null) {
                stDists = new HashMap<>();
                dists.put(stFirst, stDists);
            }
            stDists.put(stSecond, distance.getEmpMin());
            //Second
            stDists = dists.get(stSecond);
            if (stDists == null) {
                stDists = new HashMap<>();
                dists.put(stSecond, stDists);
            }
            stDists.put(stFirst, distance.getEmpMin());
        }
        return dists;
    }

    @Transactional
    public void distribute() {
        long elapsed = - System.currentTimeMillis();
        log.info("Distribute demands.");

        List<Date> distributeDates = demandRepository.findDistributeDates();
        if (distributeDates.size() > 0) {
            HashMap<Integer, HashMap<Integer, Integer>> dists = getDistances();

            List<Employee> employees = employeeRepository.findAll();
            for (Date date : distributeDates) {
                LocalDate datePlan = date.toLocalDate();

                log.info(String.format("Distribute for datePlan: %1$td.%1$tm.%1$tY", datePlan));

                List<Demand> demands = demandRepository.findAllByDatePlan(datePlan);
                Distributor distributor = new Distributor(datePlan, demands, employees, dists);
                distributor.distribute();

                int distributed = distributor.completed.size();
                int count = distributed + distributor.remaining.size();

                log.info(String.format("Distributed %d of %d.", distributed, count));
            }
        }

        elapsed += System.currentTimeMillis();
        log.info(String.format("Elapsed total time: %d second(s)", (int) Math.ceil((double) elapsed / 1000)));
    }

    @Transactional
    public void clear() {
        List<Demand> demands = demandRepository.findAll();
        for (Demand dem : demands) {
            dem.getEmp().clear();
        }
    }

    public Timeline createTimeline() {
        List<Date> distributeDates = demandRepository.findDistributeDates();
        if (distributeDates.size() > 0) {
            HashMap<Integer, HashMap<Integer, Integer>> dists = getDistances();

            List<Employee> employees = employeeRepository.findAllByOrderByNameFull();
            LocalDate datePlan = distributeDates.get(0).toLocalDate();
            List<Demand> demands = demandRepository.findAllByDatePlan(datePlan);
            Timeline timeline = new Timeline(datePlan, demands, employees, dists);
            return timeline;
        }
        return null;
    }
}
