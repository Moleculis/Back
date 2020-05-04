package ua.nure.moleculis.models.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.nure.moleculis.models.dto.group.GroupDTO;
import ua.nure.moleculis.models.entitys.Event;
import ua.nure.moleculis.models.entitys.User;
import ua.nure.moleculis.models.enums.Gender;
import ua.nure.moleculis.models.enums.Role;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserResponseDTO {
    private String displayname;
    private String fullname;
    private Gender gender;
    private Set<Event> events;
    private Set<User> contacts;
    private String username;
    private String email;
    private List<Role> roles;
    private Set<GroupDTO> groups;
    private Set<GroupDTO> admin_groups;
}