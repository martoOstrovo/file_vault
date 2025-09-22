package ukim.finki.file_vault.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.model.exception.UserNotFoundException;
import ukim.finki.file_vault.service.UserService;
import javax.management.relation.RoleNotFoundException;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UsersController {
    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showUsers(Model model) throws RoleNotFoundException {
        List<User> users = userService.getUsers();
        List<User> moderators = userService.getMods();
        List<User> lockedAccounts = userService.getLocked();
        model.addAttribute("users", users);
        model.addAttribute("moderators", moderators);
        model.addAttribute("lockedAccounts", lockedAccounts);
        return "users";
    }

    @PostMapping("/process-elevate")
    public String processElevate(@RequestParam Long userID) throws RoleNotFoundException, UserNotFoundException {
        userService.giveMod(userID);
        return "redirect:/users";
    }

    @PostMapping("/process-revoke")
    public String processRevoke(@RequestParam Long userID) throws RoleNotFoundException, UserNotFoundException {
        userService.revokeMod(userID);
        return "redirect:/users";
    }

    @PostMapping("/lock")
    public String processLock(@RequestParam Long userID) {
        userService.lockAccountByID(userID);
        return "redirect:/users";
    }

    @PostMapping("/unlock")
    public String processUnlock(@RequestParam Long userID) {
        userService.unlockAccountById(userID);
        return "redirect:/users";
    }
}
