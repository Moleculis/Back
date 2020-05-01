package ua.nure.moleculis.models.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.nure.moleculis.models.entitys.Event;
import ua.nure.moleculis.models.entitys.Group;
import ua.nure.moleculis.models.entitys.User;
import ua.nure.moleculis.models.enums.Gender;
import ua.nure.moleculis.models.enums.Role;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserUpdateDTO {
    private String displayname;
    private String fullname;
    private Gender gender;
    private Set<Event> events;
    private Set<User> contacts;
    private String username;
    private String password;
    private String email;
    private Set<Role> roles;
    private Set<Group> groups;
    private Set<Group> admin_groups;
    ;
}
