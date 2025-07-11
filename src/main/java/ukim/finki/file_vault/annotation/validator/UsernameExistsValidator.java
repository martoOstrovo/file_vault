package ukim.finki.file_vault.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import ukim.finki.file_vault.annotation.UniqueUsername;
import ukim.finki.file_vault.repository.UserRepository;

@Component
public class UsernameExistsValidator implements ConstraintValidator<UniqueUsername, String> {
    private final UserRepository userRepository;

    public UsernameExistsValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        if (username == null) return true;
        return userRepository.findByUsername(username).isEmpty();
    }
}
