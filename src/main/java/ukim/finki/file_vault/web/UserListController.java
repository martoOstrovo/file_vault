package ukim.finki.file_vault.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.model.UserFile;
import ukim.finki.file_vault.model.exception.UserFileNotFoundException;
import ukim.finki.file_vault.model.exception.UserNotFoundException;
import ukim.finki.file_vault.service.UserFileService;
import ukim.finki.file_vault.service.UserService;
import java.util.List;


@Controller
@RequestMapping("/user-list")
public class UserListController {
    private final UserService userService;
    private final UserFileService userFileService;

    public UserListController(UserService userService, UserFileService userFileService) {
        this.userService = userService;
        this.userFileService = userFileService;
    }

    @GetMapping("/{fileID}")
    public String showUserList(@PathVariable Long fileID, Model model) throws UserFileNotFoundException {
        UserFile file = userFileService.getUserFileById(fileID);
        List<User> allUsers = userService.getAllUsers();

        List<User> unaddedUsers = allUsers.stream()
                .filter(user -> !file.getUsersWithAccess().contains(user))
                .toList();

        model.addAttribute("file", file);
        model.addAttribute("users", unaddedUsers);
        return "user-list";
    }

    @PostMapping("/add")
    public String processAddUser(@RequestParam Long userID, @RequestParam Long fileID) throws UserFileNotFoundException, UserNotFoundException {
        userService.addFileToUserByIDs(userID, fileID);
        return "redirect:/manage-file/"+fileID;
    }
}
