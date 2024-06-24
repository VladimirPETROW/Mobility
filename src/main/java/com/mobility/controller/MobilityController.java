package com.mobility.controller;

import com.mobility.demand.timeline.Timeline;
import com.mobility.entity.Demand;
import com.mobility.entity.Employee;
import com.mobility.entity.Passenger;
import com.mobility.repository.DemandRepository;
import com.mobility.repository.EmployeeRepository;
import com.mobility.repository.PassengerRepository;
import com.mobility.service.DemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
public class MobilityController {

    @Autowired
    DemandService demandService;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    EmployeeRepository employeeRepository;

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
        //List<Demand> demandList = demandRepository.findAll();
        model.addAttribute("demands", demandService.findAll());
        return "demand";
    }

    @PostMapping("/demand/distribute")
    public RedirectView demandDistribute() {
        demandService.distribute();
        return new RedirectView("/demand");
    }

    @PostMapping("/demand/clear")
    public RedirectView demandClear() {
        demandService.clear();
        return new RedirectView("/demand");
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

    @PostMapping("/timeline/distribute")
    public RedirectView timelineDistribute() {
        demandService.distribute();
        return new RedirectView("/timeline");
    }

    @PostMapping("/timeline/clear")
    public RedirectView timelineClear() {
        demandService.clear();
        return new RedirectView("/timeline");
    }

    @GetMapping("/passenger")
    public String passenger(@CurrentSecurityContext SecurityContext context, Model model) {
        String userName = context.getAuthentication().getName();
        String role = context.getAuthentication().getAuthorities().iterator().next().toString();
        model.addAttribute("name", userName);
        model.addAttribute("role", role);
        //List<Demand> demandList = demandRepository.findAll();
        model.addAttribute("passengers", passengerRepository.findAll());
        return "passenger";
    }

    @GetMapping("/employee")
    public String employee(@CurrentSecurityContext SecurityContext context, Model model) {
        String userName = context.getAuthentication().getName();
        String role = context.getAuthentication().getAuthorities().iterator().next().toString();
        model.addAttribute("name", userName);
        model.addAttribute("role", role);
        //List<Demand> demandList = demandRepository.findAll();
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
