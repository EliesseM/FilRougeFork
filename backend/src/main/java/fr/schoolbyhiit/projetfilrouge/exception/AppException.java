package fr.schoolbyhiit.projetfilrouge.exception;

import lombok.Getter;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class AppException extends ErrorResponseException {
    @Getter
    private final CodeErreur codeErreur;
    private final String message;

    public AppException(CodeErreur codeErreur, String message) {
        super(codeErreur.getHttpStatus(), asProblemDetail(codeErreur, message), null);
        this.codeErreur = codeErreur;
        this.message = message;
    }

    private static ProblemDetail asProblemDetail(CodeErreur codeErreur, String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(codeErreur.getHttpStatus());
        problemDetail.setType(URI.create(codeErreur.getUrn()));
        problemDetail.setTitle(String.format("[%d] %s", codeErreur.getCodeErreur(), codeErreur.getErreur()));
        problemDetail.setDetail(message);
        return problemDetail;
    }

    public Map<String, Object> toSimpleJson() {
        Map<String, Object> error = new HashMap<>();
        error.put("message", this.message);
        error.put("status", codeErreur.getHttpStatus().value());
        error.put("code", codeErreur.getCodeErreur());
        error.put("type", codeErreur.getUrn());
        return error;
    }
}
