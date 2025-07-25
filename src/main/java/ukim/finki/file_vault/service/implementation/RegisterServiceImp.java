package ukim.finki.file_vault.service.implementation;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ukim.finki.file_vault.model.Role;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.model.UserDTO;
import ukim.finki.file_vault.model.VerificationToken;
import ukim.finki.file_vault.repository.RoleRepository;
import ukim.finki.file_vault.repository.UserRepository;
import ukim.finki.file_vault.service.MailSenderService;
import ukim.finki.file_vault.service.RegisterService;
import ukim.finki.file_vault.service.VerificationTokenService;
import java.time.LocalDateTime;

//TODO add good exception handling and custom exceptions
@Service
public class RegisterServiceImp implements RegisterService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final MailSenderService mailSenderService;
    private final VerificationTokenService verificationService;

    public RegisterServiceImp(UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              RoleRepository roleRepository,
                              MailSenderService mailSender,
                              VerificationTokenService verificationService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderService = mailSender;
        this.verificationService = verificationService;
    }

    public void registerUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        Role role = roleRepository.findByRoleName("ROLE_USER").orElseThrow(() -> new RuntimeException("Default Role Not Found"));
        user.getRoles().add(role);
        userRepository.save(user);
        sendVerificationEmail(user);
    }

    private void sendVerificationEmail(User user) {
        VerificationToken verificationToken = verificationService.createVerificationToken(user);
        String body = "please visit the following link to activate your account: http://localhost:8080/verify?token=" + verificationToken.getToken();
        mailSenderService.sendMail(user.getEmail(), "Account Verification Token", body);
    }

    public void confirmAccount(String token) {
        VerificationToken verificationToken = verificationService.getVerificationToken(token);
        if (verificationToken == null) throw new RuntimeException("Verification Token Not Found");
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Verification Token Expired");
        User user = verificationToken.getUser();
        user.setVerificationToken(null);
        user.setEnabled(true);
//        verificationService.deleteVerificationToken(verificationToken);
        userRepository.save(user);
    }

}
