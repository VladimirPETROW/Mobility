package com.mobility.controller;

import com.mobility.repository.EmployeeRepository;
import com.mobility.repository.PassengerRepository;
import com.mobility.service.DemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

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
        model.addAttribute("demands", demandService.findAll());
        return "demand";
    }

    @PostMapping("/demand/distribute")
    public RedirectView demandDistribute() {
        demandService.distribute();
        return new RedirectView("/demand");
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
