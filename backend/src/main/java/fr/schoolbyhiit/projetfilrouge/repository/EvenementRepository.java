package fr.schoolbyhiit.projetfilrouge.repository;


import fr.schoolbyhiit.projetfilrouge.entity.Evenement;
import fr.schoolbyhiit.projetfilrouge.enums.StatutEvenement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface EvenementRepository extends JpaRepository<Evenement, Long>, JpaSpecificationExecutor<Evenement> {

    Page<Evenement> findByTitre(String titre, Pageable pageable);

    Page<Evenement> findByStatutEvenement(StatutEvenement statutEvenement, Pageable pageable);

    long countByStatutEvenement(StatutEvenement statutEvenement);

    Page<Evenement> findByLocalisation(String localisation, Pageable pageable);

    Page<Evenement> findByDateCreationEvenement(LocalDateTime dateCreationEvenement, Pageable pageable);

    Page<Evenement> findByDateDebutEvenement(LocalDateTime dateDebutEvenement, Pageable pageable);

    Page<Evenement> findByDateFinEvenement(LocalDateTime dateFinEvenement, Pageable pageable);

    Page<Evenement> findByUtilisateur_UtilisateurId(String utilisateurId, Pageable pageable);

    Page<Evenement> findByDureeEvenement(Long dureeEvenement, Pageable pageable);

    // Faire le findBy"Organisateur" soit ici, soit dans le repo utilisateur, en gros faire un findByPropriétaire d'evenement, a voir !


    // On s'appuie sur l'utilisateurId en plus des autres paramètres, pour laisser par exemple 2 cour de yoga dans la même ville au meme date mais pas pour le même orga
    List<Evenement> findByLocalisationAndTitreAndUtilisateur_UtilisateurIdAndStatutEvenementIn(
            String localisation,
            String titre,
            String utilisateurId,
            List<StatutEvenement> statuts
    );


}
