package ukim.finki.file_vault.service.implementation;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ukim.finki.file_vault.model.Role;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.model.UserFile;
import ukim.finki.file_vault.model.exception.NoAccessToFileException;
import ukim.finki.file_vault.model.exception.UserFileNotFoundException;
import ukim.finki.file_vault.model.exception.UserNotFoundException;
import ukim.finki.file_vault.model.exception.UserNotFoundInSessionException;
import ukim.finki.file_vault.repository.RoleRepository;
import ukim.finki.file_vault.repository.UserFileRepository;
import ukim.finki.file_vault.repository.UserRepository;
import ukim.finki.file_vault.service.SecurityUtils;
import ukim.finki.file_vault.service.UserService;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final UserFileRepository userFileRepository;
    private final RoleRepository roleRepository;

    public UserServiceImp(UserRepository userRepository,  UserFileRepository userFileRepository,  RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userFileRepository = userFileRepository;
        this.roleRepository = roleRepository;
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

    @Override
    public void userHasAccessToFile(UserFile userFile) {
        User currentUser = getUserByIdWithFiles(Objects.requireNonNull(SecurityUtils.getCurrentUser()).getID());
        if(!(currentUser.getFiles().contains(userFile))) throw new NoAccessToFileException();
    }

    @Override
    public List<User> getUsers() throws RoleNotFoundException {
        List<User> users = userRepository.findByAccountNonLocked(true);
        Role mod = roleRepository.findByRoleName("ROLE_MODERATOR").orElseThrow(() -> new RoleNotFoundException("ROLE_MODERATOR"));
        return users.stream()
                .filter(user -> !user.getRoles().contains(mod))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getMods() throws RoleNotFoundException {
        List<User> users = userRepository.findByAccountNonLocked(true);
        Role mod = roleRepository.findByRoleName("ROLE_MODERATOR").orElseThrow(() -> new RoleNotFoundException("ROLE_MODERATOR"));
        Role admin = roleRepository.findByRoleName("ROLE_ADMIN").orElseThrow(() -> new RoleNotFoundException("ROLE_ADMIN"));
        return users.stream()
                .filter(user -> user.getRoles().contains(mod) && !user.getRoles().contains(admin))
                .collect(Collectors.toList());
    }

    @Override
    public void giveMod(Long userID) throws UserNotFoundException, RoleNotFoundException {
        Role mod = roleRepository.findByRoleName("ROLE_MODERATOR").orElseThrow(() -> new RoleNotFoundException("ROLE_MODERATOR"));
        User user = userRepository.findById(userID).orElseThrow(() -> new UserNotFoundException(userID));
        user.getRoles().add(mod);
        userRepository.save(user);
    }

    @Override
    public void revokeMod(Long userID) throws UserNotFoundException, RoleNotFoundException {
        Role mod = roleRepository.findByRoleName("ROLE_MODERATOR").orElseThrow(() -> new RoleNotFoundException("ROLE_MODERATOR"));
        User user = userRepository.findById(userID).orElseThrow(() -> new UserNotFoundException(userID));
        user.getRoles().remove(mod);
        userRepository.save(user);
    }

    @Override
    public void lockAccountByID(Long userID) throws UserNotFoundException {
        User user = userRepository.findById(userID).orElseThrow(() -> new UserNotFoundException(userID));
        user.setAccountNonLocked(false);
        userRepository.save(user);
    }

    @Override
    public List<User> getLocked() throws RoleNotFoundException {
        Role admin = roleRepository.findByRoleName("ROLE_ADMIN").orElseThrow(() -> new RoleNotFoundException("ROLE_ADMIN"));
        Role mod = roleRepository.findByRoleName("ROLE_MODERATOR").orElseThrow(() -> new RoleNotFoundException("ROLE_MODERATOR"));
        List<User> users = userRepository.findByAccountNonLocked(false);

        User currentUser = SecurityUtils.getCurrentUser();

        assert currentUser != null;
        if (currentUser.getRoles().contains(admin)) {
            return users;
        } else {
            return users.stream().filter(user -> !user.getRoles().contains(mod)).collect(Collectors.toList());
        }
    }

    @Override
    public void unlockAccountById(Long userID) {
        User user = userRepository.findById(userID).orElseThrow(() -> new UserNotFoundException(userID));
        user.setAccountNonLocked(true);
        userRepository.save(user);
    }
}
