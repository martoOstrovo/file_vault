package ukim.finki.file_vault.model.exception;

public class NoAccessToFileException extends RuntimeException {
    public NoAccessToFileException() {
        super("No access to file");
    }
}
