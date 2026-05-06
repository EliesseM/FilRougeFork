package fr.schoolbyhiit.projetfilrouge.service;


import fr.schoolbyhiit.projetfilrouge.dto.demandederole.DemandeDeRoleDto;
import fr.schoolbyhiit.projetfilrouge.entity.DemandeDeRole;
import fr.schoolbyhiit.projetfilrouge.enums.StatutDemandeRole;
import fr.schoolbyhiit.projetfilrouge.mapper.DemandeDeRoleMapper;
import fr.schoolbyhiit.projetfilrouge.repository.DemandeDeRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemandeDeRoleService {

    private final DemandeDeRoleRepository demandeDeRoleRepository;

    public DemandeDeRoleDto createDemandeDeRole(DemandeDeRoleDto demandeDeRoleDto) {

        DemandeDeRole demandeDeRole = DemandeDeRoleMapper.toEntity(demandeDeRoleDto);

        demandeDeRole.setStatutDemandeRole(StatutDemandeRole.En_attente); // Statut par défaut avant d'être accepté
        DemandeDeRole savedDemande = demandeDeRoleRepository.save(demandeDeRole);

        return DemandeDeRoleMapper.toDto(savedDemande);
    }


    public DemandeDeRoleDto updateDemande(Long id, DemandeDeRoleDto demandeDto) {
        DemandeDeRole existingDemande = demandeDeRoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande de rôle non trouvée avec l'ID : " + id));

        if (demandeDto.getStatutDemandeRole() != null) {
            existingDemande.setStatutDemandeRole(demandeDto.getStatutDemandeRole());
        }

        DemandeDeRole updatedDemande = demandeDeRoleRepository.save(existingDemande);
        return DemandeDeRoleMapper.toDto(updatedDemande);
    }


    public void deleteDemandeDeRole(Long id) {
        if (!demandeDeRoleRepository.existsById(id)) {
            throw new RuntimeException("Demande de rôle non trouvée avec l'ID : " + id);
        }
        demandeDeRoleRepository.deleteById(id);
    }

    public List<DemandeDeRoleDto> getAllDemande() {
        return demandeDeRoleRepository.findAll()
                .stream()
                .map(DemandeDeRoleMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<DemandeDeRoleDto> getStatutDemandeRole(StatutDemandeRole statut, Pageable pageable) {
        return demandeDeRoleRepository.findByStatutDemandeRole(statut, pageable)
                .stream()
                .map(DemandeDeRoleMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<DemandeDeRoleDto> getDemandeUtilisateurById(String utilisateurId, Pageable pageable) {
        return demandeDeRoleRepository.findByUtilisateur_UtilisateurId(utilisateurId, pageable)
                .stream()
                .map(DemandeDeRoleMapper::toDto)
                .collect(Collectors.toList());
    }
}
