package ukim.finki.file_vault.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ukim.finki.file_vault.service.TwoFactorTokenService;

@Controller
@RequestMapping("/2FA")
public class TwoFactorController {
    private final TwoFactorTokenService twoFactorTokenService;

    public TwoFactorController(TwoFactorTokenService twoFactorTokenService) {
        this.twoFactorTokenService = twoFactorTokenService;
    }

    @GetMapping
    public String show2FA() {
        return "verify-2FA";
    }

    @PostMapping
    public String process2FA(String verificationToken) {
        boolean valid = twoFactorTokenService.verifyTwoFactorToken(verificationToken);
        if(!valid) {
            return "redirect:/2FA?error=true";
        }
        return "redirect:/welcome";
    }
}
