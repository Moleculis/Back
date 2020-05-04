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

        for (String adminUsername : createGroupDTO.getAdmins()) {
            final User admin = userService.getUser(adminUsername);
            admin.addAdminGroup(group);
            userRepo.save(admin);
        }

        for (String username : createGroupDTO.getUsers()) {
            final User user = userService.getUser(username);
            user.addGroup(group);
            userRepo.save(user);
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


        groupRepo.save(group);
        return "";
    }

    private boolean groupContainsAdmin(Group group, String username) {
        for (User user : group.getAdmins()) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
}
