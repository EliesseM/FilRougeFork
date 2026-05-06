package fr.schoolbyhiit.projetfilrouge.repository;

import fr.schoolbyhiit.projetfilrouge.entity.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, String>, JpaSpecificationExecutor<Utilisateur> {
    Page<Utilisateur> findByNom(String nom, Pageable pageable);

    Page<Utilisateur> findByPrenom(String prenom, Pageable pageable);

    Page<Utilisateur> findByEmail(String email, Pageable pageable);

    Page<Utilisateur> findByRole(String role, Pageable pageable);

    Page<Utilisateur> findByDateCreationUtilisateurs(LocalDateTime dateCreationUtilisateurs, Pageable pageable);

    Page<Utilisateur> findByPhotoProfil(String photoProfil, Pageable pageable);

}
