package ukim.finki.file_vault.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Aspect
@Component
public class LoggingAspect {

    @AfterReturning("execution(* ukim.finki.file_vault.service.TwoFactorTokenService.verifyTwoFactorToken(..))")
    public void logAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_UNCONFIRMED"))) {

            appendLoginLog(auth.getName());
        }
    }

    @AfterReturning("execution(* ukim.finki.file_vault.service.UserFileService.uploadFile(..))")
    public void logFileSave(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String fileName = (String) args[1];
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        appendFileSaveLog(auth.getName(), fileName);

    }

    @AfterReturning("execution(* ukim.finki.file_vault.service.UserFileService.changeFileName(..))")
    public void logFileNameChange(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String newFileName = (String) args[0];
        Long fileId = (Long) args[1];
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        appendFileNameChangeLog(auth.getName(), newFileName, fileId);
    }

    @AfterReturning("execution(* ukim.finki.file_vault.service.UserFileService.deleteFileByID(..))")
    public void logFileDeletion(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long fileId = (Long) args[0];
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        appendFileNameDeletionLog(auth.getName(), fileId);
    }

    @AfterReturning("execution(* ukim.finki.file_vault.service.UserService.addFileToUserByIDs(..))")
    public void logFileAccessGivenToUser(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[0];
        Long fileId = (Long) args[1];
        appendFileAccessGivenToUserLog(userId, fileId);
    }

    @AfterReturning("execution(* ukim.finki.file_vault.service.UserService.removeFileFromUserByIDs(..))")
    public void logFileAccessRevokedToUser(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[0];
        Long fileId = (Long) args[1];
        appendFileAccessRevokedToUserLog(userId, fileId);
    }

    @AfterReturning("execution(* ukim.finki.file_vault.service.UserService.giveMod(..))")
    public void logGiveMod(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long userID = (Long) args[0];
        appendLogGiveMod(userID);
    }

    @AfterReturning("execution(* ukim.finki.file_vault.service.UserService.revokeMod(..))")
    public void logRevokeMod(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long userID = (Long) args[0];
        appendLogRevokeMod(userID);
    }

    private void appendLogRevokeMod(Long userID) {
        String logLine = String.format("User with ID:%d has lost mod at %s%n", userID,LocalDateTime.now());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("logs.log", true))) {
            writer.write(logLine);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void appendLogGiveMod(Long userID) {
        String logLine = String.format("User with ID:%d has gained mod at %s%n", userID,LocalDateTime.now());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("logs.log", true))) {
            writer.write(logLine);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void appendFileAccessRevokedToUserLog(Long userId, Long fileId) {
        String logLine = String.format("User with ID:%d has lost access to file with ID:%d at %s%n", userId, fileId,LocalDateTime.now());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("logs.log", true))) {
            writer.write(logLine);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void appendFileAccessGivenToUserLog(Long userId, Long fileId) {
        String logLine = String.format("User with ID:%d has gained access to file with ID:%d at %s%n", userId, fileId,LocalDateTime.now());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("logs.log", true))) {
            writer.write(logLine);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void appendFileNameDeletionLog(String username, Long fileId) {
        String logLine = String.format("User %s has deleted file with ID:%d at %s%n", username, fileId,LocalDateTime.now());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("logs.log", true))) {
            writer.write(logLine);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void appendFileNameChangeLog(String username, String fileName, Long fileId) {
        String logLine = String.format("User %s has changed the name of file with ID:%d to %s at %s%n", username, fileId,fileName ,LocalDateTime.now());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("logs.log", true))) {
            writer.write(logLine);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void appendFileSaveLog(String username, String fileName) {
        String logLine = String.format("User %s has uploaded file %s at %s%n", username, fileName, LocalDateTime.now());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("logs.log", true))) {
            writer.write(logLine);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void appendLoginLog(String username) {
        String logLine = String.format("User %s has logged in at %s%n", username, LocalDateTime.now());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("logs.log", true))) {
            writer.write(logLine);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
