package fr.schoolbyhiit.projetfilrouge.service;

import fr.schoolbyhiit.projetfilrouge.data.EvenementDataTest;
import fr.schoolbyhiit.projetfilrouge.dto.evenement.EvenementDto;
import fr.schoolbyhiit.projetfilrouge.entity.Evenement;
import fr.schoolbyhiit.projetfilrouge.entity.Utilisateur;
import fr.schoolbyhiit.projetfilrouge.enums.StatutEvenement;
import fr.schoolbyhiit.projetfilrouge.exception.AppException;
import fr.schoolbyhiit.projetfilrouge.exception.CodeErreur;
import fr.schoolbyhiit.projetfilrouge.repository.EvenementRepository;
import fr.schoolbyhiit.projetfilrouge.repository.UtilisateurRepository;
import fr.schoolbyhiit.projetfilrouge.service.dates.ValidationDatesEvenement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EvenementServiceTest {

    @Mock
    private EvenementRepository evenementRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private ValidationDatesEvenement validationDatesEvenement;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private EvenementService evenementService;


    @Test
    public void create_Evenement_should_Return_EvenementDto_when_Valid_Input() {
        EvenementDto evenementDto = EvenementDataTest.createTestEvenementDto();

        evenementDto.getDatesDto().setDateDebutEvenement(LocalDateTime.now().plusDays(1));
        evenementDto.getDatesDto().setDateFinEvenement(LocalDateTime.now().plusDays(2));

        Evenement savedEvenement = EvenementDataTest.createTestEvenement();

        savedEvenement.setDateDebutEvenement(LocalDateTime.now().plusDays(1));

        Utilisateur testUtilisateur = EvenementDataTest.createTestUtilisateur();

        when(utilisateurRepository.findById("1")).thenReturn(Optional.of(testUtilisateur));
        when(validationDatesEvenement.chevauchementEvenement(evenementDto, null)).thenReturn(false);
        when(validationDatesEvenement.validateDates(evenementDto.getDatesDto(), null)).thenReturn(evenementDto.getDatesDto());
        when(evenementRepository.save(argThat(evenement ->
                evenement.getTitre().equals(evenementDto.getInfoDto().getTitre()) &&
                        evenement.getUtilisateur().equals(testUtilisateur)
        ))).thenReturn(savedEvenement);

        EvenementDto result = evenementService.createEvenement(evenementDto);

        assertThat(result).isNotNull();
        assertThat(result.getEvenementId()).isEqualTo(1L);
        assertThat(result.getInfoDto().getTitre()).isEqualTo("Test Event");
    }

    @Test
    public void create_Evenement_should_Throw_Exception_when_User_Not_Found() {
        EvenementDto evenementDto = EvenementDataTest.createTestEvenementDto();


        when(utilisateurRepository.findById("1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> evenementService.createEvenement(evenementDto))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("codeErreur", CodeErreur.USER_NOT_FOUND)
                .hasMessageContaining("Utilisateur non trouvé");
    }

    @Test
    public void create_Evenement_should_Throw_Exception_when_Date_Anterieure_Creation() {
        EvenementDto evenementDto = EvenementDataTest.createTestEvenementDto();

        evenementDto.getDatesDto().setDateDebutEvenement(LocalDateTime.now().minusDays(1));

        Utilisateur testUtilisateur = EvenementDataTest.createTestUtilisateur();

        when(utilisateurRepository.findById("1")).thenReturn(Optional.of(testUtilisateur));

        assertThatThrownBy(() -> evenementService.createEvenement(evenementDto))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("codeErreur", CodeErreur.EVENT_DATE_ANTERIEURE_CREATION);
    }

    @Test
    public void create_Evenement_should_Throw_Exception_when_Event_Overlap() {
        EvenementDto evenementDto = EvenementDataTest.createTestEvenementDto();

        evenementDto.getDatesDto().setDateDebutEvenement(LocalDateTime.now().plusDays(1));
        evenementDto.getDatesDto().setDateFinEvenement(LocalDateTime.now().plusDays(2));

        Utilisateur testUtilisateur = EvenementDataTest.createTestUtilisateur();

        when(utilisateurRepository.findById("1")).thenReturn(Optional.of(testUtilisateur));
        when(validationDatesEvenement.chevauchementEvenement(evenementDto, null)).thenReturn(true);

        assertThatThrownBy(() -> evenementService.createEvenement(evenementDto))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("codeErreur", CodeErreur.EVENT_OVERLAP);
    }

    @Test
    public void create_Evenement_should_Throw_Exception_when_Invalid_Dates() {
        EvenementDto evenementDto = EvenementDataTest.createTestEvenementDto();

        evenementDto.getDatesDto().setDateDebutEvenement(LocalDateTime.now().plusDays(1));
        evenementDto.getDatesDto().setDateFinEvenement(LocalDateTime.now().plusDays(2));

        Utilisateur testUtilisateur = EvenementDataTest.createTestUtilisateur();

        when(utilisateurRepository.findById("1")).thenReturn(Optional.of(testUtilisateur));
        when(validationDatesEvenement.chevauchementEvenement(evenementDto, null)).thenReturn(false);
        when(validationDatesEvenement.validateDates(evenementDto.getDatesDto(), null))
                .thenThrow(new AppException(CodeErreur.EVENT_DATES_INVALID, "Dates invalides"));

        assertThatThrownBy(() -> evenementService.createEvenement(evenementDto))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("codeErreur", CodeErreur.EVENT_DATES_INVALID);
    }


    @Test
    public void update_Evenement_should_Return_Updated_EvenementDto_when_Valid_Input() {
        Long id = 1L;
        EvenementDto evenementDto = EvenementDataTest.createTestEvenementDto();
        evenementDto.getDatesDto().setDateDebutEvenement(LocalDateTime.now().plusDays(1));
        evenementDto.getDatesDto().setDateFinEvenement(LocalDateTime.now().plusDays(2));

        Evenement existingEvenement = EvenementDataTest.createTestEvenement();

        existingEvenement.setDateDebutEvenement(LocalDateTime.now().plusDays(1));

        when(evenementRepository.findById(id)).thenReturn(Optional.of(existingEvenement));
        when(validationDatesEvenement.chevauchementEvenement(evenementDto, id)).thenReturn(false);
        when(validationDatesEvenement.validateDates(evenementDto.getDatesDto(), existingEvenement)).thenReturn(evenementDto.getDatesDto());
        when(evenementRepository.save(argThat(evenement ->
                evenement.getEvenementId().equals(id) &&
                        evenement.getTitre().equals(evenementDto.getInfoDto().getTitre())
        ))).thenReturn(existingEvenement);

        EvenementDto result = evenementService.updateEvenement(id, evenementDto);

        assertThat(result).isNotNull();
        assertThat(result.getEvenementId()).isEqualTo(1L);
    }

    @Test
    public void update_Evenement_should_Throw_Exception_when_Event_Already_Started() {
        Long id = 1L;
        EvenementDto evenementDto = EvenementDataTest.createTestEvenementDto();
        Evenement existingEvenement = EvenementDataTest.createTestEvenement();

        existingEvenement.setDateDebutEvenement(LocalDateTime.now().minusDays(1));

        when(evenementRepository.findById(id)).thenReturn(Optional.of(existingEvenement));

        assertThatThrownBy(() -> evenementService.updateEvenement(id, evenementDto))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("codeErreur", CodeErreur.EVENT_ALREADY_STARTED);
    }

    @Test
    public void update_Evenement_should_Throw_Exception_when_Event_Cancelled() {
        Long id = 1L;
        EvenementDto evenementDto = EvenementDataTest.createTestEvenementDto();
        Evenement existingEvenement = EvenementDataTest.createTestEvenement();

        existingEvenement.setDateDebutEvenement(LocalDateTime.now().plusDays(1));
        existingEvenement.setStatutEvenement(StatutEvenement.Annuler);

        when(evenementRepository.findById(id)).thenReturn(Optional.of(existingEvenement));

        assertThatThrownBy(() -> evenementService.updateEvenement(id, evenementDto))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("codeErreur", CodeErreur.EVENT_CANCELLED);
    }


    @Test
    public void should_Return_Annule_EvenementDto_when_Valid_Input() {
        Long id = 1L;
        Utilisateur utilisateur = EvenementDataTest.createTestUtilisateur();
        Evenement existingEvenement = EvenementDataTest.createTestEvenement();

        when(evenementRepository.findById(id)).thenReturn(Optional.of(existingEvenement));
        when(evenementRepository.save(argThat(evenement ->
                evenement.getEvenementId().equals(id) &&
                        evenement.getStatutEvenement().equals(StatutEvenement.Annuler)
        ))).thenReturn(existingEvenement);

        EvenementDto result = evenementService.annulerEvenement(id, utilisateur);

        assertThat(result).isNotNull();
        assertThat(result.getStatutDto().getStatutEvenement()).isEqualTo(StatutEvenement.Annuler);
        verify(notificationService, times(1))
                .notifyEvenementUpdate(existingEvenement, "L'événement a été annulé : " + existingEvenement.getTitre());
    }

    @Test
    public void annulerEvenement_should_Throw_Exception_when_User_Not_Authorized() {
        Long id = 1L;
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUtilisateurId("2");
        Evenement existingEvenement = EvenementDataTest.createTestEvenement();

        when(evenementRepository.findById(id)).thenReturn(Optional.of(existingEvenement));

        assertThatThrownBy(() -> evenementService.annulerEvenement(id, utilisateur))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("codeErreur", CodeErreur.NOT_ADMIN_UNAUTHORIZED);
    }

    @Test
    public void delete_Evenement_should_Not_Throw_Exception_when_User_Is_Admin() {
        Long id = 1L;
        Utilisateur adminUtilisateur = EvenementDataTest.createAdminUtilisateur();

        when(evenementRepository.existsById(id)).thenReturn(true);

        assertThatNoException().isThrownBy(() -> evenementService.deleteEvenement(id, adminUtilisateur));
    }

    @Test
    public void deleteEvenement_should_Throw_Exception_when_User_Not_Admin() {
        Long id = 1L;
        Utilisateur utilisateur = EvenementDataTest.createTestUtilisateur();

        assertThatThrownBy(() -> evenementService.deleteEvenement(id, utilisateur))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("codeErreur", CodeErreur.NOT_ADMIN_UNAUTHORIZED);
    }

    @Test
    public void get_Evenement_By_Id_should_Return_EvenementDto_when_Evenement_Exists() {
        Long id = 1L;
        Evenement existingEvenement = EvenementDataTest.createTestEvenement();

        when(evenementRepository.findById(id)).thenReturn(Optional.of(existingEvenement));

        EvenementDto result = evenementService.getEvenementById(id);

        assertThat(result).isNotNull();
        assertThat(result.getEvenementId()).isEqualTo(1L);
    }

    @Test
    public void findAllEvenement_should_Return_Page_Of_EvenementDto() {
        Pageable pageable = Pageable.unpaged();
        List<Evenement> evenements = List.of(EvenementDataTest.createTestEvenement());
        Page<Evenement> evenementPage = new PageImpl<>(evenements);

        when(evenementRepository.findAll(
                (Specification<Evenement>) argThat(specification -> true),
                eq(pageable)
        )).thenReturn(evenementPage);

        Page<EvenementDto> result = evenementService.findAllEvenement(
                pageable, null, null, null, null, null, null, null, null);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    public void findAllEvenement_should_Return_Filtered_Results() {
        Pageable pageable = Pageable.unpaged();
        List<Evenement> evenements = List.of(EvenementDataTest.createTestEvenement());
        Page<Evenement> evenementPage = new PageImpl<>(evenements);

        when(evenementRepository.findAll(
                (Specification<Evenement>) argThat(specification -> true),
                eq(pageable)
        )).thenReturn(evenementPage);

        Page<EvenementDto> result = evenementService.findAllEvenement(
                pageable, 2L, StatutEvenement.En_attente, "Test Event", "Test Location",
                null, null, null, "1");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getInfoDto().getTitre()).isEqualTo("Test Event");
    }
}