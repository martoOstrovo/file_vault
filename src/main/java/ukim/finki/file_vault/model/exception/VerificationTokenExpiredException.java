package ukim.finki.file_vault.model.exception;

public class VerificationTokenExpiredException extends RuntimeException {
    public VerificationTokenExpiredException(String className) {
        super("Verification Token Expired in class: " + className);
    }
}
