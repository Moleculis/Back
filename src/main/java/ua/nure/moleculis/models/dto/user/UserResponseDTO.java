package ua.nure.moleculis.models.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.nure.moleculis.models.dto.contact.ReceiverContactResponseDTO;
import ua.nure.moleculis.models.dto.contact.SenderContactResponseDTO;
import ua.nure.moleculis.models.dto.event.EventResponseDTO;
import ua.nure.moleculis.models.dto.group.GroupDTO;
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
    private Set<EventResponseDTO> events;
    private Set<SenderContactResponseDTO> contacts;
    private Set<ReceiverContactResponseDTO> contactRequests;
    private String username;
    private String email;
    private List<Role> roles;
    private Set<GroupDTO> groups;
    private Set<GroupDTO> admin_groups;
}