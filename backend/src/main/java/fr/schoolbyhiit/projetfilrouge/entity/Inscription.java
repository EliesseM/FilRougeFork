package fr.schoolbyhiit.projetfilrouge.entity;


import fr.schoolbyhiit.projetfilrouge.enums.StatutInscription;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inscriptionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_inscription", columnDefinition = "statut_inscription")
    private StatutInscription statutInscription;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session session;

}
