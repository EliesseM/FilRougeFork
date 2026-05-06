package fr.schoolbyhiit.projetfilrouge.mapper;

import fr.schoolbyhiit.projetfilrouge.dto.demandederole.DemandeDeRoleDto;
import fr.schoolbyhiit.projetfilrouge.entity.DemandeDeRole;
import org.springframework.stereotype.Component;

@Component
public class DemandeDeRoleMapper {

    public static DemandeDeRole toEntity(DemandeDeRoleDto dto) {
        if (dto == null) return null;

        DemandeDeRole demande = new DemandeDeRole();
        demande.setDemandeDeRoleId(dto.getDemandeDeRoleId());
        demande.setStatutDemandeRole(dto.getStatutDemandeRole());
        demande.setUtilisateur(UtilisateurMapper.toEntity(dto.getUtilisateur()));
        return demande;
    }

    public static DemandeDeRoleDto toDto(DemandeDeRole entity) {
        if (entity == null) return null;

        DemandeDeRoleDto dto = new DemandeDeRoleDto();
        dto.setDemandeDeRoleId(entity.getDemandeDeRoleId());
        dto.setStatutDemandeRole(entity.getStatutDemandeRole());
        dto.setUtilisateur(UtilisateurMapper.toDto(entity.getUtilisateur()));
        return dto;
    }
}