package ukim.finki.file_vault.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import ukim.finki.file_vault.annotation.PasswordComplex;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PasswordComplexValidator implements ConstraintValidator<PasswordComplex, String> {
    private Pattern pattern;

    @Override
    public void initialize(PasswordComplex passwordComplex) {
        pattern = Pattern.compile(passwordComplex.value());
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (password == null) return true;
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
