package ukim.finki.file_vault.data_loader;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ukim.finki.file_vault.model.Role;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.repository.RoleRepository;
import ukim.finki.file_vault.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public DataInitializer(RoleRepository roleRepository,  PasswordEncoder passwordEncoder,  UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        createAndSaveRole("ROLE_USER");
        createAndSaveRole("ROLE_MODERATOR");
        createAndSaveRole("ROLE_ADMIN");
        createAndSaveRole("ROLE_UNCONFIRMED");
        createAndSaveAdmin(new User());
        createAndSaveUser(new User(), "user1");
        createAndSaveUser(new User(), "user2");
    }

    private void createAndSaveUser(User user, String username) {
        if (userRepository.findByUsername(username).isPresent()) return;
        user.setPassword(passwordEncoder.encode(username));
        user.setEnabled(true);
        user.setName(username);
        user.setEmail(username);
        user.setSurname(username);
        user.setUsername(username);
        user.getRoles().add(roleRepository.findByRoleName("ROLE_USER").orElseThrow(() -> new RuntimeException("temp exception for finding role")));
        userRepository.save(user);
    }

    private void createAndSaveAdmin(User admin) {
        if (userRepository.findByUsername("admin").isPresent()) return;
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setEnabled(true);
        admin.setName("admin");
        admin.setEmail("temp");
        admin.setSurname("admin");
        admin.setUsername("admin");
        admin.getRoles().add(roleRepository.findByRoleName("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("temp exception for finding role")));
        admin.getRoles().add(roleRepository.findByRoleName("ROLE_MODERATOR").orElseThrow(() -> new RuntimeException("temp exception for finding role")));
        admin.getRoles().add(roleRepository.findByRoleName("ROLE_USER").orElseThrow(() -> new RuntimeException("temp exception for finding role")));
        userRepository.save(admin);
    }

    private void createAndSaveRole(String roleName) {
        if (roleRepository.findByRoleName(roleName).isEmpty()) {
            Role role = new Role(roleName);
            roleRepository.save(role);
        }
    }
}
