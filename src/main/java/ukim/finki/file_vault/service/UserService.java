package ukim.finki.file_vault.service;
import ukim.finki.file_vault.model.User;

public interface UserService {
    void deleteUser(User user);
    void saveUser(User user);
    User getUserByIdWithFiles(Long id);
    boolean userOwnsFile(Long fileID);
}
