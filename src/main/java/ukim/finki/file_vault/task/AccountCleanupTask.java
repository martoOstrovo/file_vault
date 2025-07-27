package ukim.finki.file_vault.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ukim.finki.file_vault.model.TwoFactorToken;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.model.VerificationToken;
import ukim.finki.file_vault.service.TwoFactorTokenService;
import ukim.finki.file_vault.service.UserService;
import ukim.finki.file_vault.service.VerificationTokenService;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AccountCleanupTask {
    private final VerificationTokenService verificationService;
    private final UserService userService;
    private final TwoFactorTokenService twoFactorTokenService;

    public AccountCleanupTask(VerificationTokenService verificationService, UserService userService, TwoFactorTokenService twoFactorTokenService) {
        this.verificationService = verificationService;
        this.userService = userService;
        this.twoFactorTokenService = twoFactorTokenService;
    }

    @Scheduled(cron = "* * 12 * * 1-7")
    //@Scheduled(cron = "*/10 * * * * *")
    @Transactional
    public void deleteExpiredAccounts() {
        List<VerificationToken> expiredTokens = verificationService.getAllExpiredVerificationTokens(LocalDateTime.now());
        for (VerificationToken token : expiredTokens) {
            userService.deleteUser(token.getUser());
        }
    }

    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void delete2FATokens() {
        List<TwoFactorToken> expiredTokens = twoFactorTokenService.getExpiredTokens();
        User user;
        for (TwoFactorToken token : expiredTokens) {
            user = token.getUser();
            user.setTwoFactorToken(null);
            userService.saveUser(user);
        }
    }
}
