package ukim.finki.file_vault.service;

import ukim.finki.file_vault.model.User;

public interface TwoFactorTokenService {
    void sendTwoFactorTokenEmail(User user);
    boolean verifyTwoFactorToken(String token);
}
