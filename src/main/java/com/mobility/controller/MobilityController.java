package com.mobility.controller;

import com.mobility.entity.Demand;
import com.mobility.repository.EmployeeRepository;
import com.mobility.repository.PassengerRepository;
import com.mobility.repository.StationRepository;
import com.mobility.service.DemandService;
import com.mobility.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalTime;
import java.util.List;

@Controller
public class MobilityController {

    @Autowired
    DemandService demandService;

    @Autowired
    StationService stationService;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    private StationRepository stationRepository;

    @GetMapping("/")
    public String index(@CurrentSecurityContext SecurityContext context, Model model) {
        return demand(context, model);
    }

    @GetMapping("/demand")
    public String demand(@CurrentSecurityContext SecurityContext context, Model model) {
        String userName = context.getAuthentication().getName();
        String role = context.getAuthentication().getAuthorities().iterator().next().toString();
        model.addAttribute("name", userName);
        model.addAttribute("role", role);
        List<Demand> demands = demandService.findAll();
        model.addAttribute("demands", demands);
        return "demand";
    }

    @GetMapping("/demid")
    public String demid(@CurrentSecurityContext SecurityContext context, @RequestParam int id, Model model) {
        String userName = context.getAuthentication().getName();
        String role = context.getAuthentication().getAuthorities().iterator().next().toString();
        model.addAttribute("name", userName);
        model.addAttribute("role", role);
        model.addAttribute("demand", demandService.findById(id));
        model.addAttribute("statuses", demandService.getStatuses());
        model.addAttribute("stations", stationRepository.findAll());
        return "demid";
    }

    @PostMapping("/demedit")
    public RedirectView demedit(@RequestParam String id,
                                @RequestParam String status,
                                @RequestParam String timePlan,
                                @RequestParam String stBegin,
                                @RequestParam String stEnd,
                                @RequestParam String cat,
                                @RequestParam String duration,
                                @RequestParam String empM,
                                @RequestParam String empF,
                                @RequestParam String redirect) {
        Demand demand = demandService.findById(Long.parseLong(id));
        demand.setStatus(status);

        String[] time = timePlan.split(":");
        demand.setTimePlan(LocalTime.of(Integer.parseInt(time[0].trim()), Integer.parseInt(time[1].trim())));

        demand.setStBegin(stationService.findById(Long.parseLong(stBegin)));
        demand.setStEnd(stationService.findById(Long.parseLong(stEnd)));
        demand.setCat(cat);

        time = duration.split(":");
        demand.setDuration(LocalTime.of(Integer.parseInt(time[0].trim()), Integer.parseInt(time[1].trim()), Integer.parseInt(time[2].trim())));

        empM = empM.trim();
        demand.setEmpM(empM.isEmpty() ? 0 : Integer.parseInt(empM));

        empF = empF.trim();
        demand.setEmpF(empF.isEmpty() ? 0 : Integer.parseInt(empF));

        demand.setEmp(null);
        demandService.save(demand);
        return new RedirectView(redirect);
    }

    @PostMapping("/clear")
    public RedirectView demandClear(@RequestParam String redirect) {
        demandService.clear();
        return new RedirectView(redirect);
    }

    @PostMapping("/distribute")
    public RedirectView demandDistribute(@RequestParam String redirect) {
        demandService.distribute();
        return new RedirectView(redirect);
    }

    @GetMapping("/timeline")
    public String timeline(@CurrentSecurityContext SecurityContext context, Model model) {
        String userName = context.getAuthentication().getName();
        String role = context.getAuthentication().getAuthorities().iterator().next().toString();
        model.addAttribute("name", userName);
        model.addAttribute("role", role);
        model.addAttribute("timeline", demandService.createTimeline());
        return "timeline";
    }

    @GetMapping("/passenger")
    public String passenger(@CurrentSecurityContext SecurityContext context, Model model) {
        String userName = context.getAuthentication().getName();
        String role = context.getAuthentication().getAuthorities().iterator().next().toString();
        model.addAttribute("name", userName);
        model.addAttribute("role", role);
        model.addAttribute("passengers", passengerRepository.findAll());
        return "passenger";
    }

    @GetMapping("/employee")
    public String employee(@CurrentSecurityContext SecurityContext context, Model model) {
        String userName = context.getAuthentication().getName();
        String role = context.getAuthentication().getAuthorities().iterator().next().toString();
        model.addAttribute("name", userName);
        model.addAttribute("role", role);
        model.addAttribute("employees", employeeRepository.findAll());
        return "employee";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

}
