package fr.schoolbyhiit.projetfilrouge.dto.utilisateur;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UtilisateurRoleDto {
    private String role;

}
