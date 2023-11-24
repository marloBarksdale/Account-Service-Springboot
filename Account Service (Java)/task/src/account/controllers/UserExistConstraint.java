package account.controllers;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

@Constraint(validatedBy = UserExistConstraintValidator.class)

@Retention(RetentionPolicy.RUNTIME)
public @interface UserExistConstraint {
    String message() default "You are trying to enter data for user that does not exist.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}