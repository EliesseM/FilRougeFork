package fr.schoolbyhiit.projetfilrouge.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum CodeErreur {


    // Erreur liée à Admin
    NOT_ADMIN_UNAUTHORIZED(2050, "Seuls l'organisateur ou un administrateur peuvent effectuer cette action.", HttpStatus.UNAUTHORIZED),
    USER_UNAUTHENTICATED(1005, "Utilisateur non authentifié.", HttpStatus.UNAUTHORIZED),

    // Erreur liée à Utilisateur
    USER_NOT_FOUND(1003, "Utilisateur non trouvé", HttpStatus.NOT_FOUND),

    USER_DATA_INCOMPLETE(2022, "Données de l'utilisateur incomplètes", HttpStatus.BAD_REQUEST),
    USER_SUBMITTED_ALREADY(2023, "L'utilisateur est déjà inscrit, impossible de s'inscrire à nouveau", HttpStatus.CONFLICT),
    USER_SUBMITTED_ALREADY_SOMETHING_ELSE(2024, "L'utilisateur est déjà inscrit à une session ou à un évènement, qui correspond aux dates renseignée", HttpStatus.CONFLICT),
    USER_NOT_ORGANISATEUR(3001, "L'utilisateur n'a pas le bon role pour ce type d'actions", HttpStatus.FORBIDDEN),
    USER_NOT_ADMIN(3002, "L'utilisateur doit être administrateur pour ce type d'actions", HttpStatus.FORBIDDEN),


    // Erreur liée à Événement
    EVENT_NOT_FOUND(1002, "Événement non trouvé", HttpStatus.NOT_FOUND),

    EVENT_DATES_INVALID(2002, "Dates de l'événement invalides", HttpStatus.BAD_REQUEST),
    EVENT_OVERLAP(2003, "Chevauchement de dates d'événement détecté", HttpStatus.CONFLICT),
    EVENT_DATA_INCOMPLETE(2004, "Données de l'événement incomplètes", HttpStatus.BAD_REQUEST),
    EVENT_USER_REQUIRED(2005, "L'utilisateur est obligatoire", HttpStatus.BAD_REQUEST),
    EVENT_INFO_REQUIRED(2006, "Les informations de l'événement sont obligatoires", HttpStatus.BAD_REQUEST),
    EVENT_TITLE_REQUIRED(2007, "Le titre de l'événement est obligatoire", HttpStatus.BAD_REQUEST),
    EVENT_LOCALISATION_REQUIRED(2008, "La localisation de l'événement est obligatoire", HttpStatus.BAD_REQUEST),
    EVENT_DURATION_INVALID(2009, "La durée de l'événement est invalide", HttpStatus.UNPROCESSABLE_ENTITY),
    EVENT_ALREADY_STARTED(2039, "L'événement a déjà commencé et ne peut plus être modifié.", HttpStatus.CONFLICT),
    EVENT_CANCELLED(2040, "Un événement annulé ne peut pas être modifié.", HttpStatus.CONFLICT),
    EVENT_DATE_ANTERIEURE_CREATION(2041, "La date de début de l'événement ne peut pas être antérieure à la date de création.", HttpStatus.CONFLICT),

    // Erreur liée à Session
    SESSION_NOT_FOUND(1004, "Session non trouvée", HttpStatus.NOT_FOUND),

    SESSION_DATES_INVALID(2010, "Dates de la session invalides", HttpStatus.BAD_REQUEST),
    SESSION_OVERLAP(2011, "Chevauchement de dates de session détecté", HttpStatus.CONFLICT),
    SESSION_DATA_INCOMPLETE(2012, "Données de la session incomplètes", HttpStatus.BAD_REQUEST),
    SESSION_EVENT_REQUIRED(2013, "L'événement est obligatoire", HttpStatus.BAD_REQUEST),
    SESSION_EVENT_CANCELLED(2014, "Impossible de créer une session sur un événement annulé", HttpStatus.BAD_REQUEST),
    SESSION_INFO_REQUIRED(2015, "Les informations de la session sont obligatoires", HttpStatus.BAD_REQUEST),
    SESSION_TITLE_REQUIRED(2016, "Le titre de la session est obligatoire", HttpStatus.BAD_REQUEST),
    SESSION_LOCATION_REQUIRED(2017, "Le lieu de la session est obligatoire", HttpStatus.BAD_REQUEST),
    SESSION_DURATION_INVALID(2018, "La durée de la session est invalide", HttpStatus.UNPROCESSABLE_ENTITY),
    SESSION_DATES_OUT_OF_EVENT(2019, "Les dates de la session ne sont pas comprises dans celles de l'événement", HttpStatus.BAD_REQUEST),
    SESSION_CAPACITY_INVALID(2020, "La capacité maximale de la session est invalide", HttpStatus.BAD_REQUEST),
    SESSION_UPDATE_EVENT_IMPOSSIBLE(2021, "Impossible de modifier l'événement d'une session", HttpStatus.CONFLICT),
    SESSION_DATES_NEEDED(2022, "Dates obligatoires", HttpStatus.BAD_REQUEST),
    SESSION_UPDATE_START_INVALID(2023, "Impossible de modifier une session dejà commencé", HttpStatus.BAD_REQUEST),
    SESSION_HAS_REGISTRATIONS(2024, "Impossible de supprimer une session avec des inscrits", HttpStatus.BAD_REQUEST),
    SESSION_ALREADY_CANCELLED(2025, "Session déjà annulée", HttpStatus.CONFLICT),
    SESSION_CANCEL_MIN_DELAY_NOT_RESPECTED(2026, "Annulation possible uniquement 24h avant", HttpStatus.BAD_REQUEST),
    SESSION_UPDATE_CANCELLED_INVALID(2027, "Impossible de modifier une session annuler", HttpStatus.BAD_REQUEST),


    // Erreur liée à l'inscription
    SUBMIT_NOT_FOUND(1005, "Inscription non trouvée", HttpStatus.NOT_FOUND),


    SUBMIT_CAPACITY_INVALID(2027, "Impossible de s'inscrire, la capacité maximale de la session est atteinte", HttpStatus.BAD_REQUEST),
    SUBMIT_START_INVALID(2028, "Impossible de s'inscrire, la session a dejà commencé", HttpStatus.BAD_REQUEST),
    SUBMIT_END_INVALID(2028, "Impossible de s'inscrire, la session est terminée", HttpStatus.BAD_REQUEST),
    SUBMIT_HOUR_INVALID(2029, "Impossible de s'inscrire a une session au meme horaire", HttpStatus.CONFLICT),
    SUBMIT_ALREADY_EXIST(2030, "Vous êtes dejà inscrit", HttpStatus.CONFLICT),
    SUBMIT_ALREADY_CANCEL(2031, "L'inscription est dejà annulé", HttpStatus.CONFLICT),
    SUBMIT_UPDATE_CANCEL_IMPOSSIBLE(2032, "Impossible de modifier une inscription annulé", HttpStatus.BAD_REQUEST),
    SUBMIT_SESSION_EVENT_CANCEL_IMPOSSIBLE(2033, "Impossible de s'inscrire à une session d'un événement annulé", HttpStatus.BAD_REQUEST),
    SUBMIT_CANCEL_MIN_DELAY_NOT_RESPECTED(2034, "L'inscription doit être annulée 24H avant le debut d'une session", HttpStatus.BAD_REQUEST),
    SUBMIT_UPDATE_STATUT_IMPOSSIBLE(2035, "Modification du statut interdite", HttpStatus.BAD_REQUEST),
    SUBMIT_UPDATE_END_INVALID(2036, "Impossible modifier l'inscription, la session est terminée", HttpStatus.BAD_REQUEST),
    SUBMIT_MIN_DELAY_NOT_RESPECTED(2037, "Inscription fermée 1h avant la session", HttpStatus.BAD_REQUEST),


    NOTIFICATION_NOT_FOUND(2038, "Notification non trouvée", HttpStatus.NOT_FOUND);

    private final int codeErreur;
    private final String erreur;
    private final HttpStatus httpStatus;

    public String getUrn() {
        String domaine = this.name().split("_")[0].toLowerCase();
        return String.format("urn:error:%s:%s", domaine, this.name().toLowerCase());
    }


}
