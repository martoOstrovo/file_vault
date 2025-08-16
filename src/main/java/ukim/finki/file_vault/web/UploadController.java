package ukim.finki.file_vault.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ukim.finki.file_vault.model.exception.FileNameAlreadyExistsException;
import ukim.finki.file_vault.model.exception.UserNotFoundInSessionException;
import ukim.finki.file_vault.service.UserFileService;
import java.io.IOException;


@Controller
@RequestMapping("/file-upload")
public class UploadController {
    private final UserFileService userFileService;

    public UploadController(UserFileService userFileService) {
        this.userFileService = userFileService;
    }

    @GetMapping
    public String showUpload() {
        return "upload";
    }

    @PostMapping
    public String processUpload(@RequestParam MultipartFile file, @RequestParam(required = false) String fileName)
            throws IOException , UserNotFoundInSessionException, FileNameAlreadyExistsException {

        String name;
        String originalFileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            name = file.getOriginalFilename();
        } else {
            String[] split = originalFileName.split("\\.");
            String ext = split[split.length - 1];
            name = fileName +  "." + ext;
        }
        userFileService.uploadFile(file, name);
        return "redirect:/welcome";
    }

    @ExceptionHandler
    public String handleFileNameAlreadyExistsException(FileNameAlreadyExistsException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/welcome";
    }
}
