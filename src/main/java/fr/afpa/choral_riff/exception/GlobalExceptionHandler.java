// package fr.afpa.choral_riff.exception;

// import java.util.HashMap;
// import java.util.Map;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.AccessDeniedException;
// import org.springframework.web.bind.MethodArgumentNotValidException;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.RestControllerAdvice;


// @RestControllerAdvice
// public class GlobalExceptionHandler {

//   // Gestion des erreurs de validation (@Valid)
//   @ExceptionHandler(MethodArgumentNotValidException.class)
//   /**
//    * Gère les exceptions de validation et renvoie un message d'erreur approprié.
//    * @param ex
//    * @return
//    */
//   public ResponseEntity<Map<String, String>> handleValidationErrors(
//     MethodArgumentNotValidException ex
//   ) {
//     Map<String, String> errors = new HashMap<>();

//     ex
//       .getBindingResult()
//       .getFieldErrors()
//       .forEach(error ->
//         errors.put(error.getField(), error.getDefaultMessage())
//       );

//     return ResponseEntity.badRequest().body(errors);
//   }

//   @ExceptionHandler(AccessDeniedException.class)
//   /**
//    * Gère les exceptions d'accès refusé et renvoie un message d'erreur approprié.
//    * @param ex
//    * @return
//    */
//   public ResponseEntity<Map<String, String>> handleAccessDenied(
//     AccessDeniedException ex
//   ) {
//     Map<String, String> error = new HashMap<>();
//     error.put(
//       "message",
//       "Vous n'avez pas les droits nécessaires pour effectuer cette action."
//     );

//     return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
//   }

//   // Gestion des erreurs inattendues
//   /**
//    * Gère les exceptions globales et renvoie un message d'erreur générique.
//    * @param ex
//    * @return
//    */
//   @ExceptionHandler(Exception.class)
//   public ResponseEntity<Map<String, String>> handleGlobalError(Exception ex) {
//     Map<String, String> error = new HashMap<>();
//     error.put("message", "Une erreur interne est survenue.");

//     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//   }
// }
package fr.afpa.choral_riff.exception;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log =
    LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // Gestion des erreurs de validation (@Valid)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationErrors(
    MethodArgumentNotValidException ex
  ) {
    Map<String, String> errors = new HashMap<>();

    ex
      .getBindingResult()
      .getFieldErrors()
      .forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage())
      );

    return ResponseEntity.badRequest().body(errors);
  }

  // Gestion des accès refusés
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, String>> handleAccessDenied(
    AccessDeniedException ex
  ) {
    Map<String, String> error = new HashMap<>();
    error.put(
      "message",
      "Vous n'avez pas les droits nécessaires pour effectuer cette action."
    );

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }

  // Gestion des erreurs inattendues
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleGlobalError(Exception ex) {
    log.error("Erreur inattendue", ex);

    Map<String, String> error = new HashMap<>();
    error.put("message", "Une erreur interne est survenue.");

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}