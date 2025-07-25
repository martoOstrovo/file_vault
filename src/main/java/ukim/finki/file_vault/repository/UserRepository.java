package ukim.finki.file_vault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ukim.finki.file_vault.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u JOIN FETCH u.verificationToken")
    List<User> findAllWithVerificationToken();

    @Query("SELECT u FROM User u JOIN FETCH u.verificationToken WHERE u.ID = :ID")
    Optional<User> findUserWithVerificationTokenByID(@Param("ID") Long ID);
}
