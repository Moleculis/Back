package ua.nure.moleculis.models.dto.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.nure.moleculis.models.dto.user.UserSmallDTO;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GroupDTO {
    private Long id;

    private String title;

    private String description;

    private Set<UserSmallDTO> users;

    private Set<UserSmallDTO> admins;
}
