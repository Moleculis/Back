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

        if (createEventDTO.getTitle() == null || createEventDTO.getUsers() == null) {
            throw new CustomException(Translator.toLocale("wrongEventModel"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        event.setTitle(createEventDTO.getTitle());
        event.setDescription(createEventDTO.getDescription());
        event.setDate(LocalDateTime.now());
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

    public Page<Event> getEventsByPage(Integer page) {
        Pageable pageable = PageRequest.of(page, 20);
        return eventRepo.findAll(pageable);
    }
}
