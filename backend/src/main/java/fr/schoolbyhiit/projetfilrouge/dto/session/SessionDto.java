package fr.schoolbyhiit.projetfilrouge.dto.session;


import com.fasterxml.jackson.annotation.JsonInclude;
import fr.schoolbyhiit.projetfilrouge.entity.Evenement;
import fr.schoolbyhiit.projetfilrouge.entity.Inscription;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)

public class SessionDto {

    private Long sessionId;
    private Evenement evenement;
    private Inscription inscriptions;


    private SessionDateDto dates;
    private SessionInfosDto infos;
    private SessionStatutDto statut;

}
