package ua.nure.moleculis.models.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateEventDTO {
    private String title;

    private String description;

    private String location;

    private Set<String> users;
}
