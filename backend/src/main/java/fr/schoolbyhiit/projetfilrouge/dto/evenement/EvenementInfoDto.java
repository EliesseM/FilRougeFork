package fr.schoolbyhiit.projetfilrouge.dto.evenement;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.schoolbyhiit.projetfilrouge.enums.StatutEvenement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvenementInfoDto {

    private String titre;
    private String description;
    private String localisation;
}
