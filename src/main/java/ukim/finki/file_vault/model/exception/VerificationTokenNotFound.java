package ukim.finki.file_vault.model.exception;

public class VerificationTokenNotFound extends RuntimeException {
    public VerificationTokenNotFound(String className) {
        super("Verification Token Not Found in class: " + className);
    }
}
