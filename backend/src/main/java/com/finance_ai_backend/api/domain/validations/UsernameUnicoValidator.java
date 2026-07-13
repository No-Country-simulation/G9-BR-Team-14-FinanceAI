package com.finance_ai_backend.api.domain.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import com.finance_ai_backend.api.domain.repositories.UsuarioRepositorio;

@Component
public class UsernameUnicoValidator implements ConstraintValidator<UsernameUnico, String> {

    private final UsuarioRepositorio repositorio;

    public UsernameUnicoValidator(UsuarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return !repositorio.existsByUsername(username);
    }
}