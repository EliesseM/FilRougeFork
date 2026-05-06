package fr.schoolbyhiit.projetfilrouge.service;

import fr.schoolbyhiit.projetfilrouge.dto.inscription.InscriptionDto;
import fr.schoolbyhiit.projetfilrouge.entity.Evenement;
import fr.schoolbyhiit.projetfilrouge.entity.Inscription;
import fr.schoolbyhiit.projetfilrouge.entity.Session;
import fr.schoolbyhiit.projetfilrouge.entity.Utilisateur;
import fr.schoolbyhiit.projetfilrouge.enums.StatutEvenement;
import fr.schoolbyhiit.projetfilrouge.enums.StatutInscription;
import fr.schoolbyhiit.projetfilrouge.enums.StatutSession;
import fr.schoolbyhiit.projetfilrouge.exception.AppException;
import fr.schoolbyhiit.projetfilrouge.repository.InscriptionRepository;
import fr.schoolbyhiit.projetfilrouge.repository.SessionRepository;
import fr.schoolbyhiit.projetfilrouge.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InscriptionServiceTest {

    @Mock
    private InscriptionRepository inscriptionRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private InscriptionService inscriptionService;

    private Session session;
    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        session = new Session();
        session.setSessionId(1L);
        session.setCapaciteMax(10);
        session.setDateDebutSession(LocalDateTime.now().plusDays(1));
        session.setDateFinSession(LocalDateTime.now().plusDays(2));
        session.setStatutSession(StatutSession.Confirmer);

        Evenement event = new Evenement();
        event.setStatutEvenement(StatutEvenement.Confirmer);
        session.setEvenement(event);

        utilisateur = new Utilisateur();
        utilisateur.setUtilisateurId("1");
        utilisateur.setRole("USER");
    }

    @Test
    void findInscriptionById_success() {
        Inscription inscription = new Inscription();
        inscription.setInscriptionId(1L);

        when(inscriptionRepository.findById(1L)).thenReturn(Optional.of(inscription));

        InscriptionDto result = inscriptionService.findInscriptionById(1L);

        assertNotNull(result);
    }

    @Test
    void findInscriptionById_notFound() {
        when(inscriptionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () ->
                inscriptionService.findInscriptionById(1L)
        );
    }

    @Test
    void determinerStatut_should_be_confirmer() {
        when(inscriptionRepository.countBySession_SessionIdAndStatutInscription(
                1L, StatutInscription.Confirmer)).thenReturn(5L);

        StatutInscription statut = inscriptionService.determinerStatut(session);

        assertEquals(StatutInscription.Confirmer, statut);
    }

    @Test
    void determinerStatut_should_be_enAttente() {
        when(inscriptionRepository.countBySession_SessionIdAndStatutInscription(
                1L, StatutInscription.Confirmer)).thenReturn(10L);

        StatutInscription statut = inscriptionService.determinerStatut(session);

        assertEquals(StatutInscription.En_attente, statut);
    }

    @Test
    void createInscription_should_be_successful() {
        InscriptionDto dto = new InscriptionDto();
        dto.setSessionId(1L);
        dto.setUtilisateurId("1");

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(utilisateurRepository.findById("1")).thenReturn(Optional.of(utilisateur));

        Page<Inscription> emptyPage = new PageImpl<>(new ArrayList<>());
        when(inscriptionRepository.findByUtilisateur_UtilisateurId(
                "1", Pageable.unpaged())).thenReturn(emptyPage);

        when(inscriptionRepository.countBySession_SessionIdAndStatutInscription(
                1L, StatutInscription.Confirmer)).thenReturn(0L);

        Inscription saved = new Inscription();
        saved.setInscriptionId(1L);

        when(inscriptionRepository.save(argThat(inscription ->
                inscription.getSession().equals(session) &&
                        inscription.getUtilisateur().equals(utilisateur)
        ))).thenReturn(saved);

        InscriptionDto result = inscriptionService.createInscription(dto);

        assertNotNull(result);
    }

    @Test
    void updateInscription_should_be_success() {
        Inscription inscription = new Inscription();
        inscription.setInscriptionId(1L);
        inscription.setUtilisateur(utilisateur);
        inscription.setSession(session);
        inscription.setStatutInscription(StatutInscription.Confirmer);

        InscriptionDto dto = new InscriptionDto();
        dto.setInscriptionId(1L);
        dto.setUtilisateurId("1");
        dto.setStatutInscription(StatutInscription.Confirmer);

        when(inscriptionRepository.findById(1L)).thenReturn(Optional.of(inscription));
        when(inscriptionRepository.save(inscription)).thenReturn(inscription);

        InscriptionDto result = inscriptionService.updateInscription(dto);

        assertNotNull(result);
    }

    @Test
    void updateInscription_should_be_changeUser_forbidden() {
        Inscription inscription = new Inscription();
        inscription.setInscriptionId(1L);
        inscription.setUtilisateur(utilisateur);

        Utilisateur other = new Utilisateur();
        other.setUtilisateurId("2");
        inscription.setUtilisateur(other);

        InscriptionDto dto = new InscriptionDto();
        dto.setInscriptionId(1L);
        dto.setUtilisateurId("1");

        when(inscriptionRepository.findById(1L)).thenReturn(Optional.of(inscription));

        assertThrows(AppException.class, () ->
                inscriptionService.updateInscription(dto)
        );
    }

    @Test
    void annulerInscription_should_be_success() {
        session.setDateDebutSession(LocalDateTime.now().plusDays(2));

        Inscription inscription = new Inscription();
        inscription.setInscriptionId(1L);
        inscription.setSession(session);
        inscription.setStatutInscription(StatutInscription.Confirmer);

        when(inscriptionRepository.findById(1L)).thenReturn(Optional.of(inscription));
        when(inscriptionRepository.save(inscription)).thenReturn(inscription);

        when(inscriptionRepository.countBySession_SessionIdAndStatutInscription(
                1L, StatutInscription.Confirmer)).thenReturn(0L);

        when(inscriptionRepository.findFirstBySession_SessionIdAndStatutInscriptionOrderByInscriptionIdAsc(
                1L, StatutInscription.En_attente)).thenReturn(Optional.empty());

        InscriptionDto result = inscriptionService.annulerInscription(1L);

        assertEquals(StatutInscription.Annuler, inscription.getStatutInscription());
        assertNotNull(result);
    }

    @Test
    void deleteById_admin_should_be_success() {
        utilisateur.setRole("ADMIN");

        Inscription inscription = new Inscription();
        inscription.setInscriptionId(1L);

        when(utilisateurRepository.findById("1")).thenReturn(Optional.of(utilisateur));
        when(inscriptionRepository.findById(1L)).thenReturn(Optional.of(inscription));

        inscriptionService.deleteById(1L, "1");

        verify(inscriptionRepository).delete(inscription);
    }

    @Test
    void deleteById_should_be_not_admin() {
        utilisateur.setRole("USER");

        when(utilisateurRepository.findById("1")).thenReturn(Optional.of(utilisateur));

        assertThrows(AppException.class, () ->
                inscriptionService.deleteById(1L, "1")
        );
    }
}