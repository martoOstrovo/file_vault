package ukim.finki.file_vault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ukim.finki.file_vault.model.TwoFactorToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TwoFactorTokenRepository extends JpaRepository<TwoFactorToken,Long> {
    Optional<TwoFactorToken> findByToken(String token);
    List<TwoFactorToken> findAllByExpiryDateBefore(LocalDateTime currentDateTime);
}
