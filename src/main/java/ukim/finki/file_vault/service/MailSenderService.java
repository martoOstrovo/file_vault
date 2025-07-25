package ukim.finki.file_vault.service;

public interface MailSenderService {
    void sendMail(String to, String subject, String body);
}
