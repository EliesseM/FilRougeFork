package fr.schoolbyhiit.projetfilrouge.config;

import fr.schoolbyhiit.projetfilrouge.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class AuthoritiesClaimAdapter implements Converter<Map<String, Object>, Map<String, Object>> {

    private final UtilisateurService utilisateurService;

    private final MappedJwtClaimSetConverter delegate =
            MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());

    @Override
    public Map<String, Object> convert(@NonNull Map<String, Object> claims) {
        Map<String, Object> convert = this.delegate.convert(claims);
        String utilisateurId = (String) convert.get("sub");
        try {
            var roles = utilisateurService.rolesByUtilisateurId(utilisateurId);
            convert.put("authorities", roles);
        } catch (Throwable ignored) {
        }
        return convert;
    }

}
