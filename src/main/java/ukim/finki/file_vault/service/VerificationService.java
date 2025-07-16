package ukim.finki.file_vault.service;

import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.model.VerificationToken;

public interface VerificationService {
    VerificationToken createVerificationToken(User user);
    VerificationToken getVerificationToken(String token);
    void deleteVerificationToken(VerificationToken verificationToken);
}
