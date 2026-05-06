package fr.schoolbyhiit.projetfilrouge.dto.evenement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvenementDateDto {

    private LocalDateTime dateCreationEvenement;

    private LocalDateTime dateDebutEvenement;
    private LocalDateTime dateFinEvenement;
    private Long dureeEvenement;
    private String dureeFormatee;

}
