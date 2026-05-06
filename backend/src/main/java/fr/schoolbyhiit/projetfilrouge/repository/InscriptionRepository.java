package fr.schoolbyhiit.projetfilrouge.repository;


import fr.schoolbyhiit.projetfilrouge.entity.Inscription;
import fr.schoolbyhiit.projetfilrouge.entity.Session;
import fr.schoolbyhiit.projetfilrouge.enums.StatutInscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long>, JpaSpecificationExecutor<Inscription> {

    List<Inscription> findBySession(Session session);

    long countBySession_SessionIdAndStatutInscription(Long sessionId, StatutInscription statutInscription);

    Optional<Inscription> findFirstBySession_SessionIdAndStatutInscriptionOrderByInscriptionIdAsc(
            Long sessionId,
            StatutInscription statut
    );

    Page<Inscription> findByUtilisateur_UtilisateurId(String utilisateurId, Pageable unpaged);

    boolean existsBySession(Session session);


    List<Inscription> findBySession_SessionId(Long sessionId);

    long countBySessionAndStatutInscription(Session existing, StatutInscription statutInscription);

    Page<Inscription> findByStatutInscription(StatutInscription statutInscription, Pageable pageable);

    long countByStatutInscription(StatutInscription statutInscription);
}
