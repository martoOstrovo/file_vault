package ukim.finki.file_vault.model.exception;

public class VerificationTokenNotFoundException extends RuntimeException {
    public VerificationTokenNotFoundException() {
        super("There was an issue finding the verification token, please register again.");
    }
}
