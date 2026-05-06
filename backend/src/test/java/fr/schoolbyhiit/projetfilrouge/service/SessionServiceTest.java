package fr.schoolbyhiit.projetfilrouge.service;

import fr.schoolbyhiit.projetfilrouge.dto.session.SessionDateDto;
import fr.schoolbyhiit.projetfilrouge.dto.session.SessionDto;
import fr.schoolbyhiit.projetfilrouge.dto.session.SessionInfosDto;
import fr.schoolbyhiit.projetfilrouge.dto.session.SessionStatutDto;
import fr.schoolbyhiit.projetfilrouge.entity.Evenement;
import fr.schoolbyhiit.projetfilrouge.entity.Session;
import fr.schoolbyhiit.projetfilrouge.entity.Utilisateur;
import fr.schoolbyhiit.projetfilrouge.enums.StatutInscription;
import fr.schoolbyhiit.projetfilrouge.enums.StatutSession;
import fr.schoolbyhiit.projetfilrouge.exception.AppException;
import fr.schoolbyhiit.projetfilrouge.exception.CodeErreur;
import fr.schoolbyhiit.projetfilrouge.repository.EvenementRepository;
import fr.schoolbyhiit.projetfilrouge.repository.InscriptionRepository;
import fr.schoolbyhiit.projetfilrouge.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @InjectMocks
    private SessionService sessionService;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private EvenementRepository evenementRepository;

    @Mock
    private InscriptionRepository inscriptionRepository;

    @Mock
    private NotificationService notificationService;

    private Evenement evenement;

    @BeforeEach
    void setUp() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUtilisateurId("1");

        evenement = new Evenement();
        evenement.setEvenementId(1L);
        evenement.setTitre("Event test");
        evenement.setDescription("Description");
        evenement.setLocalisation("Lyon");
        evenement.setDateCreationEvenement(LocalDateTime.now());
        evenement.setDateDebutEvenement(LocalDateTime.now().plusDays(1));
        evenement.setDateFinEvenement(LocalDateTime.now().plusDays(2));
        evenement.setDureeEvenement(24L);
        evenement.setUtilisateur(utilisateur);
    }

    @Test
    void createSession_should_be_successful() {
        SessionDto dto = new SessionDto();
        dto.setSessionId(null);
        dto.setEvenement(evenement);

        SessionDateDto datesDto = new SessionDateDto();
        datesDto.setDateDebutSession(LocalDateTime.now().plusDays(1).plusHours(3));
        datesDto.setDateFinSession(LocalDateTime.now().plusDays(1).plusHours(5));
        dto.setDates(datesDto);

        SessionInfosDto infos = new SessionInfosDto();
        infos.setTitre("Session Test");
        infos.setLieu("Salle 1");
        infos.setAnimateur("Martest");
        infos.setCapaciteMax(10);
        dto.setInfos(infos);

        when(evenementRepository.findById(1L)).thenReturn(Optional.of(evenement));

        when(sessionRepository.findByEvenement_EvenementId(eq(1L), eq(Pageable.unpaged())))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
        when(sessionRepository.save(sessionCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SessionDto result = sessionService.createSession(dto);

        assertNotNull(result);
        assertEquals("Session Test", result.getInfos().getTitre());

        Session savedSession = sessionCaptor.getValue();
        assertEquals("Session Test", savedSession.getTitre());
        assertEquals("Salle 1", savedSession.getLieu());
        assertEquals("Martest", savedSession.getAnimateur());
        assertEquals(10, savedSession.getCapaciteMax());

        verify(sessionRepository, times(1)).save(savedSession);
    }

    @Test
    void createSession_should_throwsException_whenDatesMissing() {
        SessionDto dto = new SessionDto();
        dto.setEvenement(evenement);

        SessionInfosDto infos = new SessionInfosDto();
        infos.setTitre("Session Test");
        infos.setLieu("Salle 1");
        infos.setAnimateur("Moundir");
        infos.setCapaciteMax(10);
        dto.setInfos(infos);
        dto.setDates(null);

        when(evenementRepository.findById(1L)).thenReturn(Optional.of(evenement));

        AppException exception = assertThrows(AppException.class,
                () -> sessionService.createSession(dto));

        assertEquals(CodeErreur.SESSION_DATES_NEEDED, exception.getCodeErreur());
    }

    @Test
    void createSession_should_throwsException_whenEventNotFound() {
        SessionDto dto = new SessionDto();
        dto.setEvenement(evenement);

        when(evenementRepository.findById(evenement.getEvenementId()))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class,
                () -> sessionService.createSession(dto));

        assertEquals(CodeErreur.EVENT_NOT_FOUND, exception.getCodeErreur());
    }

    @Test
    void createSession_should_throwsException_whenCapacityInvalid() {
        SessionDto dto = new SessionDto();
        dto.setEvenement(evenement);

        SessionDateDto datesDto = new SessionDateDto();
        datesDto.setDateDebutSession(LocalDateTime.now().plusDays(1).plusHours(3));
        datesDto.setDateFinSession(LocalDateTime.now().plusDays(1).plusHours(5));
        dto.setDates(datesDto);

        SessionInfosDto infos = new SessionInfosDto();
        infos.setTitre("Session Test");
        infos.setLieu("Salle 1");
        infos.setAnimateur("Tartiflette");
        infos.setCapaciteMax(0);
        dto.setInfos(infos);

        when(evenementRepository.findById(evenement.getEvenementId()))
                .thenReturn(Optional.of(evenement));

        AppException exception = assertThrows(AppException.class,
                () -> sessionService.createSession(dto));

        assertEquals(CodeErreur.SESSION_CAPACITY_INVALID, exception.getCodeErreur());
    }

    @Test
    void createSession_should_throwsException_whenOverlap() {
        SessionDto dto = new SessionDto();
        dto.setEvenement(evenement);

        SessionDateDto datesDto = new SessionDateDto();
        datesDto.setDateDebutSession(LocalDateTime.now().plusDays(1).plusHours(3));
        datesDto.setDateFinSession(LocalDateTime.now().plusDays(1).plusHours(5));
        dto.setDates(datesDto);

        SessionInfosDto infos = new SessionInfosDto();
        infos.setTitre("Session Test");
        infos.setLieu("Salle 1");
        infos.setAnimateur("Kaaris");
        infos.setCapaciteMax(10);
        dto.setInfos(infos);

        Session existing = new Session();
        existing.setLieu("Salle 1");
        existing.setStatutSession(StatutSession.Confirmer);
        existing.setDateDebutSession(LocalDateTime.now().plusDays(1).plusHours(4));
        existing.setDateFinSession(LocalDateTime.now().plusDays(1).plusHours(6));

        when(evenementRepository.findById(evenement.getEvenementId()))
                .thenReturn(Optional.of(evenement));

        when(sessionRepository.findByEvenement_EvenementId(
                evenement.getEvenementId(),
                Pageable.unpaged()
        )).thenReturn(new PageImpl<>(Collections.singletonList(existing)));

        AppException exception = assertThrows(AppException.class,
                () -> sessionService.createSession(dto));

        assertEquals(CodeErreur.SESSION_OVERLAP, exception.getCodeErreur());
    }

    @Test
    void updateSession_should_be_successful() {
        Session existing = new Session();
        existing.setSessionId(1L);
        existing.setDateDebutSession(LocalDateTime.now().plusDays(2));
        existing.setEvenement(evenement);

        SessionDto dto = new SessionDto();
        dto.setSessionId(1L);
        dto.setEvenement(evenement);

        SessionStatutDto statutDto = new SessionStatutDto();
        statutDto.setStatutSession(StatutSession.Confirmer);
        dto.setStatut(statutDto);

        SessionDateDto datesDto = new SessionDateDto();
        datesDto.setDateDebutSession(LocalDateTime.now().plusDays(1).plusHours(3));
        datesDto.setDateFinSession(LocalDateTime.now().plusDays(1).plusHours(5));
        dto.setDates(datesDto);

        SessionInfosDto infos = new SessionInfosDto();
        infos.setTitre("Updated");
        infos.setLieu("Salle 2");
        infos.setAnimateur("Jerome");
        infos.setCapaciteMax(10);
        dto.setInfos(infos);

        when(evenementRepository.findById(evenement.getEvenementId()))
                .thenReturn(Optional.of(evenement));

        when(sessionRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(inscriptionRepository.countBySessionAndStatutInscription(
                existing,
                StatutInscription.Confirmer
        )).thenReturn(0L);

        when(sessionRepository.findByEvenement_EvenementId(
                evenement.getEvenementId(),
                Pageable.unpaged()
        )).thenReturn(new PageImpl<>(Collections.emptyList()));

        when(sessionRepository.save(existing)).thenReturn(existing);

        SessionDto result = sessionService.updateSession(dto);

        assertNotNull(result);
        verify(notificationService).notifySessionUpdate(
                eq(existing),
                contains("modifié")
        );
    }

    @Test
    void annulerSession_should_be_successful() {
        Session session = new Session();
        session.setSessionId(1L);
        session.setStatutSession(StatutSession.Confirmer);
        session.setDateDebutSession(LocalDateTime.now().plusDays(2));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(session)).thenReturn(session);
        when(inscriptionRepository.findBySession_SessionId(1L)).thenReturn(Collections.emptyList());

        SessionDto result = sessionService.annulerSession(1L);

        assertNotNull(result);
        assertEquals(StatutSession.Annuler, session.getStatutSession());
        verify(sessionRepository, times(1)).save(session);
        verify(notificationService, times(1))
                .notifySessionUpdate(eq(session), contains("Session annulée"));
    }

    @Test
    void annulerSession_should_throwsException_ifAlreadyCancelled() {
        Session session = new Session();
        session.setSessionId(1L);
        session.setStatutSession(StatutSession.Annuler);
        session.setDateDebutSession(LocalDateTime.now().plusDays(2));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        AppException exception = assertThrows(AppException.class,
                () -> sessionService.annulerSession(1L));

        assertEquals(CodeErreur.SESSION_ALREADY_CANCELLED, exception.getCodeErreur());
    }

    @Test
    void findById_should_be_successful() {
        Session session = new Session();
        session.setSessionId(1L);
        session.setTitre("Session Test");
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertEquals("Session Test", sessionService.findById(1L).getInfos().getTitre());
    }

    @Test
    void deleteSession_should_throwsException_ifStarted() {
        Session session = new Session();
        session.setSessionId(1L);
        session.setDateDebutSession(LocalDateTime.now().minusHours(1));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        AppException exception = assertThrows(AppException.class,
                () -> sessionService.deleteSession(1L));
        assertTrue(exception.getMessage().contains("Impossible de supprimer"));
    }

    @Test
    void deleteSession_should_throwsException_ifHasRegistrations() {
        Session session = new Session();
        session.setSessionId(1L);
        session.setDateDebutSession(LocalDateTime.now().plusDays(1));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(inscriptionRepository.existsBySession(session)).thenReturn(true);

        AppException exception = assertThrows(AppException.class,
                () -> sessionService.deleteSession(1L));

        assertEquals(CodeErreur.SESSION_HAS_REGISTRATIONS, exception.getCodeErreur());
    }
}