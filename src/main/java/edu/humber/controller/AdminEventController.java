package edu.humber.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.humber.model.Event;
import edu.humber.service.EventService;

@Controller
@RequestMapping("/admin/events")
public class AdminEventController {

	private final EventService eventService;

	public AdminEventController(EventService eventService) {
		this.eventService = eventService;
	}

	@GetMapping("/new")
	public String newEventForm(Model model) {
		if (!model.containsAttribute("event")) {
			model.addAttribute("event", Event.builder().cancelled(false).build());
		}
		return "admin/events/form";
	}

	@GetMapping("/{id}/edit")
	public String editEventForm(@org.springframework.web.bind.annotation.PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Event event = eventService.getEventById(id);
			model.addAttribute("event", event);
			return "admin/events/form";
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("error", "Event not found.");
			return "redirect:/events";
		}
	}

	@PostMapping
	public String createEvent(
			@RequestParam String title,
			@RequestParam(required = false) String description,
			@RequestParam("eventDate") String eventDateStr,
			@RequestParam String location,
			@RequestParam(required = false) Integer capacity,
			@RequestParam Double price,
			@RequestParam(required = false, defaultValue = "false") boolean cancelled,
			RedirectAttributes redirectAttributes,
			Model model
	) {
		// Minimal validation
		if (title == null || title.isBlank() || location == null || location.isBlank() || eventDateStr == null || eventDateStr.isBlank()) {
			model.addAttribute("error", "Please fill in the required fields.");
			model.addAttribute("event", Event.builder()
					.title(title)
					.description(description)
					.location(location)
					.capacity(capacity == null ? 0 : capacity)
					.price(price == null ? 0.0 : price)
					.cancelled(cancelled)
					.build());
			return "admin/events/form";
		}

		LocalDateTime eventDate = null;
		try {
			// Handle HTML datetime-local (yyyy-MM-dd'T'HH:mm)
			DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
			eventDate = LocalDateTime.parse(eventDateStr, f);
		} catch (DateTimeParseException e1) {
			try {
				// Fallback to ISO_LOCAL_DATE_TIME
				eventDate = LocalDateTime.parse(eventDateStr);
			} catch (DateTimeParseException e2) {
				model.addAttribute("error", "Invalid date/time format.");
				model.addAttribute("event", Event.builder()
						.title(title)
						.description(description)
						.location(location)
						.capacity(capacity == null ? 0 : capacity)
						.price(price == null ? 0.0 : price)
						.cancelled(cancelled)
						.build());
				return "admin/events/form";
			}
		}

		Event event = Event.builder()
				.title(title)
				.description(description)
				.eventDate(eventDate)
				.location(location)
				.capacity(capacity == null ? 0 : capacity)
				.price(price == null ? 0.0 : price)
				.cancelled(cancelled)
				.build();

		eventService.createEvent(event);
		redirectAttributes.addFlashAttribute("message", "Event created successfully.");
		return "redirect:/events";
	}

	@PostMapping("/{id}")
	public String updateEvent(
			@org.springframework.web.bind.annotation.PathVariable Long id,
			@RequestParam String title,
			@RequestParam(required = false) String description,
			@RequestParam("eventDate") String eventDateStr,
			@RequestParam String location,
			@RequestParam(required = false) Integer capacity,
			@RequestParam Double price,
			@RequestParam(required = false, defaultValue = "false") boolean cancelled,
			RedirectAttributes redirectAttributes,
			Model model
	) {
		if (title == null || title.isBlank() || location == null || location.isBlank() || eventDateStr == null || eventDateStr.isBlank()) {
			model.addAttribute("error", "Please fill in the required fields.");
			model.addAttribute("event", Event.builder()
					.id(id)
					.title(title)
					.description(description)
					.location(location)
					.capacity(capacity == null ? 0 : capacity)
					.price(price == null ? 0.0 : price)
					.cancelled(cancelled)
					.build());
			return "admin/events/form";
		}

		LocalDateTime eventDate = null;
		try {
			DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
			eventDate = LocalDateTime.parse(eventDateStr, f);
		} catch (DateTimeParseException e1) {
			try {
				eventDate = LocalDateTime.parse(eventDateStr);
			} catch (DateTimeParseException e2) {
				model.addAttribute("error", "Invalid date/time format.");
				model.addAttribute("event", Event.builder()
						.id(id)
						.title(title)
						.description(description)
						.location(location)
						.capacity(capacity == null ? 0 : capacity)
						.price(price == null ? 0.0 : price)
						.cancelled(cancelled)
						.build());
				return "admin/events/form";
			}
		}

		Event updated = Event.builder()
				.id(id)
				.title(title)
				.description(description)
				.eventDate(eventDate)
				.location(location)
				.capacity(capacity == null ? 0 : capacity)
				.price(price == null ? 0.0 : price)
				.cancelled(cancelled)
				.build();

		try {
			eventService.updateEvent(id, updated);
			redirectAttributes.addFlashAttribute("message", "Event updated successfully.");
			return "redirect:/events/" + id;
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("error", "Event not found.");
			return "redirect:/events";
		}
	}

	@PostMapping("/{id}/delete")
	public String deleteEvent(@org.springframework.web.bind.annotation.PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			eventService.deleteEvent(id);
			redirectAttributes.addFlashAttribute("message", "Event deleted.");
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("error", "Event not found.");
		}
		return "redirect:/events";
	}
}


