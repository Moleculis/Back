package ua.nure.moleculis.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.nure.moleculis.components.Translator;
import ua.nure.moleculis.exception.CustomException;
import ua.nure.moleculis.models.dto.group.CreateGroupDTO;
import ua.nure.moleculis.models.entitys.Group;
import ua.nure.moleculis.models.entitys.User;
import ua.nure.moleculis.repos.GroupRepo;
import ua.nure.moleculis.repos.UserRepo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@Service
public class GroupService {

    private final GroupRepo groupRepo;
    private final UserRepo userRepo;
    private final UserService userService;

    public GroupService(GroupRepo groupRepo, UserRepo userRepo, UserService userService) {
        this.groupRepo = groupRepo;
        this.userRepo = userRepo;
        this.userService = userService;
    }

    public String createGroup(CreateGroupDTO createGroupDTO, HttpServletRequest request) {
        final Group group = new Group();

        final User currentUser = userService.currentUser(request);
        group.addAdmin(currentUser);

        group.setTitle(createGroupDTO.getTitle());

        group.setDescription(createGroupDTO.getDescription());

        final Set<String> admins = createGroupDTO.getAdmins();
        if (admins != null) {
            for (String adminUsername : admins) {
                final User admin = userService.getUser(adminUsername);
                admin.addAdminGroup(group);
                group.addAdmin(admin);
            }
        }
        final Set<String> users = createGroupDTO.getUsers();
        if (users != null) {
            for (String username : users) {
                final User user = userService.getUser(username);
                group.addUser(user);
                user.addGroup(group);
            }
        }
        groupRepo.save(group);
        return Translator.toLocale("groupCreated");
    }

    public List<Group> getAllGroups() {
        return groupRepo.findAll();
    }

    public Page<Group> getGroupsByPage(Integer page) {
        Pageable pageable = PageRequest.of(page, 20);
        return groupRepo.findAll(pageable);
    }

    public String updateGroup(Long groupId, CreateGroupDTO groupDTO, HttpServletRequest request) {
        final Group group = groupRepo.findGroupById(groupId);
        final User currentUser = userService.currentUser(request);

        if (!groupContainsAdmin(group, currentUser.getUsername())) {
            throw new CustomException(Translator.toLocale("updateGroupNotAdmin"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        final String title = groupDTO.getTitle();
        if (title != null && !title.isEmpty() && !group.getTitle().equals(title)) {
            group.setTitle(title);
        }
        final String description = groupDTO.getDescription();
        if (description != null && !description.isEmpty() && !group.getDescription().equals(description)) {
            group.setDescription(description);
        }

        final Set<User> users = group.getUsers();
        final Set<String> newUsers = groupDTO.getUsers();

        if (newUsers != null && groupDTO.getAdmins() != null) {
            for (String user : newUsers) {
                if (groupDTO.getAdmins().contains(user)) {
                    throw new CustomException(Translator.toLocale("userAsUserAndAdmin"), HttpStatus.UNPROCESSABLE_ENTITY);
                }
            }
        }

        if (newUsers != null) {
            for (String user : newUsers) {
                if (groupContainsUser(group, user)) {
                    final User u = userRepo.findUserByUsername(user);
                    group.addUser(u);
                }
            }

            for (User user : users) {
                if (!newUsers.contains(user.getUsername())) {
                    group.removeUser(user);
                }
            }
        }

        final Set<User> admins = group.getAdmins();
        final Set<String> newAdmins = groupDTO.getAdmins();
        if (newAdmins != null) {
            for (String admin : newAdmins) {
                if (groupContainsUser(group, admin)) {
                    final User u = userRepo.findUserByUsername(admin);
                    group.addAdmin(u);
                }
            }
            for (User admin : admins) {
                if (!newAdmins.contains(admin.getUsername())) {
                    group.removeAdmin(admin);
                }
            }
        }

        groupRepo.save(group);
        return Translator.toLocale("groupUpdatedSuccessfully");
    }

    public String deleteGroup(Long id, HttpServletRequest request) {
        final User currentUser = userService.currentUser(request);
        final Group group = groupRepo.findGroupById(id);

        if (!group.getAdmins().contains(currentUser)) {
            throw new CustomException(Translator.toLocale("userNotAdmin"), HttpStatus.FORBIDDEN);
        }
        groupRepo.deleteById(id);
        return Translator.toLocale("groupDeleted");
    }

    private boolean groupContainsAdmin(Group group, String username) {
        for (User user : group.getAdmins()) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private boolean groupContainsUser(Group group, String username) {
        for (User user : group.getUsers()) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
}
