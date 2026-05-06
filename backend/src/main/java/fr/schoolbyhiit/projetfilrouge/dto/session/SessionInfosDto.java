package fr.schoolbyhiit.projetfilrouge.dto.session;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionInfosDto {

    private String titre;
    private String description;
    private String lieu;
    private String animateur;
    private Integer capaciteMax;
}
