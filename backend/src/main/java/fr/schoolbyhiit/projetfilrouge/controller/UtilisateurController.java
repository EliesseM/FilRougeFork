package fr.schoolbyhiit.projetfilrouge.controller;

import fr.schoolbyhiit.projetfilrouge.dto.utilisateur.UtilisateurDto;
import fr.schoolbyhiit.projetfilrouge.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<UtilisateurDto> findAllUtilisateurs(@RequestParam(name = "nom", required = false) String nom,
                                                    @RequestParam(name = "prenom", required = false) String prenom,
                                                    @RequestParam(name = "email", required = false) String email,
                                                    @RequestParam(name = "role", required = false) String role,
                                                    @RequestParam(name = "dateCreationUtilisateurs", required = false) LocalDateTime dateCreationUtilisateurs,
                                                    @RequestParam(name = "photoProfil", required = false) String photoProfil,
                                                    Pageable page) {

        return utilisateurService.findAllUtilisateurs(nom, prenom, email, role, dateCreationUtilisateurs, photoProfil, page);
    }

    @GetMapping(value = "/{id}")
    public UtilisateurDto getUtilisateurById(@PathVariable("id") String utilisateurId) {
        return utilisateurService.getUtilisateurById(utilisateurId);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UtilisateurDto register(@RequestBody UtilisateurDto utilisateurDto) {
        return utilisateurService.upsertByGoogleSub(
                utilisateurDto.getUtilisateurId(),
                utilisateurDto.getEmail(),
                utilisateurDto.getNom(),
                utilisateurDto.getPrenom()
        );
    }
}