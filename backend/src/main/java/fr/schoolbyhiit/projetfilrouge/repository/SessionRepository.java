package fr.schoolbyhiit.projetfilrouge.repository;


import fr.schoolbyhiit.projetfilrouge.entity.Evenement;
import fr.schoolbyhiit.projetfilrouge.entity.Session;
import fr.schoolbyhiit.projetfilrouge.enums.StatutSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long>, JpaSpecificationExecutor<Session> {

    Page<Session> findByTitre(String titre, Pageable page);

    Page<Session> findByAnimateur(String animateur, Pageable page);

    Page<Session> findByLieu(String lieu, Pageable page);

    Page<Session> findByCapaciteMax(Integer capaciteMax, Pageable page);

    Page<Session> findByStatutSession(StatutSession statutSession, Pageable page);

    Page<Session> findByDureeSession(Long dureeSession, Pageable page);

    Page<Session> findByDateCreationSession(LocalDateTime dateCreationSession, Pageable page);

    Page<Session> findByDateDebutSession(LocalDateTime dateDebutSession, Pageable page);

    Page<Session> findByDateFinSession(LocalDateTime dateFinSession, Pageable page);

    Page<Session> findByEvenement_EvenementId(Long evenementId,
                                              Pageable pageable);

    List<Session> findByEvenement(Evenement evenement);

}