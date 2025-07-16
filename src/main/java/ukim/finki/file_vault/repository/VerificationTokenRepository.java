package ukim.finki.file_vault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ukim.finki.file_vault.model.VerificationToken;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
}
