package ua.nure.moleculis.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nure.moleculis.models.dto.MessageDTO;
import ua.nure.moleculis.models.dto.group.CreateGroupDTO;
import ua.nure.moleculis.models.dto.group.GroupDTO;
import ua.nure.moleculis.services.GroupService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;
    private final ModelMapper modelMapper;

    public GroupController(GroupService groupService, ModelMapper modelMapper) {
        this.groupService = groupService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/")
    public ResponseEntity<MessageDTO> createGroup(@RequestBody CreateGroupDTO createGroupDTO, HttpServletRequest request) {
        String message = groupService.createGroup(createGroupDTO, request);
        return new ResponseEntity<>(new MessageDTO(message), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<GroupDTO>> getAllGroups() {
        List<GroupDTO> groupDTOS = groupService
                .getAllGroups()
                .stream()
                .map(group -> modelMapper.map(group, GroupDTO.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(groupDTOS, HttpStatus.OK);
    }
}
