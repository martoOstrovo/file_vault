package ukim.finki.file_vault.model.exception;

public class VerificationTokenExpiredException extends RuntimeException {
    public VerificationTokenExpiredException() {
        super("Your verification token has expired, please register your account again.");
    }
}
