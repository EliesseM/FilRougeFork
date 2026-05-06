package fr.schoolbyhiit.projetfilrouge.controller;

import fr.schoolbyhiit.projetfilrouge.dto.evenement.EvenementDto;
import fr.schoolbyhiit.projetfilrouge.entity.Utilisateur;
import fr.schoolbyhiit.projetfilrouge.enums.StatutEvenement;
import fr.schoolbyhiit.projetfilrouge.service.EvenementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/evenements")
@RequiredArgsConstructor
public class EvenementController {

    private final EvenementService evenementService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public EvenementDto createEvenement(@Valid @RequestBody EvenementDto evenementDto) {
        return evenementService.createEvenement(evenementDto);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize("isAuthenticated()")
    public EvenementDto updateEvenement(@PathVariable Long id, @RequestBody EvenementDto evenementDto) {
        evenementDto.setEvenementId(id);
        return evenementService.updateEvenement(id, evenementDto);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEvenement(@PathVariable Long id, @RequestBody Utilisateur utilisateur) {
        evenementService.deleteEvenement(id, utilisateur);
    }

    @PatchMapping("/{id}/annuler")
    // @PreAuthorize("isAuthenticated()")
    public EvenementDto annulerEvenement(@PathVariable Long id, @RequestBody Utilisateur utilisateur) {
        return evenementService.annulerEvenement(id, utilisateur);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize("isAuthenticated()")
    public Page<EvenementDto> findAllEvenement(
            @RequestParam(name = "dureeEvenement", required = false) Long dureeEvenement,
            Pageable pageable,
            @RequestParam(name = "statutEvenement", required = false) StatutEvenement statutEvenement,
            @RequestParam(name = "titre", required = false) String titre,
            @RequestParam(name = "localisation", required = false) String localisation,
            @RequestParam(name = "dateCreationEvenement", required = false) LocalDateTime dateCreationEvenement,
            @RequestParam(name = "dateDebutEvenement", required = false) LocalDateTime dateDebutEvenement,
            @RequestParam(name = "dateFinEvenement", required = false) LocalDateTime dateFinEvenement,
            @RequestParam(name = "utilisateurId", required = false) String utilisateurId) {

        return evenementService.findAllEvenement(pageable, dureeEvenement, statutEvenement, titre, localisation,
                dateCreationEvenement, dateDebutEvenement, dateFinEvenement, utilisateurId);
    }

    @GetMapping(value = "/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public EvenementDto getEvenementById(@PathVariable Long id) {
        return evenementService.getEvenementById(id);
    }
}
