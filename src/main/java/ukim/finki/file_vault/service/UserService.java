package ukim.finki.file_vault.service;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.model.UserFile;

import java.util.List;

public interface UserService {
    void deleteUser(User user);
    void saveUser(User user);
    User getUserByIdWithFiles(Long id);
    boolean userOwnsFile(Long fileID);
    List<User> getAllUsers();
    void addFileToUserByIDs(Long userID, Long fileID);
    void removeFileFromUserByIDs(Long userID, Long fileID);
    void userHasAccessToFile(UserFile userFile);

}
