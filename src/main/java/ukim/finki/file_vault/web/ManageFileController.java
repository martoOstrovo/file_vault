package ukim.finki.file_vault.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ukim.finki.file_vault.model.UserFile;
import ukim.finki.file_vault.model.exception.FileNameAlreadyExistsException;
import ukim.finki.file_vault.model.exception.IllegalFileNameException;
import ukim.finki.file_vault.model.exception.UserFileNotFoundException;
import ukim.finki.file_vault.model.exception.UserNotFoundInSessionException;
import ukim.finki.file_vault.service.SecurityUtils;
import ukim.finki.file_vault.service.UserFileService;
import ukim.finki.file_vault.service.UserService;
import java.io.IOException;
import java.util.Objects;

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
    public String showManageFilePage(@PathVariable Long fileID, Model model,
                                     @ModelAttribute(value = "fileNameExists") String fileNameExists,
                                     @ModelAttribute(value = "illegalFileName") String illegalFileName)
            throws UserNotFoundInSessionException, UserFileNotFoundException {

        if(fileNameExists == null || fileNameExists.isEmpty()) {
            model.addAttribute("fileNameExists", null);
        } else {
            model.addAttribute("fileNameExists", fileNameExists);
        }

        if (illegalFileName == null || illegalFileName.isEmpty()) {
            model.addAttribute("illegalFileName", null);
        } else {
            model.addAttribute("illegalFileName", illegalFileName);
        }

        Long userID = Objects.requireNonNull(SecurityUtils.getCurrentUser()).getID();
        model.addAttribute("userID", userID);

        UserFile file = userFileService.getUserFileByIDWithAccessList(fileID);
        model.addAttribute("file", file);
        model.addAttribute("usersWithAccess", file.getUsersWithAccess());

        boolean userOwnsFile = userService.userOwnsFile(fileID);
        model.addAttribute("userOwnsFile", userOwnsFile);

        return "/manage-file";
    }

    @PostMapping("/change-name")
    public String processFileNameChange(@RequestParam String newFileName, Long fileID, RedirectAttributes redirectAttributes)
            throws IOException, UserNotFoundInSessionException {
        try {
            userFileService.changeFileName(newFileName, fileID);
        } catch (FileNameAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("fileNameExists", "File name already exists!");
            return "redirect:/manage-file/"+ fileID;
        } catch (IllegalFileNameException e) {
            redirectAttributes.addFlashAttribute("illegalFileName", e.getMessage());
            return "redirect:/manage-file/"+ fileID;
        }
        return "redirect:/welcome";
    }

    @PostMapping("/delete")
    public String processFileDeletion(@RequestParam Long fileID) throws IOException {
        userFileService.deleteFileByID(fileID);
        return "redirect:/welcome";
    }

    @PostMapping("/remove-file")
    public String processFileRemoval(@RequestParam Long fileID, @RequestParam Long userID) {
        userService.removeFileFromUserByIDs(userID, fileID);
        return "redirect:/manage-file/"+ fileID;
    }

}
