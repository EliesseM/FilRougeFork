package fr.schoolbyhiit.projetfilrouge.data;

import fr.schoolbyhiit.projetfilrouge.entity.*;
import fr.schoolbyhiit.projetfilrouge.enums.StatutEvenement;
import fr.schoolbyhiit.projetfilrouge.enums.StatutSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDataTest {

    public static Utilisateur createTestUtilisateur() {
        Utilisateur utilisateur = new Utilisateur();

        utilisateur.setUtilisateurId("1");
        utilisateur.setRole("USER");
        return utilisateur;
    }

    public static Evenement createTestEvenement() {
        Evenement evenement = new Evenement();

        evenement.setEvenementId(1L);
        evenement.setTitre("Test Evenement");
        evenement.setDescription("Test evenement description");
        evenement.setLocalisation("Test evenement localisation");
        evenement.setStatutEvenement(StatutEvenement.Annuler);
        evenement.setDateDebutEvenement(LocalDateTime.now());
        evenement.setDateFinEvenement(LocalDateTime.now().plusHours(2));
        return evenement;
    }

    public static Session createTestSession() {
        Session session = new Session();

        session.setSessionId(1L);
        session.setTitre("Test Session");
        session.setDescription("Test Session description");
        session.setLieu("Test Session lieu");
        session.setAnimateur("Test Session animateur");
        session.setCapaciteMax(10);
        session.setStatutSession(StatutSession.Confirmer);
        session.setDateDebutSession(LocalDateTime.now());
        session.setDateFinSession(LocalDateTime.now().plusHours(2));
        return session;
    }

    public static Inscription createTestInscription() {
        Inscription inscription = new Inscription();

        inscription.setInscriptionId(1L);
        inscription.setUtilisateur(createTestUtilisateur());
        inscription.setSession(createTestSession());
        return inscription;
    }

    public static Notification createTestNotification() {
        Notification notification = new Notification();

        notification.setNotificationId(1L);
        notification.setTitre("Test Notification");
        notification.setDescription("Test Notification description");
        notification.setDate(LocalDateTime.now());

        notification.setUtilisateur(createTestUtilisateur());
        notification.setSession(createTestSession());
        notification.setEvenement(createTestEvenement());
        return notification;
    }

    public static List<Inscription> createTestInscriptionList() {
        List<Inscription> inscriptionList = new ArrayList<>();
        inscriptionList.add(createTestInscription());
        return inscriptionList;

    }
}
