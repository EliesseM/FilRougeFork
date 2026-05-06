package fr.schoolbyhiit.projetfilrouge.mapper;

import fr.schoolbyhiit.projetfilrouge.config.DureeConfig;
import fr.schoolbyhiit.projetfilrouge.dto.session.SessionDateDto;
import fr.schoolbyhiit.projetfilrouge.dto.session.SessionDto;
import fr.schoolbyhiit.projetfilrouge.dto.session.SessionInfosDto;
import fr.schoolbyhiit.projetfilrouge.dto.session.SessionStatutDto;
import fr.schoolbyhiit.projetfilrouge.entity.Session;


public class SessionMapper {

    public static Session toEntity(SessionDto dto) {

        if (dto == null) return null;

        Session session = new Session();

        session.setSessionId(dto.getSessionId());
        session.setEvenement(dto.getEvenement());

        if (dto.getDates() != null) {
            session.setDateCreationSession(dto.getDates().getDateCreationSession());
            session.setDateDebutSession(dto.getDates().getDateDebutSession());
            session.setDateFinSession(dto.getDates().getDateFinSession());
        }

        if (dto.getInfos() != null) {
            session.setTitre(dto.getInfos().getTitre());
            session.setDescription(dto.getInfos().getDescription());
            session.setLieu(dto.getInfos().getLieu());
            session.setCapaciteMax(dto.getInfos().getCapaciteMax());
            session.setAnimateur(dto.getInfos().getAnimateur());
        }

        if (dto.getStatut() != null) {
            session.setStatutSession(dto.getStatut().getStatutSession());
        }

        return session;
    }

    public static SessionDto toDto(Session session) {

        if (session == null) return null;

        SessionDto dto = new SessionDto();

        dto.setSessionId(session.getSessionId());
        dto.setEvenement(session.getEvenement());

        SessionDateDto dates = new SessionDateDto();
        dates.setDateCreationSession(session.getDateCreationSession());
        dates.setDateDebutSession(session.getDateDebutSession());
        dates.setDateFinSession(session.getDateFinSession());

        // durée logique
        dates.setDureeSession(session.getDureeSession());

        //durée formatée
        dates.setDureeFormatee(
                DureeConfig.formaterDuree(session.getDureeSession())
        );

        SessionInfosDto infos = new SessionInfosDto();
        infos.setTitre(session.getTitre());
        infos.setDescription(session.getDescription());
        infos.setLieu(session.getLieu());
        infos.setCapaciteMax(session.getCapaciteMax());
        infos.setAnimateur(session.getAnimateur());

        SessionStatutDto statut = new SessionStatutDto();
        statut.setStatutSession(session.getStatutSession());

        dto.setDates(dates);
        dto.setInfos(infos);
        dto.setStatut(statut);

        return dto;
    }

}