package ukim.finki.file_vault.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoUtils {
    private final static String AES_ALGO = "AES/GCM/NoPadding";
    private final static int GCM_TAG_LENGTH = 128;
    private final static int IV_LENGTH = 12;
    private final SecretKey aesKey;

    public CryptoUtils(@Value("${AES_MASTER_KEY_BASE64}") String base64AesKey) {
        this.aesKey = new SecretKeySpec(Base64.getDecoder().decode(base64AesKey), "AES");
    }

    public byte[] encrypt(byte[] data, byte[] iv) throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        Cipher cipher = Cipher.getInstance(AES_ALGO);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, parameterSpec);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data, byte[] iv) throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        Cipher cipher = Cipher.getInstance(AES_ALGO);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, parameterSpec);
        return cipher.doFinal(data);
    }

    public byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

}
