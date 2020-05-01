package ua.nure.moleculis.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO {
    private List<Object> content;
    private Boolean first;
    private Boolean last;
    private Integer number;
    private Integer numberOfElements;
    private Integer size;
    private Integer totalPages;
}
