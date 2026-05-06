package fr.schoolbyhiit.projetfilrouge.config;

import org.springframework.stereotype.Component;

@Component
public class DureeConfig {

    public static String formaterDuree(Long totalHeures) {

        if (totalHeures == null) return null;

        long jours = totalHeures / 24;
        long heures = totalHeures % 24;

        if (jours > 0 && heures > 0) {
            return jours + "j " + heures + "h";
        } else if (jours > 0) {
            return jours + "j";
        } else {
            return heures + "h";
        }
    }
}
