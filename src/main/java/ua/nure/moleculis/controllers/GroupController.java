package ua.nure.moleculis.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.nure.moleculis.models.dto.MessageDTO;
import ua.nure.moleculis.models.dto.PageDTO;
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<MessageDTO> createGroup(@RequestBody CreateGroupDTO createGroupDTO, HttpServletRequest request) {
        String message = groupService.createGroup(createGroupDTO, request);
        return new ResponseEntity<>(new MessageDTO(message), HttpStatus.OK);
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<GroupDTO>> getAllGroups() {
        List<GroupDTO> groupDTOS = groupService
                .getAllGroups()
                .stream()
                .map(group -> modelMapper.map(group, GroupDTO.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(groupDTOS, HttpStatus.OK);
    }

    @GetMapping("/page/{page}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<PageDTO> getGroups(@PathVariable Integer page) {
        Slice<GroupDTO> groupDTOs = groupService
                .getGroupsByPage(page)
                .map(group -> modelMapper.map(group, GroupDTO.class));

        PageDTO pageDTO = modelMapper.map(groupDTOs, PageDTO.class);
        return new ResponseEntity<>(pageDTO, HttpStatus.OK);
    }

    @PutMapping(value = "/{groupId}}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<MessageDTO> updateGroup(@PathVariable Long groupId, @RequestBody CreateGroupDTO createGroupDTO, HttpServletRequest request) {
        String result = groupService.updateGroup(groupId, createGroupDTO, request);
        return new ResponseEntity<>(new MessageDTO(result), HttpStatus.OK);
    }
}
