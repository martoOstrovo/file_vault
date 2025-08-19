package ukim.finki.file_vault.service.implementation;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ukim.finki.file_vault.model.Role;
import ukim.finki.file_vault.model.TwoFactorToken;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.repository.RoleRepository;
import ukim.finki.file_vault.repository.TwoFactorTokenRepository;
import ukim.finki.file_vault.security.CustomUserDetails;
import ukim.finki.file_vault.service.MailSenderService;
import ukim.finki.file_vault.service.SecurityUtils;
import ukim.finki.file_vault.service.TwoFactorTokenService;
import ukim.finki.file_vault.service.UserService;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TwoFactorTokenServiceImp implements TwoFactorTokenService {
    private static final String SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new SecureRandom();
    private static final int TOKEN_LENGTH = 6;
    private final UserService userService;
    private final MailSenderService mailSender;
    private final TwoFactorTokenRepository twoFactorTokenRepository;
    private final RoleRepository roleRepository;

    public TwoFactorTokenServiceImp(UserService userService,
                                    MailSenderService mailSender,
                                    TwoFactorTokenRepository twoFactorTokenRepository,
                                    RoleRepository roleRepository) {

        this.userService = userService;
        this.mailSender = mailSender;
        this.twoFactorTokenRepository = twoFactorTokenRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void sendTwoFactorTokenEmail(User user) {
        TwoFactorToken twoFactorToken = createTwoFactorToken(user);
//        String mail = user.getEmail();
//        String subject = "Two Factor Authentication Token";
//        String body = "This code will expire in 5 minutes, please do not share it with anybody! \n" + twoFactorToken.getToken();
//        mailSender.sendMail(mail, subject, body);
    }

    @Override
    public boolean verifyTwoFactorToken(String token) {
        Optional<TwoFactorToken> twoFactorTokenOptional = twoFactorTokenRepository.findByToken(token);
        if (twoFactorTokenOptional.isEmpty()) {
            return false;
        }
        TwoFactorToken twoFactorToken = twoFactorTokenOptional.get();
        User currentUser = SecurityUtils.getCurrentUser();

        assert currentUser != null;
        if(twoFactorToken.getUser().equals(currentUser)) {
            setNewAuth(currentUser);
            return true;
        }
        return false;
    }

    @Override
    public List<TwoFactorToken> getExpiredTokens() {
        return twoFactorTokenRepository.findAllByExpiryDateBefore(LocalDateTime.now());
    }

    private void setNewAuth(User currentUser) {
        CustomUserDetails customUserDetails = new CustomUserDetails(currentUser);
        Role roleUnconfirmed = roleRepository.findByRoleName("ROLE_UNCONFIRMED").orElseThrow(() ->
                new RuntimeException("Role Unconfirmed not found in TwoFactorTokenServiceImp"));

        currentUser.getRoles().remove(roleUnconfirmed);
        currentUser.setTwoFactorToken(null);
        userService.saveUser(currentUser);

        List<GrantedAuthority> updatedAuthorities = customUserDetails.getAuthorities()
                .stream()
                .filter(authority -> !authority.getAuthority().equals(roleUnconfirmed.getRoleName()))
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(customUserDetails,
                customUserDetails.getPassword(),
                updatedAuthorities);

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    private TwoFactorToken createTwoFactorToken(User user) {
        TwoFactorToken twoFactorToken = new TwoFactorToken();
        twoFactorToken.setToken(generateRandomString());
        twoFactorToken.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        user.addTwoFactorToken(twoFactorToken);
        userService.saveUser(user);
        return twoFactorToken;
    }

    private String generateRandomString() {
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            int index = RANDOM.nextInt(SYMBOLS.length());
            token.append(SYMBOLS.charAt(index));
        }
        return token.toString();
    }
}
