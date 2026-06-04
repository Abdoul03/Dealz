package com.doul.dealz.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ExceptionHandlerDealz {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErreur> handleEntityNotFound(EntityNotFoundException ex) {
        // Quand une entité n’est pas trouvée (par exemple un findById échoue)
        ApiErreur error = new ApiErreur(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErreur> handleBadCredentials(BadCredentialsException ex) {
        ApiErreur apiError = new ApiErreur(HttpStatus.UNAUTHORIZED, "Identifiant ou mot de passe incorrect.");
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErreur> handleValidation(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + " : " + err.getDefaultMessage())
                .toList();

        // Quand une validation de DTO échoue (annotation @Valid)
        ApiErreur error = new ApiErreur(HttpStatus.BAD_REQUEST, "Validation failed", messages);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErreur> handleAll(Exception ex) {
        ApiErreur error = new ApiErreur(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur : " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiErreur> handleNumberFormatException(NumberFormatException ex) {
        ApiErreur apiErreur = new ApiErreur(
                HttpStatus.UNAUTHORIZED,
                "Erreur d'authentification",
                List.of("Le jeton d'utilisateur (principal) n'est pas un ID valide.")
        );
        return new ResponseEntity<>(apiErreur, HttpStatus.UNAUTHORIZED); // 401
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErreur> handleConflictExceptions(RuntimeException ex) {
        // Intercepte les conflits avec des Rendez-vous
        ApiErreur apiErreur = new ApiErreur(HttpStatus.BAD_REQUEST, "Erreur de règle métier", List.of(ex.getMessage()));
        return new ResponseEntity<>(apiErreur, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiErreur> handleSecurityException(SecurityException ex) {
        // Utilisé pour les erreurs d'autorisation (l'utilisateur est connu, mais l'accès est refusé).
        ApiErreur apiErreur = new ApiErreur(HttpStatus.FORBIDDEN, "Accès Refusé (Autorisation)", List.of(ex.getMessage()));
        return new ResponseEntity<>(apiErreur, HttpStatus.FORBIDDEN); // 403
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErreur> handleIllegalArgumentException(IllegalArgumentException ex) {
        // Utilisé pour les erreurs de validation simple ou d'argument.
        ApiErreur apiErreur = new ApiErreur(HttpStatus.BAD_REQUEST, "Argument(s) Invalide(s)", List.of(ex.getMessage()));
        return new ResponseEntity<>(apiErreur, HttpStatus.BAD_REQUEST); // 400
    }
}
