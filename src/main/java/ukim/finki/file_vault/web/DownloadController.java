package ukim.finki.file_vault.web;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ukim.finki.file_vault.model.UserFile;
import ukim.finki.file_vault.model.exception.NoAccessToFileException;
import ukim.finki.file_vault.model.exception.UserFileNotFoundException;
import ukim.finki.file_vault.service.UserFileService;
import ukim.finki.file_vault.service.UserService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Controller
@RequestMapping("/file-download")
public class DownloadController {
    private final UserFileService userFileService;
    private final UserService userService;

    public DownloadController(UserFileService userFileService,  UserService userService) {
        this.userFileService = userFileService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<InputStreamResource> processDownload(@RequestParam Long fileID)
            throws Exception {

        UserFile userFile = userFileService.getUserFileById(fileID);
        userService.userHasAccessToFile(userFile);

        Path filePath = Paths.get(userFile.getFilePath());
        Path backupPath = Paths.get(userFile.getBackupPath());

        byte[] plainData = userFileService.safeReadFile(filePath, backupPath, userFile);
        InputStreamResource inputStreamResource;
        inputStreamResource = new InputStreamResource(new ByteArrayInputStream(plainData));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + userFile.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(userFile.getContentType()))
                .contentLength(plainData.length)
                .body(inputStreamResource);
    }

    @ExceptionHandler(UserFileNotFoundException.class)
    public String handleUserFileNotFoundException(UserFileNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", e.getMessage());
        return "redirect:/welcome";
    }

    @ExceptionHandler(FileNotFoundException.class)
    public String handleFileNotFoundException(FileNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/welcome";
    }
}
