package fr.schoolbyhiit.projetfilrouge.service;

import fr.schoolbyhiit.projetfilrouge.dto.utilisateur.UtilisateurDto;
import fr.schoolbyhiit.projetfilrouge.entity.Utilisateur;
import fr.schoolbyhiit.projetfilrouge.mapper.UtilisateurMapper;
import fr.schoolbyhiit.projetfilrouge.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtilisateurService {


    private final UtilisateurRepository utilisateurRepository;


    public UtilisateurDto upsertByGoogleSub(String utilisateurId, String email, String nom, String prenom) {
        Optional<Utilisateur> existingUser = utilisateurRepository.findById(utilisateurId);
        Utilisateur utilisateur;
        if (existingUser.isPresent()) {
            utilisateur = existingUser.get();

            utilisateur.setEmail(email);
            utilisateur.setNom(nom);
            utilisateur.setPrenom(prenom);
        } else {
            utilisateur = new Utilisateur();
            utilisateur.setUtilisateurId(utilisateurId);
            utilisateur.setEmail(email);
            utilisateur.setNom(nom);
            utilisateur.setPrenom(prenom);
            utilisateur.setDateCreationUtilisateurs(LocalDateTime.now());

            if (utilisateurRepository.count() == 0) {
                utilisateur.setRole("ROLE_ADMIN");
            } else {
                utilisateur.setRole("ROLE_USER");
            }
        }
        utilisateurRepository.save(utilisateur);

        return UtilisateurMapper.toDto(utilisateur);
    }


    public UtilisateurDto getUtilisateurById(String utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId).orElseThrow(() -> new RuntimeException("Utilisateur not found"));
        return UtilisateurMapper.toDto(utilisateur);
    }


    public Page<UtilisateurDto> findAllUtilisateurs(String nom, String prenom, String email, String role, LocalDateTime dateCreationUtilisateurs, String photoProfil, Pageable page) {

        List<Specification<Utilisateur>> specs = new ArrayList<>();
        if (nom != null) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("nom")), "%" + nom.toLowerCase() + "%"));
        }

        if (prenom != null) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("prenom")), "%" + prenom.toLowerCase() + "%"));
        }
        if (email != null) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
        }
        if (role != null) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("role")), "%" + role.toLowerCase() + "%"));
        }
        if (dateCreationUtilisateurs != null) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("dateCreationUtilisateurs"), dateCreationUtilisateurs));
        }
        if (photoProfil != null) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("photoProfil")), "%" + photoProfil.toLowerCase() + "%"));
        }
        Specification<Utilisateur> spec = Specification.allOf(specs);

        return utilisateurRepository.findAll(spec, page)
                .map(UtilisateurMapper::toDto);
    }

    public List<String> rolesByUtilisateurId(String utilisateurId) {
        // TODO implement
        return List.of("USER");
    }
}
