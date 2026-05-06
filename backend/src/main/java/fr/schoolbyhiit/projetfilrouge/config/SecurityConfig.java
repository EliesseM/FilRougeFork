package fr.schoolbyhiit.projetfilrouge.config;

import fr.schoolbyhiit.projetfilrouge.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.web.cors.CorsConfiguration.ALL;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final AppConfig appConfig;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        var corsConfiguration = openCorsConfiguration();

        http
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/ws-notifications/**", "/login/oauth2/code/google", "utilisateurs/register").permitAll()
                                .anyRequest().permitAll()
                )

                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/home-page", true) // redirige après login
                )

                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(request -> corsConfiguration))
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                );
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder(UtilisateurService utilisateurService) {
        NimbusJwtDecoder jwtDecoder =
                NimbusJwtDecoder.withJwkSetUri(appConfig.getJwkSetUri()).build();
        jwtDecoder.setClaimSetConverter(new AuthoritiesClaimAdapter(utilisateurService));
        return jwtDecoder;
    }

    private CorsConfiguration openCorsConfiguration() {
        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader(ALL);
        corsConfiguration.addAllowedOrigin(ALL);
        corsConfiguration.addAllowedMethod(ALL);
        return corsConfiguration;
    }
}
