package ua.nure.moleculis.models.dto.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.nure.moleculis.models.enums.Gender;
import ua.nure.moleculis.models.enums.Role;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {
    private String displayname;
    private String fullname;
    private Gender gender;
    private String username;
    private String email;
    private String password;
    private Set<Role> roles;
}
