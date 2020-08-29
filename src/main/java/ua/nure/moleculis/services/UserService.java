package ua.nure.moleculis.services;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;
import ua.nure.moleculis.components.OnRegistrationCompleteEvent;
import ua.nure.moleculis.components.OnResetPasswordEvent;
import ua.nure.moleculis.components.Translator;
import ua.nure.moleculis.exception.CustomException;
import ua.nure.moleculis.models.dto.user.UserUpdateDTO;
import ua.nure.moleculis.models.entitys.*;
import ua.nure.moleculis.models.enums.Gender;
import ua.nure.moleculis.models.enums.Role;
import ua.nure.moleculis.models.enums.SortDirection;
import ua.nure.moleculis.repos.UserRepo;
import ua.nure.moleculis.security.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {
    private final UserRepo userRepo;

    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    private final ApplicationEventPublisher eventPublisher;

    public UserService(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager, ApplicationEventPublisher eventPublisher) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.eventPublisher = eventPublisher;
    }

    public List<User> getUsersNearby(HttpServletRequest req) {
        final List<User> otherUsers = getAllOtherUsers(req);
        final Random random = new Random();
        List<Long> otherUsersIds = new ArrayList<>();
        for(User user : otherUsers){
            otherUsersIds.add(user.getId());
        }
        final int count = random.nextInt(otherUsersIds.size());
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            final int index = random.nextInt(otherUsersIds.size() - 1);
            final Long id = otherUsersIds.get(index);
            final User usr = userRepo.findUserById(id);
            if (usr != null) {
                users.add(usr);
                otherUsersIds.remove(id);
            }
        }
        return users;
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public List<User> getAllOtherUsers(HttpServletRequest req) {
        final User currentUser = currentUser(req);
        final List<String> usernames = new ArrayList<>();
        usernames.add(currentUser.getUsername());

        for (Contact contact : currentUser.getContacts()) {
            if (!contact.getReceiver().getUsername().equals(currentUser.getUsername())) {
                usernames.add(contact.getReceiver().getUsername());
            } else {
                usernames.add(contact.getSender().getUsername());
            }
        }

        for (Contact contact : currentUser.getContactRequests()) {
            if (!contact.getReceiver().getUsername().equals(currentUser.getUsername())) {
                usernames.add(contact.getReceiver().getUsername());
            } else {
                usernames.add(contact.getSender().getUsername());
            }
        }

        return userRepo.findAllByUsernameIsNotIn(usernames);
    }

    public Page<User> getUsersByPage(Integer page, String userOrderBy, SortDirection sortDirection) {
        final Pageable pageable = getPageableWithSort(page, userOrderBy, sortDirection);
        return userRepo.findAll(pageable);
    }

    static Pageable getPageableWithSort(Integer page, String userOrderBy, SortDirection sortDirection) {
        Pageable pageable = PageRequest.of(page, 20);
        if (userOrderBy != null) {
            Sort sort = Sort.by(userOrderBy);
            if (sortDirection != null) {
                if (!sortDirection.getDirectionCode().equals("ASC")) {
                    sort = Sort.by(userOrderBy).descending();
                }
            }
            pageable = PageRequest.of(page, 20, sort);
        }
        return pageable;
    }

    public User getUser(String username) {
        User user = userRepo.findUserByUsername(username);
        if (user == null) {
            throw new CustomException(Translator.toLocale("noUser"), HttpStatus.NOT_FOUND);
        }
        return user;
    }

    public String updateCurrentUser(HttpServletRequest req, WebRequest request, UserUpdateDTO userUpdateDTO) {
        User user = currentUser(req);

        String dtoUsername = userUpdateDTO.getUsername();
        if (dtoUsername != null && !dtoUsername.equals(user.getUsername())) {
            if (userRepo.existsByUsername(dtoUsername)) {
                throw new CustomException(Translator.toLocale("usernameExists"), HttpStatus.UNPROCESSABLE_ENTITY);
            } else if (dtoUsername.length() < 4) {
                throw new CustomException(Translator.toLocale("shortUsername"), HttpStatus.UNPROCESSABLE_ENTITY);
            } else {
                user.setUsername(dtoUsername);
            }
        }

        String newPassword = userUpdateDTO.getPassword();
        if (newPassword != null) {
            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                throw new CustomException(Translator.toLocale("samePass"), HttpStatus.BAD_REQUEST);
            }
            if (newPassword.length() < 8) {
                throw new CustomException(Translator.toLocale("shortPass"), HttpStatus.BAD_REQUEST);
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        Gender dtoGender = userUpdateDTO.getGender();
        if (dtoGender != null && !dtoGender.equals(user.getGender())) {
            user.setGender(dtoGender);
        }

        String dtoEmail = userUpdateDTO.getEmail();
        String responseMessage = Translator.toLocale("userUpdSuc");
        if (dtoEmail != null && !dtoEmail.equals(user.getEmail())) {
            if (userRepo.existsByEmail(dtoEmail)) {
                throw new CustomException(Translator.toLocale("emailExists"), HttpStatus.UNPROCESSABLE_ENTITY);
            }
            user.setEmail(dtoEmail);
            user.setEnabled(false);
            String appUrl = request.getContextPath();
            String token = jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl, token));
            responseMessage = Translator.toLocale("updUserConfEmail");
        }

        Set<Event> dtoEvents = userUpdateDTO.getEvents();
        Set<Event> events = user.getEvents();
        if (dtoEvents != null && !dtoEvents.equals(events)) {
            for (Event event : events) {
                if (!dtoEvents.contains(event)) {
                    user.removeEvent(event);
                }
            }


            for (Event event : dtoEvents) {
                if (!events.contains(event)) {
                    user.addEvent(event);
                }
            }
            user.setEvents(events);
        }

        Set<Group> dtoGroups = userUpdateDTO.getGroups();
        Set<Group> groups = user.getGroups();
        if (dtoGroups != null && !dtoGroups.equals(groups)) {
            for (Group group : groups) {
                if (!dtoGroups.contains(group)) {
                    user.removeGroup(group);
                }
            }

            for (Group group : dtoGroups) {
                if (!groups.contains(group)) {
                    user.addGroup(group);
                }
            }
            user.setGroups(groups);
        }

        Set<Group> dtoAdminGroups = userUpdateDTO.getAdmin_groups();
        Set<Group> adminGroups = user.getAdmin_groups();
        if (dtoAdminGroups != null && !dtoAdminGroups.equals(adminGroups)) {
            for (Group group : adminGroups) {
                if (!dtoAdminGroups.contains(group)) {
                    user.removeAdminGroup(group);
                }
            }

            for (Group group : dtoAdminGroups) {
                if (!adminGroups.contains(group)) {
                    user.addAdminGroup(group);
                }
            }
            user.setAdmin_groups(groups);
        }

        String dtoDisplayName = userUpdateDTO.getDisplayname();
        if (dtoDisplayName != null && !dtoDisplayName.equals(user.getDisplayname())) {
            user.setDisplayname(dtoDisplayName);
        }

        String dtoFullName = userUpdateDTO.getFullname();
        if (dtoFullName != null && !dtoFullName.equals(user.getFullname())) {
            user.setFullname(dtoFullName);
        }
        userRepo.save(user);

        return responseMessage;
    }

    public String verifyUser(String token) {
        String username = jwtTokenProvider.getUsername(token);
        User user = getUser(username);
        if (user.isEnabled()) {
            return Translator.toLocale("userEnabled");
        }
        user.addTokenToBlacklist(token, LocalDateTime.now());
        user.setEnabled(true);
        userRepo.save(user);
        return Translator.toLocale("emailConfSuc");
    }

    public String resetPassword(String token, String password) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new CustomException(Translator.toLocale("wrongJWT"), HttpStatus.FORBIDDEN);
        }
        final String username = jwtTokenProvider.getUsername(token);
        final User user = getUser(username);
        user.setPassword(passwordEncoder.encode(password));
        user.addTokenToBlacklist(token, LocalDateTime.now());
        userRepo.save(user);
        return Translator.toLocale("resetPassSuccess");
    }

    public String grantAdminPrivileges(String username) {
        User user = userRepo.findUserByUsername(username);
        if (user == null) {
            throw new CustomException(Translator.toLocale("noUser"), HttpStatus.NOT_FOUND);
        }
        Set<Role> roles = user.getRoles();
        if (roles.contains(Role.ROLE_ADMIN)) {
            throw new CustomException(Translator.toLocale("alrAdmin"), HttpStatus.NOT_FOUND);
        }
        roles.add(Role.ROLE_ADMIN);
        user.setRoles(roles);
        userRepo.save(user);
        return Translator.toLocale("permGranted");
    }

    public String login(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return jwtTokenProvider.createToken(username, userRepo.findUserByUsername(username).getRoles());
        } catch (AuthenticationException e) {
            throw new CustomException(Translator.toLocale("invalidCredentials"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public String register(User user, WebRequest request) {
        if (userRepo.existsByUsername(user.getUsername())) {
            throw new CustomException(Translator.toLocale("usernameExists"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if(userRepo.existsByEmail(user.getEmail())){
            throw new CustomException(Translator.toLocale("emailExists"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (user.getPassword() == null) {
            throw new CustomException(Translator.toLocale("noPass"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (user.getPassword().length() < 8) {
            throw new CustomException(Translator.toLocale("shortPass"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (user.getUsername().length() < 4) {
            throw new CustomException(Translator.toLocale("shortUsername"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (user.getUsername().length() > 30) {
            throw new CustomException(Translator.toLocale("longUsername"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        final Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_CLIENT);
        user.setRoles(roles);
        userRepo.save(user);
        String appUrl = request.getContextPath();
        String token = jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl, token));
        return Translator.toLocale("userRegistered");
    }

    public String sendResetPassEmail(String email, WebRequest request) {
        final User user = userRepo.findUserByEmail(email);
        if (user == null) {
            throw new CustomException(Translator.toLocale("noUser"), HttpStatus.NOT_FOUND);
        }
        String appUrl = request.getContextPath();
        String token = jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
        eventPublisher.publishEvent(new OnResetPasswordEvent(user, request.getLocale(), appUrl, token));
        return Translator.toLocale("resetPassMailSent");
    }

    public String logout(HttpServletRequest req) {
        User user = currentUser(req);
        String token = jwtTokenProvider.resolveToken(req);
        user.addTokenToBlacklist(token, LocalDateTime.now());
        userRepo.save(user);
        return Translator.toLocale("logoutSuc");
    }

    public void deleteUser(String username) {
        userRepo.deleteByUsername(username);
    }

    public User currentUser(HttpServletRequest req) {
        return userRepo.findUserByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
    }

    public Boolean isTokenValid(String token) {
        try {
            final User user = userRepo.findUserByUsername(jwtTokenProvider.getUsername(token));
            for (TokenBlacklist tokenBlacklist : user.getBlacklistTokens()) {
                if (tokenBlacklist.getToken().equals(token)) {
                    return false;
                }
            }
            return jwtTokenProvider.validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }
}
