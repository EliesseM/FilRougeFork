package fr.schoolbyhiit.projetfilrouge.entity;


import fr.schoolbyhiit.projetfilrouge.enums.StatutSession;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity

@Getter
@Setter
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "statut_session")
    private StatutSession statutSession = StatutSession.Confirmer;

    private Long dureeSession;
    private LocalDateTime dateCreationSession;
    private LocalDateTime dateDebutSession;
    private LocalDateTime dateFinSession;

    private String titre;
    private String description;
    private String lieu;
    private String animateur;
    private Integer capaciteMax;


    @ManyToOne
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inscription> inscriptions = new ArrayList<>();

}
