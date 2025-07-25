package ukim.finki.file_vault.service.implementation;

import org.springframework.stereotype.Service;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.model.VerificationToken;
import ukim.finki.file_vault.repository.UserRepository;
import ukim.finki.file_vault.repository.VerificationTokenRepository;
import ukim.finki.file_vault.service.VerificationTokenService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VerificationTokenServiceImp implements VerificationTokenService {
    public final VerificationTokenRepository verificationTokenRepository;
    public final UserRepository userRepository;

    public VerificationTokenServiceImp(VerificationTokenRepository verificationTokenRepository, UserRepository userRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
    }

    public VerificationToken createVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token);
        verificationToken.setExpiryDate(LocalDateTime.now().plusSeconds(30));
        user.addVerificationToken(verificationToken);
        userRepository.save(user);
        return verificationToken;
    }

    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token).orElse(null);
    }

    public void deleteVerificationToken(VerificationToken verificationToken) {
        verificationTokenRepository.delete(verificationToken);
    }

    @Override
    public List<VerificationToken> getAllExpiredVerificationTokens(LocalDateTime currentDateTime) {
        return verificationTokenRepository.findAllByExpiryDateBefore(currentDateTime);
    }

    @Override
    public void saveVerificationToken(VerificationToken verificationToken) {
        verificationTokenRepository.save(verificationToken);
    }

}
