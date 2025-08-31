package ukim.finki.file_vault.model.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userID) {
        super("User with ID " + userID + " not found");
    }
}
