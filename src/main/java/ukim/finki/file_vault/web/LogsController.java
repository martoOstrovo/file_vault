package ukim.finki.file_vault.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/logs")
public class LogsController {

    @GetMapping
    public String showLogs(Model model) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader("logs.log"));
        List<String> logs = new ArrayList<>();
        br.lines().forEach(logs::add);
        model.addAttribute("logs", logs);
        return "logs";
    }
}
