package fr.schoolbyhiit.projetfilrouge.dto.evenement;


import com.fasterxml.jackson.annotation.JsonInclude;
import fr.schoolbyhiit.projetfilrouge.entity.Utilisateur;
import fr.schoolbyhiit.projetfilrouge.enums.StatutEvenement;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)

public class EvenementDto {

    private Long evenementId;
    private Utilisateur utilisateur;

    private EvenementInfoDto infoDto;
    private EvenementDateDto datesDto;
    private EvenementStatutDto statutDto;


}
