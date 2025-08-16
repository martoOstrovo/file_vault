package ukim.finki.file_vault.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error-page")
public class ErrorPageController {

    @GetMapping
    public String getErrorPage(@ModelAttribute("exception") String exception, Model model) {
        model.addAttribute("exception", exception);
        return "error-page";
    }
}
