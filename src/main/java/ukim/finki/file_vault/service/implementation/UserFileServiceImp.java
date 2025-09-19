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
import ukim.finki.file_vault.service.CryptoUtils;
import ukim.finki.file_vault.service.SecurityUtils;
import ukim.finki.file_vault.service.UserFileService;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserFileServiceImp implements UserFileService {
    private final UserRepository userRepository;
    private final UserFileRepository userFileRepository;
    private final static Pattern FILE_PATTERN = Pattern.compile("^[a-zA-Z0-9][^\\\\/:*?\"<>|]*\\.[a-zA-Z0-9]{1,5}$");
    private final CryptoUtils cryptoUtils;

    @Value("${file.storage.base-path}")
    private String basePath;

    @Value("${file.storage.backup-path}")
    private String backupPath;

    public UserFileServiceImp(UserRepository userRepository,
                              UserFileRepository userFileRepository,
                              @Value("${AES_MASTER_KEY_BASE64}") String aesKey,
                              @Value("${HMAC_SECRET_BASE64}") String hmacKey) {

        this.userRepository = userRepository;
        this.userFileRepository = userFileRepository;
        this.cryptoUtils = new CryptoUtils(aesKey, hmacKey);
    }

    @Override
    @Transactional
    public void uploadFile(MultipartFile file, String fileName) throws Exception {
        byte[] IV = cryptoUtils.generateIV();
        byte[] fileBytes = file.getBytes();
        byte[] encrypted = cryptoUtils.encrypt(fileBytes, IV);
        byte[] hmac = cryptoUtils.calculateHmac(encrypted);

        saveFileToDatabase(file, fileName, IV, hmac);

        User currentUser = SecurityUtils.getCurrentUser();
        assert currentUser != null;
        String fullPath =  basePath + "/" + currentUser.getUsername() + "/" + fileName;
        String fullBackupPath = backupPath + "/" + currentUser.getUsername() + "/" + fileName;

        Path filePathMain = Paths.get(fullPath);
        Path filePathBackup = Paths.get(fullBackupPath);
        Files.createDirectories(filePathMain.getParent());
        Files.createDirectories(filePathBackup.getParent());
        Files.write(filePathMain, encrypted);
        Files.write(filePathBackup, encrypted);
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

        String[] split = file.getFileName().split("\\.");
        String ext = split[split.length - 1];

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
        for(User user : file.getUsersWithAccess()) {
            user.getFiles().remove(file);
        }
        file.setUsersWithAccess(null);
        userFileRepository.delete(file);

        Files.deleteIfExists(Path.of(file.getFilePath()));
        Files.deleteIfExists(Paths.get(file.getBackupPath()));
    }

    @Override
    public byte[] safeReadFile(Path main, Path backup, UserFile userFile) throws Exception {
        byte[] iv = Base64.getDecoder().decode(userFile.getIvBase64());
        byte[] expectedHmac = Base64.getDecoder().decode(userFile.getHmacBase64());

        if(Files.exists(main)) {
            byte[] mainData = Files.readAllBytes(main);
            if(cryptoUtils.verifyMac(mainData, expectedHmac)) {
                return cryptoUtils.decrypt(mainData, iv);
            }
        }
        if (Files.exists(backup)) {
            byte[] backupData = Files.readAllBytes(backup);
            if(cryptoUtils.verifyMac(backupData, expectedHmac)) {
                Files.write(Path.of(userFile.getFilePath()), backupData);
                return cryptoUtils.decrypt(backupData, iv);
            }
        }
        deleteFileByID(userFile.getId());
        Files.deleteIfExists(main);
        Files.deleteIfExists(backup);
        throw new FileNotFoundException("The file was tampered with and removed for safety.");
    }

    private void saveFileToDatabase(MultipartFile file, String fileName, byte[] IV, byte[] hmac) throws FileNameAlreadyExistsException, IllegalFileNameException {
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
        userFile.setBackupPath(backupPath + "/" + currentUser.getUsername() + "/" + fileName);
        userFile.setOwnerID(currentUser.getID());
        userFile.setContentType(file.getContentType());
        userFile.setSize(file.getSize());
        userFile.setHmacBase64(Base64.getEncoder().encodeToString(hmac));
        userFile.setIvBase64(Base64.getEncoder().encodeToString(IV));
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
