package ukim.finki.file_vault.service;

import org.springframework.web.multipart.MultipartFile;
import ukim.finki.file_vault.model.UserFile;

import java.io.IOException;

public interface UserFileService {
    void uploadFile(MultipartFile file, String fileName) throws IOException;
    UserFile getUserFileById(Long id);
}
