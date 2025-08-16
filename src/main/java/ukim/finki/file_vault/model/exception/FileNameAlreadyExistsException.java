package ukim.finki.file_vault.model.exception;

public class FileNameAlreadyExistsException extends RuntimeException {
    public FileNameAlreadyExistsException(String fileName) {
        super(String.format("Theres already a file named \" %s \" in the system.", fileName));
    }
}
