package fr.schoolbyhiit.projetfilrouge.service;

import fr.schoolbyhiit.projetfilrouge.dto.demandederole.DemandeDeRoleDto;
import fr.schoolbyhiit.projetfilrouge.dto.utilisateur.UtilisateurDto;
import fr.schoolbyhiit.projetfilrouge.entity.DemandeDeRole;
import fr.schoolbyhiit.projetfilrouge.entity.Utilisateur;
import fr.schoolbyhiit.projetfilrouge.enums.StatutDemandeRole;
import fr.schoolbyhiit.projetfilrouge.mapper.DemandeDeRoleMapper;
import fr.schoolbyhiit.projetfilrouge.repository.DemandeDeRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DemandeDeRoleServiceTest {

    @Mock
    private DemandeDeRoleRepository demandeDeRoleRepository;

    @InjectMocks
    private DemandeDeRoleService demandeDeRoleService;

    private DemandeDeRoleDto demandeDeRoleDto;
    private DemandeDeRole demandeDeRole;
    private DemandeDeRole savedDemande;
    private UtilisateurDto utilisateurDto;

    @BeforeEach
    void setUp() {
        utilisateurDto = new UtilisateurDto();
        utilisateurDto.setUtilisateurId("1");
        utilisateurDto.setNom("Dupont");
        utilisateurDto.setPrenom("Jean");
        utilisateurDto.setEmail("jean.dupont@email.com");

        demandeDeRoleDto = new DemandeDeRoleDto();
        demandeDeRoleDto.setStatutDemandeRole(StatutDemandeRole.En_attente);
        demandeDeRoleDto.setUtilisateur(utilisateurDto);

        // Pour les entités on garde Utilisateur (entité JPA)
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUtilisateurId("1");

        demandeDeRole = new DemandeDeRole();
        demandeDeRole.setStatutDemandeRole(StatutDemandeRole.En_attente);
        demandeDeRole.setUtilisateur(utilisateur);

        savedDemande = new DemandeDeRole();
        savedDemande.setDemandeDeRoleId(1L);
        savedDemande.setStatutDemandeRole(StatutDemandeRole.En_attente);
        savedDemande.setUtilisateur(utilisateur);
    }

    @Test
    public void createDemandeDeRole_should_Return_DemandeDeRoleDto() {
        MockedStatic<DemandeDeRoleMapper> mockedMapper = mockStatic(DemandeDeRoleMapper.class);

        mockedMapper.when(() -> DemandeDeRoleMapper.toEntity(argThat(dto ->
                dto.getStatutDemandeRole() == StatutDemandeRole.En_attente &&
                        dto.getUtilisateur().getUtilisateurId().equals("1")
        ))).thenReturn(demandeDeRole);

        when(demandeDeRoleRepository.save(argThat(entity ->
                entity.getStatutDemandeRole() == StatutDemandeRole.En_attente &&
                        entity.getUtilisateur().getUtilisateurId().equals("1")
        ))).thenReturn(savedDemande);

        mockedMapper.when(() -> DemandeDeRoleMapper.toDto(argThat(entity ->
                entity.getDemandeDeRoleId().equals(1L) &&
                        entity.getStatutDemandeRole() == StatutDemandeRole.En_attente &&
                        entity.getUtilisateur().getUtilisateurId().equals("1")
        ))).thenReturn(demandeDeRoleDto);

        DemandeDeRoleDto result = demandeDeRoleService.createDemandeDeRole(demandeDeRoleDto);

        assertThat(result).isNotNull();
        assertThat(result.getStatutDemandeRole()).isEqualTo(StatutDemandeRole.En_attente);
        assertThat(result.getUtilisateur().getUtilisateurId()).isEqualTo("1");
        verify(demandeDeRoleRepository, times(1)).save(demandeDeRole);

        mockedMapper.close();
    }

    @Test
    public void updateDemande_should_Return_Updated_DemandeDeRoleDto() {
        MockedStatic<DemandeDeRoleMapper> mockedMapper = mockStatic(DemandeDeRoleMapper.class);
        Long id = 1L;

        DemandeDeRoleDto updatedDemandeDto = new DemandeDeRoleDto();
        updatedDemandeDto.setStatutDemandeRole(StatutDemandeRole.Confirmer);
        updatedDemandeDto.setUtilisateur(utilisateurDto);

        savedDemande.setStatutDemandeRole(StatutDemandeRole.Confirmer);

        when(demandeDeRoleRepository.findById(id)).thenReturn(Optional.of(demandeDeRole));

        when(demandeDeRoleRepository.save(argThat(entity ->
                entity.getStatutDemandeRole() == StatutDemandeRole.Confirmer &&
                        entity.getUtilisateur().getUtilisateurId().equals("1")
        ))).thenReturn(savedDemande);

        mockedMapper.when(() -> DemandeDeRoleMapper.toDto(argThat(entity ->
                entity.getStatutDemandeRole() == StatutDemandeRole.Confirmer &&
                        entity.getUtilisateur().getUtilisateurId().equals("1")
        ))).thenReturn(updatedDemandeDto);

        DemandeDeRoleDto result = demandeDeRoleService.updateDemande(id, updatedDemandeDto);

        assertThat(result).isNotNull();
        assertThat(result.getStatutDemandeRole()).isEqualTo(StatutDemandeRole.Confirmer);
        assertThat(result.getUtilisateur().getUtilisateurId()).isEqualTo("1");
        verify(demandeDeRoleRepository, times(1)).save(demandeDeRole);

        mockedMapper.close();
    }

    @Test
    public void deleteDemandeDeRole_should_Delete_When_Exists() {
        Long id = 1L;
        when(demandeDeRoleRepository.existsById(id)).thenReturn(true);
        doNothing().when(demandeDeRoleRepository).deleteById(id);

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                demandeDeRoleService.deleteDemandeDeRole(id)
        );
        verify(demandeDeRoleRepository, times(1)).deleteById(id);
    }

    @Test
    public void deleteDemandeDeRole_should_Throw_Exception_When_Not_Exists() {
        Long id = 1L;
        when(demandeDeRoleRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> demandeDeRoleService.deleteDemandeDeRole(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Demande de rôle non trouvée avec l'ID : " + id);
    }

    @Test
    public void getAllDemande_should_Return_List_Of_DemandeDeRoleDto() {
        MockedStatic<DemandeDeRoleMapper> mockedMapper = mockStatic(DemandeDeRoleMapper.class);
        List<DemandeDeRole> demandes = Collections.singletonList(demandeDeRole);
        when(demandeDeRoleRepository.findAll()).thenReturn(demandes);

        mockedMapper.when(() -> DemandeDeRoleMapper.toDto(argThat(entity ->
                entity.getStatutDemandeRole() == StatutDemandeRole.En_attente &&
                        entity.getUtilisateur().getUtilisateurId().equals("1")
        ))).thenReturn(demandeDeRoleDto);

        List<DemandeDeRoleDto> result = demandeDeRoleService.getAllDemande();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStatutDemandeRole()).isEqualTo(StatutDemandeRole.En_attente);
        assertThat(result.getFirst().getUtilisateur().getUtilisateurId()).isEqualTo("1");

        mockedMapper.close();
    }

    @Test
    public void getStatutDemandeRole_should_Return_List_Of_DemandeDeRoleDto() {
        MockedStatic<DemandeDeRoleMapper> mockedMapper = mockStatic(DemandeDeRoleMapper.class);
        StatutDemandeRole statut = StatutDemandeRole.En_attente;
        Pageable pageable = mock(Pageable.class);
        Page<DemandeDeRole> page = new PageImpl<>(Collections.singletonList(demandeDeRole));

        when(demandeDeRoleRepository.findByStatutDemandeRole(statut, pageable)).thenReturn(page);

        mockedMapper.when(() -> DemandeDeRoleMapper.toDto(argThat(entity ->
                entity.getStatutDemandeRole() == StatutDemandeRole.En_attente &&
                        entity.getUtilisateur().getUtilisateurId().equals("1")
        ))).thenReturn(demandeDeRoleDto);

        List<DemandeDeRoleDto> result = demandeDeRoleService.getStatutDemandeRole(statut, pageable);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStatutDemandeRole()).isEqualTo(StatutDemandeRole.En_attente);
        assertThat(result.getFirst().getUtilisateur().getUtilisateurId()).isEqualTo("1");

        mockedMapper.close();
    }

    @Test
    public void getDemandeUtilisateurById_should_Return_List_Of_DemandeDeRoleDto() {
        MockedStatic<DemandeDeRoleMapper> mockedMapper = mockStatic(DemandeDeRoleMapper.class);
        String utilisateurId = "1";
        Pageable pageable = mock(Pageable.class);
        Page<DemandeDeRole> page = new PageImpl<>(Collections.singletonList(demandeDeRole));

        when(demandeDeRoleRepository.findByUtilisateur_UtilisateurId(utilisateurId, pageable)).thenReturn(page);

        mockedMapper.when(() -> DemandeDeRoleMapper.toDto(argThat(entity ->
                entity.getStatutDemandeRole() == StatutDemandeRole.En_attente &&
                        entity.getUtilisateur().getUtilisateurId().equals("1")
        ))).thenReturn(demandeDeRoleDto);

        List<DemandeDeRoleDto> result = demandeDeRoleService.getDemandeUtilisateurById(utilisateurId, pageable);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStatutDemandeRole()).isEqualTo(StatutDemandeRole.En_attente);
        assertThat(result.getFirst().getUtilisateur().getUtilisateurId()).isEqualTo("1");

        mockedMapper.close();
    }
}