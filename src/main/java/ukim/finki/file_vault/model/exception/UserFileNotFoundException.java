package ukim.finki.file_vault.model.exception;

public class UserFileNotFoundException extends RuntimeException {
    public UserFileNotFoundException(Long id) {
        super(String.format("Could not find file with id %d", id));
    }
}
