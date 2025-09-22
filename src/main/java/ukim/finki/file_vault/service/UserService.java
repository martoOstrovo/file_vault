package ukim.finki.file_vault.service;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.model.UserFile;
import ukim.finki.file_vault.model.exception.UserNotFoundException;

import javax.management.relation.RoleNotFoundException;
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
    List<User> getUsers() throws RoleNotFoundException;
    List<User> getMods() throws RoleNotFoundException;
    void giveMod(Long userID) throws RoleNotFoundException, UserNotFoundException;
    void revokeMod(Long userID) throws RoleNotFoundException, UserNotFoundException;
    void lockAccountByID(Long userID) throws UserNotFoundException;
    List<User> getLocked() throws RoleNotFoundException;
    void unlockAccountById(Long userID);
}
