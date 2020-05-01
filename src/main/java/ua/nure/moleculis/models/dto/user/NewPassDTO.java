package ua.nure.moleculis.models.dto.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewPassDTO {
    private String email;
    private String password1;
    private String password2;
}
