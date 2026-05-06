package fr.schoolbyhiit.projetfilrouge.data;

import fr.schoolbyhiit.projetfilrouge.dto.evenement.EvenementDateDto;
import fr.schoolbyhiit.projetfilrouge.dto.evenement.EvenementDto;
import fr.schoolbyhiit.projetfilrouge.dto.evenement.EvenementInfoDto;
import fr.schoolbyhiit.projetfilrouge.dto.evenement.EvenementStatutDto;
import fr.schoolbyhiit.projetfilrouge.entity.Evenement;
import fr.schoolbyhiit.projetfilrouge.entity.Utilisateur;
import fr.schoolbyhiit.projetfilrouge.enums.StatutEvenement;

import java.time.LocalDateTime;

public class EvenementDataTest {

    public static Utilisateur createTestUtilisateur() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUtilisateurId("1");
        utilisateur.setRole("USER");
        return utilisateur;
    }

    public static Utilisateur createAdminUtilisateur() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUtilisateurId("1");
        utilisateur.setRole("ADMIN");
        return utilisateur;
    }

    public static EvenementDto createTestEvenementDto() {
        EvenementDto dto = new EvenementDto();
        dto.setEvenementId(1L);
        dto.setUtilisateur(createTestUtilisateur());

        EvenementInfoDto infoDto = new EvenementInfoDto();
        infoDto.setTitre("Test Event");
        infoDto.setDescription("Test Description");
        infoDto.setLocalisation("Test Location");
        dto.setInfoDto(infoDto);

        EvenementDateDto dateDto = new EvenementDateDto();
        dateDto.setDateDebutEvenement(LocalDateTime.now());
        dateDto.setDateFinEvenement(LocalDateTime.now().plusHours(2));
        dto.setDatesDto(dateDto);

        EvenementStatutDto statutDto = new EvenementStatutDto();
        statutDto.setStatutEvenement(StatutEvenement.En_attente);
        dto.setStatutDto(statutDto);

        return dto;
    }

    public static Evenement createTestEvenement() {
        Evenement evenement = new Evenement();
        evenement.setEvenementId(1L);
        evenement.setUtilisateur(createTestUtilisateur());
        evenement.setTitre("Test Event");
        evenement.setDescription("Test Description");
        evenement.setLocalisation("Test Location");
        evenement.setStatutEvenement(StatutEvenement.En_attente);
        evenement.setDateDebutEvenement(LocalDateTime.now());
        evenement.setDateFinEvenement(LocalDateTime.now().plusHours(2));

        return evenement;
    }
}
