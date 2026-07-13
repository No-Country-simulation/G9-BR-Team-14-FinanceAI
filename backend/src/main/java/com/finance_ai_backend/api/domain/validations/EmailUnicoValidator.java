package com.finance_ai_backend.api.domain.validations;

import com.finance_ai_backend.api.domain.repositories.UsuarioRepositorio;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class EmailUnicoValidator implements ConstraintValidator<EmailUnico, String> {

    private final UsuarioRepositorio repositorio;

    public EmailUnicoValidator(UsuarioRepositorio repositorio) {
        this.repositorio= repositorio;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return !repositorio.existsByEmail(email);
    }
}
