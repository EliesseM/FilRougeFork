package fr.schoolbyhiit.projetfilrouge.controller;

import fr.schoolbyhiit.projetfilrouge.dto.demandederole.DemandeDeRoleDto;
import fr.schoolbyhiit.projetfilrouge.enums.StatutDemandeRole;
import fr.schoolbyhiit.projetfilrouge.mapper.DemandeDeRoleMapper;
import fr.schoolbyhiit.projetfilrouge.repository.DemandeDeRoleRepository;
import fr.schoolbyhiit.projetfilrouge.service.DemandeDeRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/demande-role")
@RequiredArgsConstructor
public class DemandeDeRoleController {

    private final DemandeDeRoleService demandeDeRoleService;
    private final DemandeDeRoleRepository demandeDeRoleRepository;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public DemandeDeRoleDto createDemandeDeRole(@RequestBody DemandeDeRoleDto demandeDeRoleDto) {
        return demandeDeRoleService.createDemandeDeRole(demandeDeRoleDto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public DemandeDeRoleDto updateDemandeDeRole(@PathVariable Long id, @RequestBody DemandeDeRoleDto demandeDeRoleDto) {
        return demandeDeRoleService.updateDemande(id, demandeDeRoleDto);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDemandeDeRole(@PathVariable Long id) {
        demandeDeRoleService.deleteDemandeDeRole(id);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public List<DemandeDeRoleDto> findAllDemande() {
        return demandeDeRoleService.getAllDemande();
    }

    @GetMapping(value = "/statut", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public List<DemandeDeRoleDto> statutDemande(
            @RequestParam(required = false) StatutDemandeRole statutDemandeRole,
            Pageable pageable) {
        return demandeDeRoleService.getStatutDemandeRole(statutDemandeRole, pageable);
    }

    @GetMapping(value = "/pending-counts", produces = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Long> getPendingCounts() {
        long demandes = demandeDeRoleRepository.countByStatutDemandeRole(StatutDemandeRole.En_attente);
        return Map.of("demandes", demandes, "total", demandes);
    }

    @GetMapping(value = "/pending", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<DemandeDeRoleDto> getPendingDemandes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return demandeDeRoleRepository
                .findByStatutDemandeRole(StatutDemandeRole.En_attente, PageRequest.of(page, size))
                .map(DemandeDeRoleMapper::toDto);
    }

    @PatchMapping(value = "/{id}/approuver", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public DemandeDeRoleDto approuverDemande(@PathVariable Long id) {
        DemandeDeRoleDto dto = new DemandeDeRoleDto();
        dto.setStatutDemandeRole(StatutDemandeRole.Confirmer);
        return demandeDeRoleService.updateDemande(id, dto);
    }

    @PatchMapping(value = "/{id}/refuser", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public DemandeDeRoleDto refuserDemande(@PathVariable Long id) {
        DemandeDeRoleDto dto = new DemandeDeRoleDto();
        dto.setStatutDemandeRole(StatutDemandeRole.Refuser);
        return demandeDeRoleService.updateDemande(id, dto);
    }
}