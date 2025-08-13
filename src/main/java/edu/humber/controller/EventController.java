package edu.humber.controller;

import edu.humber.model.Event;
import edu.humber.model.User;
import edu.humber.service.EventService;
import edu.humber.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping
    public String getAllEvents(Model model, Authentication authentication) {
        model.addAttribute("events", eventService.getAllEvents());

        if (authentication != null && authentication.isAuthenticated()) {
            Optional<User> currentUserOpt = userService.findByEmail(authentication.getName());
            currentUserOpt.ifPresent(user -> model.addAttribute("currentUserId", user.getId()));
        }

        return "events/list";
    }

    @GetMapping("/{id}")
    public String getEventById(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);
        return "events/detail";
    }
}
