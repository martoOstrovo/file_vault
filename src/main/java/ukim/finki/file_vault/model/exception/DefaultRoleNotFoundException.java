package ukim.finki.file_vault.model.exception;

public class DefaultRoleNotFoundException extends RuntimeException {
    public DefaultRoleNotFoundException(String className) {
        super("Default role not found in class: " + className);
    }
}
