package ukim.finki.file_vault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//TODO implement auto-deletion of expired 2fa codes
//TODO proper exception handling
//TODO add re-authentication after 2fa code confirmation

@SpringBootApplication
@EnableScheduling
public class FileVaultApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileVaultApplication.class, args);
	}

}
