package devSuivi.exception;

import org.springframework.boot.json.JsonParseException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GestionnaireExceptions extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail ressourceIntrouvable(Exception e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(status, "Corps de requête illisible");
        probleme.setProperty("cause", extraireCause(ex));
        return handleExceptionInternal(ex, probleme, headers, status, request);
    }

    private String extraireCause(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            String chemin = ife.getPath().stream()
                    .map(JacksonException.Reference::getPropertyName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));
            return "Valeur invalide '%s' pour le champ '%s' (type attendu : %s)"
                    .formatted(ife.getValue(), chemin, ife.getTargetType().getSimpleName());
        }
        if (cause instanceof UnrecognizedPropertyException upe) {
            String chemin = upe.getPath().stream()
                    .map(JacksonException.Reference::getPropertyName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));
            return "Champ inconnu '%s' (chemin : %s)".formatted(upe.getPropertyName(), chemin);
        }
        if (cause instanceof MismatchedInputException mie) {
            String chemin = mie.getPath().stream()
                    .map(JacksonException.Reference::getPropertyName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));
            return "Type incompatible pour le champ '%s' (type attendu : %s)"
                    .formatted(chemin, mie.getTargetType().getSimpleName());
        }
        if (cause instanceof JsonParseException) {
            return "JSON syntaxiquement invalide : " + cause.getMessage();
        }
        return ex.getMostSpecificCause().getMessage();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        List<Map<String, String>> erreurs = ex.getBindingResult().getFieldErrors().stream()
                .map(this::versMap)
                .toList();
        ProblemDetail pb = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Les données saisies sont invalides");
        pb.setProperty("champsEnErreur", erreurs);
        return ResponseEntity.badRequest().body(pb);
    }

    private Map<String, String> versMap(FieldError erreur) {
        return Map.of(
                "champ",   erreur.getField(),
                "message", Objects.requireNonNullElse(
                        erreur.getDefaultMessage(), "Valeur invalide"));
    }
}
