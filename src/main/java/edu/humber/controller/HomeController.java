package edu.humber.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import edu.humber.service.AdminDashboardService;

@Controller
public class HomeController {

    private final AdminDashboardService adminDashboardService;

    public HomeController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        // Only load admin data if user is an admin
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            
            // Event statistics
            model.addAttribute("totalEvents", adminDashboardService.getTotalEvents());
            model.addAttribute("activeEvents", adminDashboardService.getActiveEvents());
            model.addAttribute("pastEvents", adminDashboardService.getPastEvents());
            model.addAttribute("cancelledEvents", adminDashboardService.getCancelledEvents());
            
            // User statistics
            model.addAttribute("totalUsers", adminDashboardService.getTotalUsers());
            model.addAttribute("adminUsers", adminDashboardService.getAdminUsers());
            model.addAttribute("regularUsers", adminDashboardService.getRegularUsers());
            
            // Ticket statistics
            model.addAttribute("ticketsSold", adminDashboardService.getTotalTicketsSold());
            model.addAttribute("activeTickets", adminDashboardService.getActiveTickets());
            model.addAttribute("cancelledTickets", adminDashboardService.getCancelledTickets());
            model.addAttribute("totalRevenue", adminDashboardService.getTotalRevenue());
        }
        
        return "index";
    }
}



