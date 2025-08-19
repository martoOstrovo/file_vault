package ukim.finki.file_vault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//TODO divide the files by user owned and non user owned when showcasing them.

@SpringBootApplication
@EnableScheduling
public class FileVaultApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileVaultApplication.class, args);
	}

}
