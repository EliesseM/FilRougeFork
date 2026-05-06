package fr.schoolbyhiit.projetfilrouge.controller;


import fr.schoolbyhiit.projetfilrouge.dto.session.SessionDto;
import fr.schoolbyhiit.projetfilrouge.enums.StatutSession;
import fr.schoolbyhiit.projetfilrouge.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SessionDto> getAllSessions(@RequestParam(name = "animateur", required = false) String animateur,
                                           @RequestParam(name = "lieu", required = false) String lieu,
                                           @RequestParam(name = "titre", required = false) String titre,
                                           @RequestParam(name = "capaciteMax", required = false) Integer capaciteMax,
                                           @RequestParam(name = "statut", required = false) StatutSession statutSession,
                                           @RequestParam(name = "evenement", required = false) Long evenementId,
                                           @RequestParam(name = "dateCreationSession", required = false) LocalDateTime dateCreationSession,
                                           @RequestParam(name = "dateDebutSession", required = false) LocalDateTime dateDebutSession,
                                           @RequestParam(name = "dateFinSession", required = false) LocalDateTime dateFinSession,
                                           @RequestParam(name = "dureeSession", required = false) Long dureeSession, Pageable page) {

        return sessionService.findAllSession(page, dureeSession, dateCreationSession, dateDebutSession, dateFinSession, statutSession, lieu, titre, animateur, capaciteMax, evenementId);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SessionDto getSessionById(@PathVariable Long id) {

        return sessionService.findById(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SessionDto createSession(@RequestBody SessionDto sessionDto) {

        return sessionService.createSession(sessionDto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SessionDto updateSession(@PathVariable Long id, @RequestBody SessionDto sessionDto) {

        sessionDto.setSessionId(id);

        return sessionService.updateSession(sessionDto);
    }

    @PatchMapping("/annuler/{id}")
    public SessionDto annulerSession(@PathVariable Long id) {

        return sessionService.annulerSession(id);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteSession(@PathVariable Long id) {

        sessionService.deleteSession(id);

    }

}
