package edu.humber.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.humber.model.User;
import edu.humber.service.UserService;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String manageUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        
        // Add statistics for the page
        long totalUsers = users.size();
        long adminCount = users.stream().filter(u -> "ROLE_ADMIN".equals(u.getRole())).count();
        long userCount = users.stream().filter(u -> "ROLE_USER".equals(u.getRole())).count();
        
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("userCount", userCount);
        
        return "admin/users/manage";
    }
}