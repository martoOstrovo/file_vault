package ukim.finki.file_vault.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.model.UserFile;
import ukim.finki.file_vault.service.SecurityUtils;
import ukim.finki.file_vault.service.UserService;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/welcome")
public class WelcomeController {
    private final UserService userService;

    public WelcomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showWelcome(@ModelAttribute("error") String error,  Model model) {
        User currentUser = userService.getUserByIdWithFiles(Objects.requireNonNull(SecurityUtils.getCurrentUser()).getID());
        List<UserFile> ownedFiles = currentUser.getFiles().stream()
                .filter(file -> file.getOwnerID().equals(currentUser.getID())).toList();

        List<UserFile> accessibleFiles = currentUser.getFiles().stream()
                .filter(file -> !(file.getOwnerID().equals(currentUser.getID()))).toList();

        model.addAttribute("user", currentUser);
        model.addAttribute("ownedFiles", ownedFiles);
        model.addAttribute("accessibleFiles", accessibleFiles);
        model.addAttribute("error", error);
        return "welcome";
    }
}
