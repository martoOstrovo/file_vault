package ukim.finki.file_vault.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ukim.finki.file_vault.service.RegisterService;

@Controller
@RequestMapping("/verify")
public class VerifyController {
    private final RegisterService registerService;

    public VerifyController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @GetMapping
    public String showVerify(@RequestParam String token) throws RuntimeException {
        registerService.confirmAccount(token);
        return "activated";
    }
}
