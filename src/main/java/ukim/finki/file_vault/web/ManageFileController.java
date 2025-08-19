package ukim.finki.file_vault.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ukim.finki.file_vault.model.UserFile;
import ukim.finki.file_vault.model.exception.FileNameAlreadyExistsException;
import ukim.finki.file_vault.model.exception.UserNotFoundInSessionException;
import ukim.finki.file_vault.service.UserFileService;
import ukim.finki.file_vault.service.UserService;
import java.io.IOException;

@Controller
@RequestMapping("/manage-file")
public class ManageFileController {
    private final UserFileService userFileService;
    private final UserService userService;

    public ManageFileController(UserFileService userFileService,  UserService userService) {
        this.userFileService = userFileService;
        this.userService = userService;
    }

    @GetMapping("/{fileID}")
    public String showManageFilePage(@PathVariable Long fileID, Model model) {
        UserFile file = userFileService.getUserFileById(fileID);
        boolean userOwnsFile = userService.userOwnsFile(fileID);
        model.addAttribute("userOwnsFile", userOwnsFile);
        model.addAttribute("file", file);
        return "/manage-file";
    }

    @PostMapping("/change-name")
    public String handleFileNameChange(@RequestParam String newFileName, Long fileID) throws IOException, FileNameAlreadyExistsException, UserNotFoundInSessionException {
        userFileService.changeFileName(newFileName, fileID);
        return "redirect:/welcome";
    }

    @ExceptionHandler(FileNameAlreadyExistsException.class)
    public String manageFileAlreadyExistsException(FileNameAlreadyExistsException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/welcome";
    }
}
