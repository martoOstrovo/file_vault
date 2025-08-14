package ukim.finki.file_vault.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.service.SecurityUtils;
import ukim.finki.file_vault.service.UserService;

@Controller
@RequestMapping("/welcome")
public class WelcomeController {
    private final UserService userService;

    public WelcomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showWelcome(Model model) {
        User currentUser = userService.getUserByIdWithFiles(SecurityUtils.getCurrentUser().getID());
        model.addAttribute("user", currentUser);
        model.addAttribute("files", currentUser.getFiles());
        return "welcome";
    }
}
