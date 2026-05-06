package fr.schoolbyhiit.projetfilrouge.config;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties("app")
public class AppConfig {

    @NotEmpty
    private String jwkSetUri;

    @Email
    @NotEmpty
    private String defaultAdminEmail;

}
