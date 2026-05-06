package fr.schoolbyhiit.projetfilrouge.entity;

import fr.schoolbyhiit.projetfilrouge.enums.StatutDemandeRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity

@Getter
@Setter
public class DemandeDeRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long demandeDeRoleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_demande_de_role", columnDefinition = "statut_demande_de_role")

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private StatutDemandeRole statutDemandeRole;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

}