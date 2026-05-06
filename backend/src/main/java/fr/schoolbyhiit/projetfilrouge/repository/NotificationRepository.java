package fr.schoolbyhiit.projetfilrouge.repository;


import fr.schoolbyhiit.projetfilrouge.entity.Evenement;
import fr.schoolbyhiit.projetfilrouge.entity.Notification;
import fr.schoolbyhiit.projetfilrouge.entity.Session;
import fr.schoolbyhiit.projetfilrouge.entity.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface NotificationRepository extends JpaRepository<Notification, Long> {


    Page<Notification> findByUtilisateur(Utilisateur utilisateur, Pageable pageable);

    Page<Notification> findByEvenement(Evenement evenement, Pageable pageable);

    Page<Notification> findBySession(Session session, Pageable pageable);

    Page<Notification> findByUtilisateurAndIsLu(Utilisateur utilisateur, boolean isLu, Pageable pageable);


}


