package ukim.finki.file_vault.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String token;
    private LocalDateTime expiryDate;
    @OneToOne(mappedBy = "verificationToken")
    private User user;

    public VerificationToken(String token) {
        this.token = token;
    }
}
