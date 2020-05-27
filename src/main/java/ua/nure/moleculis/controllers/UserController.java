package ua.nure.moleculis.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import ua.nure.moleculis.components.Translator;
import ua.nure.moleculis.exception.CustomException;
import ua.nure.moleculis.models.dto.BooleanDTO;
import ua.nure.moleculis.models.dto.MessageDTO;
import ua.nure.moleculis.models.dto.PageDTO;
import ua.nure.moleculis.models.dto.user.*;
import ua.nure.moleculis.models.entitys.User;
import ua.nure.moleculis.models.enums.SortDirection;
import ua.nure.moleculis.security.JwtTokenProvider;
import ua.nure.moleculis.services.ContactService;
import ua.nure.moleculis.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    private final ContactService contactService;

    private final ModelMapper modelMapper;

    private final JwtTokenProvider jwtTokenProvider;


    public UserController(UserService userService, ContactService contactService, ModelMapper modelMapper, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.contactService = contactService;
        this.modelMapper = modelMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        if (!userService.getUser(loginDTO.getUsername()).isEnabled()) {
            throw new CustomException(Translator.toLocale("emailNotConfirmed"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(
                new LoginResponseDTO(userService.login(loginDTO.getUsername(), loginDTO.getPassword())), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<MessageDTO> register(@RequestBody UserDTO userDTO, WebRequest request) {
        User user = modelMapper.map(userDTO, User.class);
        String message = userService.register(user, request);
        return new ResponseEntity<>(new MessageDTO(message), HttpStatus.OK);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<MessageDTO> logout(HttpServletRequest req) {
        String response = userService.logout(req);
        return new ResponseEntity<>(new MessageDTO(response), HttpStatus.OK);
    }

    @GetMapping("/registrationConfirm")
    public ResponseEntity<MessageDTO> confirmRegistration
            (@RequestParam("token") String token) {
        String username = jwtTokenProvider.getUsername(token);

        User user = userService.getUser(username);
        String message = userService.verifyUser(user);
        return new ResponseEntity<>(new MessageDTO(message), HttpStatus.OK);
    }

    @GetMapping("/tokenValid")
    public ResponseEntity<BooleanDTO> confirmResetPasswordToke(@RequestParam("token") String token) {
        return new ResponseEntity<>(new BooleanDTO(userService.isResetPassTokenValid(token)), HttpStatus.OK);
    }

    @GetMapping("/nearby")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<PeopleNearbyDTO> getUsersNearby() {
        List<UserSmallDTO> userDTOS = userService
                .getUsersNearby()
                .stream()
                .map(user -> modelMapper.map(user, UserSmallDTO.class))
                .collect(Collectors.toList());
        final PeopleNearbyDTO peopleNearbyDTO = new PeopleNearbyDTO();
        peopleNearbyDTO.setUsers(userDTOS);
        return new ResponseEntity<>(peopleNearbyDTO, HttpStatus.OK);
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<List<UserResponseDTO>> getUsers() {
        List<UserResponseDTO> userDTOS = userService
                .getAllUsers()
                .stream()
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }

    @GetMapping("/page/{page}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<PageDTO> getUsers(@PathVariable Integer page,
                                            @RequestParam(required = false) String orderBy,
                                            @RequestParam(required = false) SortDirection sortDirection) {
        Slice<UserResponseDTO> userDTOS = userService
                .getUsersByPage(page, orderBy, sortDirection)
                .map(user -> modelMapper.map(user, UserResponseDTO.class));

        PageDTO pageDTO = modelMapper.map(userDTOS, PageDTO.class);
        return new ResponseEntity<>(pageDTO, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageDTO> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return new ResponseEntity<>(new MessageDTO("User " + username + " deleted successfully"), HttpStatus.OK);
    }

    @GetMapping(value = "/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String username) {
        UserResponseDTO userDTO = modelMapper.map(userService.getUser(username), UserResponseDTO.class);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PutMapping(value = "/{username}/grant_admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageDTO> grantAdminPrivileges(@PathVariable String username) {
        String response = userService.grantAdminPrivileges(username);
        return new ResponseEntity<>(new MessageDTO(response), HttpStatus.OK);
    }

    @GetMapping(value = "/current")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<UserResponseDTO> currentUser(HttpServletRequest req) {
        UserResponseDTO userDTO = modelMapper.map(userService.currentUser(req), UserResponseDTO.class);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PutMapping(value = "/current")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<MessageDTO> updateCurrentUser(@RequestBody UserUpdateDTO userDTO,
                                                        HttpServletRequest req, WebRequest request) {
        String result = userService.updateCurrentUser(req, request, userDTO);
        return new ResponseEntity<>(new MessageDTO(result), HttpStatus.OK);
    }

    @PostMapping(value = "/send_contact_request/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<MessageDTO> sendContactRequest(@PathVariable String username, HttpServletRequest req) {
        String result = contactService.sendContactRequest(req, username);
        return new ResponseEntity<>(new MessageDTO(result), HttpStatus.OK);
    }

    @PostMapping(value = "/send_contact_requests")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<MessageDTO> sendContactRequests(@RequestBody SendConnectionRequestsDTO requestsDTO, HttpServletRequest req) {
        String result = contactService.sendContactRequests(req, requestsDTO.getUsernames());
        return new ResponseEntity<>(new MessageDTO(result), HttpStatus.OK);
    }

    @PostMapping(value = "/accept_contact_request/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<MessageDTO> acceptContactRequest(@PathVariable Long id, HttpServletRequest req) {
        String result = contactService.acceptContactRequest(id, req);
        return new ResponseEntity<>(new MessageDTO(result), HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete_contact/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<MessageDTO> deleteContactRequest(@PathVariable Long id) {
        final String result = contactService.deleteContact(id);
        return new ResponseEntity<>(new MessageDTO(result), HttpStatus.OK);
    }
}
