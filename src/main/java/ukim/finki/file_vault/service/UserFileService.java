package ukim.finki.file_vault.service;

import org.springframework.web.multipart.MultipartFile;
import ukim.finki.file_vault.model.UserFile;

public interface UserFileService {
    void uploadFile(MultipartFile file, String fileName);
    UserFile getUserFileById(Long id);
}
