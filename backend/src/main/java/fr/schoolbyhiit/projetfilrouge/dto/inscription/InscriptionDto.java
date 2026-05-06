package fr.schoolbyhiit.projetfilrouge.dto.inscription;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.schoolbyhiit.projetfilrouge.enums.StatutInscription;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InscriptionDto {

    private Long inscriptionId;
    private StatutInscription statutInscription;


    private String utilisateurId;
    private String utilisateurNom;


    private Long sessionId;
    private String sessionTitre;
}
