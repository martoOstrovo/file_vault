package ukim.finki.file_vault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//TODO implement auto-deletion of expired account verification codes along with their accounts
//TODO proper exception handling in the RegisterServiceImp class
//TODO add 2FA for the login

@SpringBootApplication
@EnableScheduling
public class FileVaultApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileVaultApplication.class, args);
	}

}
