package ukim.finki.file_vault.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.service.SecurityUtils;

@Controller
@RequestMapping("/welcome")
public class WelcomeController {
    @GetMapping
    public String showWelcome(Model model) {
        User currentUser = SecurityUtils.getCurrentUser();
        model.addAttribute("user", currentUser);
        return "welcome";
    }
}
