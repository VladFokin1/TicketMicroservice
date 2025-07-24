package org.example.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.model.Role;

public class ValidRoleValidator implements ConstraintValidator<ValidRole, Role> {
    @Override
    public boolean isValid(Role role, ConstraintValidatorContext context) {
        return role == Role.ROLE_CUSTOMER || role == Role.ROLE_ADMIN;
    }
}
