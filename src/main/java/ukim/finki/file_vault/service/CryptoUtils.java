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
    private final SecretKey KEK;

    public CryptoUtils(@Value("${AES_MASTER_KEY_BASE64}") String base64AesKey) {
        this.KEK = new SecretKeySpec(Base64.getDecoder().decode(base64AesKey), "AES");
    }

    public byte[] encrypt(byte[] data, byte[] iv, String wrappedDEKBase64) throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        SecretKey DEK = unwrapDEK(wrappedDEKBase64);
        Cipher cipher = Cipher.getInstance(AES_ALGO);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, DEK, parameterSpec);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data, byte[] iv, String wrappedDEKBase64) throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        SecretKey DEK = unwrapDEK(wrappedDEKBase64);
        Cipher cipher = Cipher.getInstance(AES_ALGO);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, DEK, parameterSpec);
        return cipher.doFinal(data);
    }

    public byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public String generateWrappedDEKBase64() throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey DEK = keyGenerator.generateKey();
        byte[] wrappedDEk = wrapDEK(DEK);
        return Base64.getEncoder().encodeToString(wrappedDEk);
    }

    private byte[] wrapDEK(SecretKey DEK) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AESWrap");
        cipher.init(Cipher.WRAP_MODE, KEK);
        return cipher.wrap(DEK);
    }

    private SecretKey unwrapDEK(String wrappedDEKBase64) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException {
        byte[] wrappedDEK = Base64.getDecoder().decode(wrappedDEKBase64);
        Cipher cipher = Cipher.getInstance("AESWrap");
        cipher.init(Cipher.UNWRAP_MODE, KEK);
        return (SecretKey) cipher.unwrap(wrappedDEK, "AES", Cipher.SECRET_KEY);
    }

}
