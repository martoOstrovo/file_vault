package ukim.finki.file_vault.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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
    public String processUpload(@RequestParam MultipartFile file, @RequestParam(required = false) String fileName) throws IOException {
        String name;
        String originalFileName = file.getOriginalFilename();
        if (fileName == null) {
            name = file.getOriginalFilename();
        } else {
            assert originalFileName != null;
            String[] split = originalFileName.split("\\.");
            name = fileName +  "." + split[1];
        }
        userFileService.uploadFile(file, name);
        return "redirect:/welcome";
    }
}
