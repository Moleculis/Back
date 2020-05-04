package ua.nure.moleculis.services;

import org.springframework.stereotype.Service;
import ua.nure.moleculis.components.Translator;
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
}
