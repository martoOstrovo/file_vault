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
import ukim.finki.file_vault.model.exception.UserFileNotFoundException;
import ukim.finki.file_vault.model.exception.UserNotFoundInSessionException;
import ukim.finki.file_vault.service.UserFileService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


@Controller
@RequestMapping("/file-download")
public class DownloadController {
    private final UserFileService userFileService;

    public DownloadController(UserFileService userFileService) {
        this.userFileService = userFileService;
    }

    @PostMapping
    public ResponseEntity<InputStreamResource> processDownload(@RequestParam Long fileID) throws FileNotFoundException, UserFileNotFoundException {
        UserFile userFile = userFileService.getUserFileById(fileID);
        File file =  new File(userFile.getFilePath());
        InputStreamResource inputStreamResource;
        inputStreamResource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.parseMediaType(userFile.getContentType()))
                .contentLength(file.length())
                .body(inputStreamResource);
    }

    @ExceptionHandler(UserFileNotFoundException.class)
    public String handleUserFileNotFoundException(UserNotFoundInSessionException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", e.getMessage());
        return "redirect:/welcome";
    }

    @ExceptionHandler(FileNotFoundException.class)
    public String handleFileNotFoundException(FileNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/welcome";
    }
}
