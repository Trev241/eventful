package edu.humber.service;

import edu.humber.model.Event;
import edu.humber.model.User;
import edu.humber.repository.EventRepository;
import edu.humber.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public Event createEvent(Event event, Long hostId) {
        User host = userRepository.findById(hostId).orElseThrow(() -> new RuntimeException("Host not found"));
        event.setHost(host);
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        Event event = getEventById(id);
        event.setTitle(updatedEvent.getTitle());
        event.setDescription(updatedEvent.getDescription());
        event.setEventDate(updatedEvent.getEventDate());
        event.setLocation(updatedEvent.getLocation());
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public List<Event> getEventsByHost(Long hostId) {
        User host = userRepository.findById(hostId).orElseThrow(() -> new RuntimeException("Host not found"));
        return eventRepository.findByHost(host);
    }
}
