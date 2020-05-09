package ua.nure.moleculis.models.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.nure.moleculis.models.dto.user.UserSmallDTO;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventResponseDTO {
    private Long id;

    private String title;

    private String description;

    private LocalDateTime date;

    private LocalDateTime dateCreated;

    private String location;

    private Set<UserSmallDTO> users;
}