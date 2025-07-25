package ukim.finki.file_vault.service.implementation;

import org.springframework.stereotype.Service;
import ukim.finki.file_vault.model.TwoFactorToken;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.repository.TwoFactorTokenRepository;
import ukim.finki.file_vault.service.MailSenderService;
import ukim.finki.file_vault.service.SecurityUtils;
import ukim.finki.file_vault.service.TwoFactorTokenService;
import ukim.finki.file_vault.service.UserService;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class TwoFactorTokenServiceImp implements TwoFactorTokenService {
    private static final String SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new SecureRandom();
    private static final int TOKEN_LENGTH = 6;
    private final UserService userService;
    private final MailSenderService mailSender;
    private final TwoFactorTokenRepository twoFactorTokenRepository;

    public TwoFactorTokenServiceImp(UserService userService,  MailSenderService mailSender,  TwoFactorTokenRepository twoFactorTokenRepository) {
        this.userService = userService;
        this.mailSender = mailSender;
        this.twoFactorTokenRepository = twoFactorTokenRepository;
    }

    @Override
    public void sendTwoFactorTokenEmail(User user) {
        TwoFactorToken twoFactorToken = createTwoFactorToken(user);
        String mail = user.getEmail();
        String subject = "Two Factor Authentication Token";
        String body = "This code will expire in 5 minutes, please do not share it with anybody! \n" + twoFactorToken.getToken();
        mailSender.sendMail(mail, subject, body);
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
        if(twoFactorToken.getUser().getID().equals(currentUser.getID())) {
            deleteTwoFactorToken(twoFactorToken);
            return true;
        }
        return false;
    }

    private void deleteTwoFactorToken(TwoFactorToken twoFactorToken) {
        User user = twoFactorToken.getUser();
        user.setTwoFactorToken(null);
        userService.saveUser(user);
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
