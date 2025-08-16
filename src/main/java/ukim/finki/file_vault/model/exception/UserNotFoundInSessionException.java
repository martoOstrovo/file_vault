package ukim.finki.file_vault.model.exception;

public class UserNotFoundInSessionException extends RuntimeException {
    public UserNotFoundInSessionException() {
        super("There was an issue getting the user's information from the session.");
    }
}
