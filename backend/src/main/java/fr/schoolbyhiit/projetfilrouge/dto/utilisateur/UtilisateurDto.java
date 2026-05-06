package fr.schoolbyhiit.projetfilrouge.dto.utilisateur;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UtilisateurDto {

    private String utilisateurId;
    private String nom;
    private String prenom;

    private String email;
    private String role;

    private LocalDateTime dateCreationUtilisateurs;
    private String photoProfil;
}
