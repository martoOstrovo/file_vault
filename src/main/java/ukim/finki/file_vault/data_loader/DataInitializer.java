package ukim.finki.file_vault.data_loader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ukim.finki.file_vault.model.Role;
import ukim.finki.file_vault.repository.RoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        createAndSave("ROLE_USER");
        createAndSave("ROLE_MODERATOR");
        createAndSave("ROLE_ADMIN");
        createAndSave("ROLE_UNCONFIRMED");
    }

    private void createAndSave(String roleName) {
        if (roleRepository.findByRoleName(roleName).isEmpty()) {
            Role role = new Role(roleName);
            roleRepository.save(role);
        }
    }
}
