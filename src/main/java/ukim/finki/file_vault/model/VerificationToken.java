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
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private User user;

    public VerificationToken(String token, User user) {
        this.token = token;
        this.expiryDate =  LocalDateTime.now().plusDays(1);
        this.user = user;
    }
}
