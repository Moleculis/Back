package ua.nure.moleculis.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.nure.moleculis.models.dto.MessageDTO;
import ua.nure.moleculis.models.dto.PageDTO;
import ua.nure.moleculis.models.dto.event.CreateEventDTO;
import ua.nure.moleculis.models.dto.event.EventResponseDTO;
import ua.nure.moleculis.services.EventService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final ModelMapper modelMapper;

    public EventController(EventService eventService, ModelMapper modelMapper) {
        this.eventService = eventService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<MessageDTO> createEvent(@RequestBody CreateEventDTO createEventDTO, HttpServletRequest request) {
        String message = eventService.createEvent(createEventDTO, request);
        return new ResponseEntity<>(new MessageDTO(message), HttpStatus.OK);
    }

    @GetMapping("/page/{page}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<PageDTO> getEvents(@PathVariable Integer page) {
        Slice<EventResponseDTO> eventDTOs = eventService
                .getEventsByPage(page)
                .map(event -> modelMapper.map(event, EventResponseDTO.class));

        PageDTO pageDTO = modelMapper.map(eventDTOs, PageDTO.class);
        return new ResponseEntity<>(pageDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageDTO> deleteEvent(@PathVariable Long eventId) {
        String result = eventService.deleteEvent(eventId);
        return new ResponseEntity<>(new MessageDTO(result), HttpStatus.OK);
    }
}
