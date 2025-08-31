package ukim.finki.file_vault.model.exception;

public class IllegalFileNameException extends RuntimeException {
    public IllegalFileNameException(String fileName) {
        super("Illegal file name: " + fileName);
    }
}
