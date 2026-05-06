package fr.schoolbyhiit.projetfilrouge.controller;


import fr.schoolbyhiit.projetfilrouge.dto.inscription.InscriptionDto;
import fr.schoolbyhiit.projetfilrouge.enums.StatutInscription;
import fr.schoolbyhiit.projetfilrouge.service.InscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/inscriptions")
@RequiredArgsConstructor
public class InscriptionController {

    private final InscriptionService inscriptionService;

    @GetMapping
    public Page<InscriptionDto> getAllInscriptions(@RequestParam(name = "utilisateurId", required = false) String utilisateurId,
                                                   @RequestParam(name = "sessionId", required = false) Long sessionId,
                                                   @RequestParam(name = "statutInscription", required = false) StatutInscription statutInscription,
                                                   Pageable page) {

        return inscriptionService.findAllInscription(sessionId, utilisateurId, statutInscription, page);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public InscriptionDto getInscriptionById(@PathVariable Long id) {

        return inscriptionService.findInscriptionById(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public InscriptionDto createInscription(@RequestBody InscriptionDto inscriptionDto) {
        return inscriptionService.createInscription(inscriptionDto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public InscriptionDto updateInscription(@PathVariable Long id, @RequestBody InscriptionDto inscriptionDto) {
        inscriptionDto.setInscriptionId(id);
        return inscriptionService.updateInscription(inscriptionDto);
    }

    @PatchMapping("/annuler/{id}")
    public InscriptionDto annuler(@PathVariable Long id) {
        return inscriptionService.annulerInscription(id);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteInscription(@PathVariable Long id, String utilisateurId) {
        inscriptionService.deleteById(id, utilisateurId);
    }
}