package fr.schoolbyhiit.projetfilrouge.dto.session;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.schoolbyhiit.projetfilrouge.enums.StatutSession;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionStatutDto {
    private StatutSession statutSession;
}
