package fr.schoolbyhiit.projetfilrouge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity

@Getter
@Setter
public class Utilisateur {

    @Id
    @Column(name = "utilisateur_id")
    private String utilisateurId;


    private String nom;
    private String prenom;
    private String email;
    private String role;
    private LocalDateTime dateCreationUtilisateurs;
    private String photoProfil;
}
