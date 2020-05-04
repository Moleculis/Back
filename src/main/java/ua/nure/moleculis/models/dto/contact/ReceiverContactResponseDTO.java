package ua.nure.moleculis.models.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.nure.moleculis.models.dto.user.UserSmallDTO;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReceiverContactResponseDTO {
    private boolean accepted;
    private UserSmallDTO sender;
}
