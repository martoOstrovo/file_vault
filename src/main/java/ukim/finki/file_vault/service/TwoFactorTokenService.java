package ukim.finki.file_vault.service;

import ukim.finki.file_vault.model.TwoFactorToken;
import ukim.finki.file_vault.model.User;

import java.util.List;

public interface TwoFactorTokenService {
    void sendTwoFactorTokenEmail(User user);
    boolean verifyTwoFactorToken(String token);
    List<TwoFactorToken> getExpiredTokens();
}
