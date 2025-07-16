package ukim.finki.file_vault.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.model.VerificationToken;
import ukim.finki.file_vault.service.UserService;
import ukim.finki.file_vault.service.VerificationService;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AccountCleanupTask {
    private final VerificationService verificationService;
    private final UserService userService;

    public AccountCleanupTask(VerificationService verificationService, UserService userService) {
        this.verificationService = verificationService;
        this.userService = userService;
    }

    @Scheduled(cron = "0 0 3 */1 * 1-7")
    @Transactional
    public void deleteExpiredAccounts() {
        List<VerificationToken> expiredTokens = verificationService.getAllExpiredVerificationTokens(LocalDateTime.now());
        for (VerificationToken token : expiredTokens) {
            User user = token.getUser();
            token.setUser(null);
            verificationService.saveVerificationToken(token);
            if (user != null) userService.deleteUser(user);
            verificationService.deleteVerificationToken(token);
        }
    }
}
