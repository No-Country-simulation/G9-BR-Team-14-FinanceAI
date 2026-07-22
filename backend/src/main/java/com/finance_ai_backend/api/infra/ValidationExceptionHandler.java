package com.finance_ai_backend.api.infra;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.finance_ai_backend.api.domain.exceptions.CredenciaisInvalidasException;
import com.finance_ai_backend.api.domain.exceptions.TokenInvalidoException;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<List<Map<String, String>>> handleHandlerMethodValidation(HandlerMethodValidationException ex) {
        List<Map<String, String>> erros = ex.getAllErrors().stream()
            .filter(error -> error instanceof FieldError)
            .map(error -> {
                FieldError fe = (FieldError) error;
                return Map.of(
                    "campo", fe.getField(),
                    "mensagem", fe.getDefaultMessage(),
                    "valorRejeitado", String.valueOf(fe.getRejectedValue())
                );
            })
            .toList();

        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<Map<String, String>> handleCredenciaisInvalidas(CredenciaisInvalidasException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleTokenInvalido(TokenInvalidoException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }
}
