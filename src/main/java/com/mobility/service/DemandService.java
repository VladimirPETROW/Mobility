package com.mobility.service;

import com.mobility.demand.DemandContext;
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
        List<Demand> demands = demandRepository.findAll();
        return demands;
    }

    @Transactional
    public void distribute() {
        long elapsed = - System.currentTimeMillis();
        log.info("Distribute demands.");

        List<Date> distributeDates = demandRepository.findDistributeDates();
        if (distributeDates.size() > 0) {
            //Distances
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

            List<Employee> employees = employeeRepository.findAll();
            for (Date date : distributeDates) {
                LocalDate datePlan = date.toLocalDate();

                log.info(String.format("Distribute for datePlan: %1$td.%1$tm.%1$tY", datePlan));

                List<Demand> demands = demandRepository.findAllByDatePlanAndStatusIn(datePlan, new String[] {"Заявка закончена", "Отказ по регламенту"});
                DemandContext demandContext = new DemandContext(datePlan, demands, employees, dists);
                demandContext.distribute();
                int distributed = 0;
                for (Demand dem : demands) {
                    if (!dem.getEmp().isEmpty()) {
                        distributed++;
                    }
                }

                log.info(String.format("Distributed %d of %d.", distributed, demands.size()));
            }
        }

        elapsed += System.currentTimeMillis();
        log.info(String.format("Elapsed total time: %d second(s)", (int) Math.ceil((double) elapsed / 1000)));
   }
}
