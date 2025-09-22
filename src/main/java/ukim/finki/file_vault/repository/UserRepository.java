package ukim.finki.file_vault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ukim.finki.file_vault.model.Role;
import ukim.finki.file_vault.model.User;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByAccountNonLocked(boolean locked);

    @Query("SELECT u FROM User u JOIN FETCH u.verificationToken")
    List<User> findAllWithVerificationToken();

    @Query("SELECT u FROM User u JOIN FETCH u.verificationToken WHERE u.ID = :ID")
    Optional<User> findUserWithVerificationTokenByID(@Param("ID") Long ID);

    @Query("SELECT u FROM User u JOIN FETCH u.files")
    List<User> findAllWithFiles();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.files WHERE u.ID = :ID")
    Optional<User> findByIDWithFiles(@Param("ID") Long id);

}
