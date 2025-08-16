package ukim.finki.file_vault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ukim.finki.file_vault.model.UserFile;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserFileRepository extends JpaRepository<UserFile,Long> {
    List<UserFile> findAllByOwnerID(Long id);
    List<UserFile> findAllByFileNameStartingWith(String fileName);
    Optional<UserFile> findByFileName(String fileName);
}
