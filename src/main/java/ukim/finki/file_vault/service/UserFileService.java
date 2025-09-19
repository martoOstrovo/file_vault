package ukim.finki.file_vault.service;

import org.springframework.web.multipart.MultipartFile;
import ukim.finki.file_vault.model.UserFile;
import java.io.IOException;
import java.nio.file.Path;


public interface UserFileService {
    void uploadFile(MultipartFile file, String fileName) throws Exception;
    UserFile getUserFileById(Long id);
    void changeFileName(String newFileName, Long fileID) throws IOException;
    void deleteFileByID(Long fileID) throws IOException;
    UserFile getUserFileByIDWithAccessList(Long id);
    byte[] safeReadFile(Path main, Path backup, UserFile userFile) throws Exception;
}