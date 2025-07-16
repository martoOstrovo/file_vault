package ukim.finki.file_vault.service.implementation;

import org.springframework.stereotype.Service;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.model.VerificationToken;
import ukim.finki.file_vault.repository.VerificationTokenRepository;
import ukim.finki.file_vault.service.VerificationService;
import java.util.UUID;

@Service
public class VerificationServiceImp implements VerificationService {
    public final VerificationTokenRepository verificationTokenRepository;

    public VerificationServiceImp(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public VerificationToken createVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        return verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token).orElse(null);
    }

    public void deleteVerificationToken(VerificationToken verificationToken) {
        verificationTokenRepository.delete(verificationToken);
    }
}
