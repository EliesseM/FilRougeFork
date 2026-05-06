package fr.schoolbyhiit.projetfilrouge.repository;


import fr.schoolbyhiit.projetfilrouge.entity.DemandeDeRole;
import fr.schoolbyhiit.projetfilrouge.enums.StatutDemandeRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandeDeRoleRepository extends JpaRepository<DemandeDeRole, Long> {

    Page<DemandeDeRole> findByStatutDemandeRole(StatutDemandeRole statut, Pageable pageable);

    Page<DemandeDeRole> findByUtilisateur_UtilisateurId(String utilisateurId, Pageable pageable);

    long countByStatutDemandeRole(StatutDemandeRole statut);
}