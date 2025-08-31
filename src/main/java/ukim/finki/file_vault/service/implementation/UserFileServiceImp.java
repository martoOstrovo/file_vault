package ukim.finki.file_vault.service.implementation;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.model.UserFile;
import ukim.finki.file_vault.model.exception.FileNameAlreadyExistsException;
import ukim.finki.file_vault.model.exception.IllegalFileNameException;
import ukim.finki.file_vault.model.exception.UserFileNotFoundException;
import ukim.finki.file_vault.model.exception.UserNotFoundInSessionException;
import ukim.finki.file_vault.repository.UserFileRepository;
import ukim.finki.file_vault.repository.UserRepository;
import ukim.finki.file_vault.service.SecurityUtils;
import ukim.finki.file_vault.service.UserFileService;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserFileServiceImp implements UserFileService {
    private final UserRepository userRepository;
    private final UserFileRepository userFileRepository;
    private final static Pattern FILE_PATTERN = Pattern.compile("^[a-zA-Z0-9][^\\\\/:*?\"<>|]*\\.[a-zA-Z0-9]{1,5}$");
    @Value("${file.storage.base-path}")
    private String basePath;

    public UserFileServiceImp(UserRepository userRepository,  UserFileRepository userFileRepository) {
        this.userRepository = userRepository;
        this.userFileRepository = userFileRepository;
    }

    @Override
    @Transactional
    public void uploadFile(MultipartFile file, String fileName) throws IOException, FileNameAlreadyExistsException, IllegalFileNameException {
        saveFileToDatabase(file, fileName);

        byte[] fileBytes = file.getBytes();
        User currentUser = SecurityUtils.getCurrentUser();
        assert currentUser != null;
        String fullPath =  basePath + "/" + currentUser.getUsername() + "/" + fileName;
        Path filePath = Paths.get(fullPath);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, fileBytes);
    }

    @Override
    public UserFile getUserFileById(Long id) {
        return userFileRepository.findById(id).orElseThrow(() -> new UserFileNotFoundException(id));
    }

    public UserFile getUserFileByIDWithAccessList(Long id) {
        return userFileRepository.findByFileIDWithUsersWithAccess(id).orElseThrow(() -> new UserFileNotFoundException(id));
    }

    @Override
    public void changeFileName(String newFileName, Long fileID)
            throws FileNameAlreadyExistsException, IOException , UserNotFoundInSessionException, IllegalFileNameException {

        UserFile file = getUserFileById(fileID);
        String ext = file.getFileName().split("\\.")[1];
        newFileName = newFileName + "." + ext;
        checkFileNameAvailability(newFileName);
        checkFileNameLegality(newFileName);
        User currentUser = userRepository
                .findById(Objects.requireNonNull(SecurityUtils.getCurrentUser()).getID()).orElseThrow(UserNotFoundInSessionException::new);
        String newPathString = basePath + "/" + currentUser.getUsername() + "/" + newFileName;
        Path newFilePath = Paths.get(newPathString);
        Files.move(Paths.get(file.getFilePath()), newFilePath, StandardCopyOption.REPLACE_EXISTING);
        file.setFileName(newFileName);
        file.setFilePath(newPathString);
        userFileRepository.save(file);
    }

    @Override
    public void deleteFileByID(Long fileID) throws IOException {
        UserFile file = userFileRepository.findByFileIDWithUsersWithAccess(fileID).orElseThrow(FileNotFoundException::new);
        Files.delete(Path.of(file.getFilePath()));
        for(User user : file.getUsersWithAccess()) {
            user.getFiles().remove(file);
        }
        file.setUsersWithAccess(null);
        userFileRepository.delete(file);

    }

    private void saveFileToDatabase(MultipartFile file, String fileName) throws FileNameAlreadyExistsException, IllegalFileNameException {
        checkFileNameAvailability(fileName);
        checkFileNameLegality(fileName);

        Optional<User> currentUserOpt = userRepository.findByIDWithFiles(Objects.requireNonNull(SecurityUtils.getCurrentUser()).getID());
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

    private void checkFileNameLegality(String fileName) {
        Matcher matcher = FILE_PATTERN.matcher(fileName);
        if (!matcher.matches()) throw new IllegalFileNameException(fileName);
    }

    private void checkFileNameAvailability(String fileName) {
        Optional<UserFile> userFileOpt = userFileRepository.findByFileName(fileName);
        if (userFileOpt.isPresent()) {
            throw new FileNameAlreadyExistsException(fileName);
        }
    }
}
