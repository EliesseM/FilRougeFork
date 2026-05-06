package fr.schoolbyhiit.projetfilrouge.entity;


import fr.schoolbyhiit.projetfilrouge.enums.StatutEvenement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity

@Getter
@Setter
public class Evenement {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evenementId;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_evenement", columnDefinition = "statut_evenement")
    private StatutEvenement statutEvenement;

    private String titre;
    private String description;
    private String localisation;

    private LocalDateTime dateCreationEvenement;
    private LocalDateTime dateDebutEvenement;
    private LocalDateTime dateFinEvenement;

    private Long dureeEvenement;

    @ManyToOne(optional = false)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

}
