package ukim.finki.file_vault.service.implementation;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.model.UserFile;
import ukim.finki.file_vault.model.exception.FileNameAlreadyExistsException;
import ukim.finki.file_vault.model.exception.UserFileNotFoundException;
import ukim.finki.file_vault.model.exception.UserNotFoundInSessionException;
import ukim.finki.file_vault.repository.UserFileRepository;
import ukim.finki.file_vault.repository.UserRepository;
import ukim.finki.file_vault.service.SecurityUtils;
import ukim.finki.file_vault.service.UserFileService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserFileServiceImp implements UserFileService {
    private final UserRepository userRepository;
    private final UserFileRepository userFileRepository;
    @Value("${file.storage.base-path}")
    private String basePath;

    public UserFileServiceImp(UserRepository userRepository,  UserFileRepository userFileRepository) {
        this.userRepository = userRepository;
        this.userFileRepository = userFileRepository;
    }

    @Override
    @Transactional
    public void uploadFile(MultipartFile file, String fileName) throws IOException {
        byte[] fileBytes = file.getBytes();
        User currentUser = SecurityUtils.getCurrentUser();
        assert currentUser != null;
        String fullPath =  basePath + "/" + currentUser.getUsername() + "/" + fileName;
        Path filePath = Paths.get(fullPath);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, fileBytes);
        saveFileToDatabase(file, fileName);
    }

    @Override
    public UserFile getUserFileById(Long id) {
        return userFileRepository.findById(id).orElseThrow(() -> new UserFileNotFoundException(id));
    }

    @Override
    public void changeFileName(String newFileName, Long fileID) throws FileNameAlreadyExistsException, IOException , UserNotFoundInSessionException {
        UserFile file = getUserFileById(fileID);
        String ext = file.getFileName().split("\\.")[1];
        newFileName = newFileName + "." + ext;
        checkFileNameAvailability(newFileName);
        User currentUser = userRepository
                .findById(Objects.requireNonNull(SecurityUtils.getCurrentUser()).getID()).orElseThrow(UserNotFoundInSessionException::new);
        String newPathString = basePath + "/" + currentUser.getUsername() + "/" + newFileName;
        Path newFilePath = Paths.get(newPathString);
        Files.move(Paths.get(file.getFilePath()), newFilePath, StandardCopyOption.REPLACE_EXISTING);
        file.setFileName(newFileName);
        file.setFilePath(newPathString);
        userFileRepository.save(file);
    }

    private void saveFileToDatabase(MultipartFile file, String fileName) throws FileNameAlreadyExistsException {
        checkFileNameAvailability(fileName);

        Optional<User> currentUserOpt = userRepository.findByIDWithFiles(SecurityUtils.getCurrentUser().getID());
        User currentUser;
        if (currentUserOpt.isPresent()) {
            currentUser = currentUserOpt.get();
        } else {
            throw new UserNotFoundInSessionException();
        }

        UserFile userFile = new UserFile();
        userFile.setFileName(fileName);
        userFile.setFilePath(basePath + "/" + currentUser.getUsername() + "/" + fileName);
        userFile.setOwnerID(currentUser.getID());
        userFile.setContentType(file.getContentType());
        userFile.setSize(file.getSize());
        userFile.getUsersWithAccess().add(currentUser);
        currentUser.getFiles().add(userFile);
        userRepository.save(currentUser);
    }

    private void checkFileNameAvailability(String fileName) {
        Optional<UserFile> userFileOpt = userFileRepository.findByFileName(fileName);
        if (userFileOpt.isPresent()) {
            throw new FileNameAlreadyExistsException(fileName);
        }
    }
}
