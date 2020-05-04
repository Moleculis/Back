package ua.nure.moleculis.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.nure.moleculis.models.dto.MessageDTO;
import ua.nure.moleculis.models.dto.user.CreateGroupDTO;
import ua.nure.moleculis.services.GroupService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/")
    public ResponseEntity<MessageDTO> createGroup(@RequestBody CreateGroupDTO createGroupDTO, HttpServletRequest request) {
        String message = groupService.createGroup(createGroupDTO, request);
        return new ResponseEntity<>(new MessageDTO(message), HttpStatus.OK);
    }
}
