package ukim.finki.file_vault.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ukim.finki.file_vault.annotation.validator.PasswordComplexValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordComplexValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordComplex {
    String value() default "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$";
    String message() default "Password must contain at least one lower and uppercase letter, one number and one special character.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
