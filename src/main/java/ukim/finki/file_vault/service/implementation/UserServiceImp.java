package ukim.finki.file_vault.service.implementation;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.model.UserFile;
import ukim.finki.file_vault.model.exception.UserFileNotFoundException;
import ukim.finki.file_vault.model.exception.UserNotFoundException;
import ukim.finki.file_vault.model.exception.UserNotFoundInSessionException;
import ukim.finki.file_vault.repository.UserFileRepository;
import ukim.finki.file_vault.repository.UserRepository;
import ukim.finki.file_vault.service.SecurityUtils;
import ukim.finki.file_vault.service.UserService;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final UserFileRepository userFileRepository;

    public UserServiceImp(UserRepository userRepository,  UserFileRepository userFileRepository) {
        this.userRepository = userRepository;
        this.userFileRepository = userFileRepository;
    }

    @Override
    public void deleteUser(User user) {
        user.setVerificationToken(null);
        userRepository.delete(user);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUserByIdWithFiles(Long id) {
        return userRepository.findByIDWithFiles(id).orElse(null);
    }

    @Override
    public boolean userOwnsFile(Long fileID) {
        Long userId = Objects.requireNonNull(SecurityUtils.getCurrentUser()).getID();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundInSessionException::new);
        UserFile file = userFileRepository.findById(fileID).orElseThrow(() -> new UserFileNotFoundException(fileID));
        return user.getID().equals(file.getOwnerID());
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void addFileToUserByIDs(Long userID, Long fileID) throws UserNotFoundInSessionException, UserFileNotFoundException {
        User  user = userRepository.findByIDWithFiles(userID).orElseThrow(() -> new UserNotFoundException(userID));
        UserFile userFile = userFileRepository.findByFileIDWithUsersWithAccess(fileID).orElseThrow(() -> new UserFileNotFoundException(fileID));
        user.getFiles().add(userFile);
        userFile.getUsersWithAccess().add(user);
        userFileRepository.save(userFile);
    }

    @Override
    @Transactional
    public void removeFileFromUserByIDs(Long userID, Long fileID) throws UserNotFoundInSessionException, UserFileNotFoundException {
        User  user = userRepository.findByIDWithFiles(userID).orElseThrow(() -> new UserNotFoundException(userID));
        UserFile userFile = userFileRepository.findByFileIDWithUsersWithAccess(fileID).orElseThrow(() -> new UserFileNotFoundException(fileID));
        user.getFiles().remove(userFile);
        userFile.getUsersWithAccess().remove(user);
        userFileRepository.save(userFile);
    }
}
