package ukim.finki.file_vault.service;

import ukim.finki.file_vault.model.UserDTO;

public interface RegisterService {
    void registerUser(UserDTO userDTO);
    void confirmAccount(String token);
}
