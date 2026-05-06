package fr.schoolbyhiit.projetfilrouge.dto.demandederole;


import com.fasterxml.jackson.annotation.JsonInclude;
import fr.schoolbyhiit.projetfilrouge.dto.utilisateur.UtilisateurDto;
import fr.schoolbyhiit.projetfilrouge.enums.StatutDemandeRole;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DemandeDeRoleDto {

    private Long demandeDeRoleId;

    private StatutDemandeRole statutDemandeRole;
    private UtilisateurDto utilisateur;

}

