package ukim.finki.file_vault.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ukim.finki.file_vault.annotation.PasswordComplex;
import ukim.finki.file_vault.annotation.UniqueUsername;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @NotBlank(message = "Please enter your name.")
    private String name;
    @NotBlank(message = "Please enter your surname.")
    private String surname;
    @NotBlank
    @Email(message = "Please enter a valid email address.")
    private String email;
    @NotBlank
    @UniqueUsername
    private String username;
    @NotBlank
    @PasswordComplex
    private String password;
}
