package fr.schoolbyhiit.projetfilrouge.dto.session;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionDateDto {

    private LocalDateTime dateCreationSession;
    private LocalDateTime dateDebutSession;
    private LocalDateTime dateFinSession;
    private Long dureeSession;
    private String dureeFormatee;
}
