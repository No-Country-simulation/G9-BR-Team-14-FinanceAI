package com.finance_ai_backend.api.domain.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UsernameUnicoValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UsernameUnico {
    String message() default "Username já está em uso";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

