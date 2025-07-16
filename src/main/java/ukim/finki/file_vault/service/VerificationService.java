package ukim.finki.file_vault.service;

import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.model.VerificationToken;

import java.time.LocalDateTime;
import java.util.List;

public interface VerificationService {
    VerificationToken createVerificationToken(User user);
    VerificationToken getVerificationToken(String token);
    void deleteVerificationToken(VerificationToken verificationToken);
    List<VerificationToken> getAllExpiredVerificationTokens(LocalDateTime currentDateTime);
    void saveVerificationToken(VerificationToken verificationToken);
}
