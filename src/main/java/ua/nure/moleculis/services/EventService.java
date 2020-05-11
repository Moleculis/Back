package ua.nure.moleculis.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.nure.moleculis.components.Translator;
import ua.nure.moleculis.exception.CustomException;
import ua.nure.moleculis.models.dto.event.CreateEventDTO;
import ua.nure.moleculis.models.entitys.Event;
import ua.nure.moleculis.models.entitys.User;
import ua.nure.moleculis.repos.EventRepo;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class EventService {

    private final EventRepo eventRepo;
    private final UserService userService;

    public EventService(EventRepo eventRepo, UserService userService) {
        this.eventRepo = eventRepo;
        this.userService = userService;
    }

    public String createEvent(CreateEventDTO createEventDTO, HttpServletRequest request) {
        final User currentUser = userService.currentUser(request);

        final Event event = new Event();

        if (createEventDTO.getTitle() == null || createEventDTO.getDate() == null || createEventDTO.getUsers() == null) {
            throw new CustomException(Translator.toLocale("wrongEventModel"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        event.setTitle(createEventDTO.getTitle());
        event.setDescription(createEventDTO.getDescription());

        event.setPrivate(createEventDTO.isPrivate());

        event.setDateCreated(LocalDateTime.now());
        event.setDate(createEventDTO.getDate());
        event.setLocation(createEventDTO.getLocation());

        event.addUser(currentUser);

        final Set<String> users = createEventDTO.getUsers();
        if (users != null) {
            for (String user : users) {
                if (!user.equals(currentUser.getUsername())) {
                    final User userModel = userService.getUser(user);
                    event.addUser(userModel);
                }
            }
        }
        eventRepo.save(event);

        return Translator.toLocale("eventCreatedSuccessfully");
    }

    public Page<Event> getEventsByPage(Integer page, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, 20);
        final User currentUser = userService.currentUser(request);


        return eventRepo.findAllByUsersContains(currentUser, pageable);
    }

    public Page<Event> getOthersEventsByPage(Integer page, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, 20);
        final User currentUser = userService.currentUser(request);

        return eventRepo.findDistinctByUsersNotContains(currentUser, pageable);
    }

    public Event getEvent(Long id) {
        final Event event = eventRepo.findById(id);
        if (event == null) {
            throw new CustomException(Translator.toLocale("noEvent"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return event;
    }

    public String leaveEvent(Long eventId, HttpServletRequest request) {
        final Event event = eventRepo.findById(eventId);
        final User user = userService.currentUser(request);

        event.removeUser(user);

        if (event.getUsers().isEmpty()) {
            return deleteEvent(eventId);
        }

        eventRepo.save(event);

        return Translator.toLocale("eventLeft");
    }

    public String updateEvent(Long eventId, CreateEventDTO updateEventDTO, HttpServletRequest request) {
        final User currentUser = userService.currentUser(request);
        final Event event = eventRepo.findById(eventId);

        if (updateEventDTO.getUsers() != null && !containsUser(updateEventDTO.getUsers(), currentUser)) {
            throw new CustomException(Translator.toLocale("unAuth"), HttpStatus.UNAUTHORIZED);
        }

        if (updateEventDTO.isPrivate() != event.isPrivate()) {
            event.setPrivate(updateEventDTO.isPrivate());
        }

        final String title = updateEventDTO.getTitle();
        if (title != null && title.length() > 5) {
            event.setTitle(title);
        }
        final String description = updateEventDTO.getDescription();
        if (description != null) {
            event.setDescription(description);
        }

        final LocalDateTime date = updateEventDTO.getDate();
        if (date != null && date.isAfter(LocalDateTime.now())) {
            event.setDate(date);
        }

        final String location = updateEventDTO.getLocation();
        if (location != null && !location.isEmpty()) {
            event.setLocation(location);
        }

        final Set<String> users = updateEventDTO.getUsers();
        if (users != null && !users.isEmpty()) {
            for (String username : users) {
                if (!containsUsername(event.getUsers(), username)) {
                    final User user = userService.getUser(username);
                    event.addUser(user);
                    user.addEvent(event);
                }
            }
            for (User user : event.getUsers()) {
                if (!users.contains(user.getUsername())) {
                    event.removeUser(user);
                    user.removeEvent(event);
                }
            }
        }

        eventRepo.save(event);

        return Translator.toLocale("eventUpdatedSuccessfully");
    }

    public String deleteEvent(Long eventId) {
        eventRepo.deleteById(eventId);
        return Translator.toLocale("eventDeleted");
    }

    private boolean containsUsername(Set<User> users, String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsUser(Set<String> users, User user) {
        for (String username : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
}
