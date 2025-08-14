package ukim.finki.file_vault.service.implementation;

import org.springframework.stereotype.Service;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.repository.UserRepository;
import ukim.finki.file_vault.service.UserService;

@Service
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void deleteUser(User user) {
        user.setVerificationToken(null);
        userRepository.delete(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUserByIdWithFiles(Long id) {
        return userRepository.findByIDWithFiles(id).orElse(null);
    }
}
