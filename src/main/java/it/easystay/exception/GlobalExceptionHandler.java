package it.easystay.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UsernameNotFoundException ex) {

        log.warn("Tentativo di accesso fallito: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", "Credenziali non valide");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(StanzaGiaOccupataException.class)
    public ResponseEntity<Map<String, String>> handleStanzaOccupata(StanzaGiaOccupataException ex) {
        log.warn("Conflitto prenotazione: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", "Conflitto Prenotazione");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleGenericRuntime(RuntimeException ex) {

        log.error("ERRORE CRITICO DI SISTEMA: ", ex);

        Map<String, String> error = new HashMap<>();
        error.put("error", "Errore interno al server");
        error.put("message", "Si è verificato un errore imprevisto.");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // CASO 1: JSON malformato o Enum non validi (HttpMessageNotReadableException)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonErrors(HttpMessageNotReadableException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        String message = "Formato JSON non valido o valore non permesso.";

        if (ex.getCause() instanceof InvalidFormatException ife) {
            if (ife.getTargetType().isEnum()) {
                String fieldName = ife.getPath().get(0).getFieldName();
                String invalidValue = ife.getValue().toString();
                String acceptedValues = Arrays.toString(ife.getTargetType().getEnumConstants());
                message = String.format("Il valore '%s' per il campo '%s' non è valido. Valori ammessi: %s",
                        invalidValue, fieldName, acceptedValues);
            }
        }

        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // CASO 2: Validazione fallita (es. @Email, @NotBlank)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(CustomDuplicateException.class)
    public ResponseEntity<Map<String, String>> handleCustomDuplicate(CustomDuplicateException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Conflitto dati");
        response.put("field", ex.getField());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, String> response = new HashMap<>();

        response.put("error", "Unauthorized");
        // Messaggio generico per motivi di sicurezza (non dire se è la mail o la password a mancare)
        response.put("message", "Email o password non corretti.");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException ex) {
        Map<String, String> response = new HashMap<>();

        response.put("error", "Risorsa non trovata");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleDatiErrati(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();

        response.put("error", "Dati errati");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}