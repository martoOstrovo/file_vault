package ukim.finki.file_vault.service;

public interface MailSenderService {
    void sendActivationCode(String to, String subject, String body);
}
