package ukim.finki.file_vault.service;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoUtils {
    private final static String AES_ALGO = "AES/GCM/NoPadding";
    private final static int GCM_TAG_LENGTH = 128;
    private final static int IV_LENGTH = 12;

    private final SecretKey aesKey;
    private final SecretKey hmacKey;

    public CryptoUtils(String base64AesKey, String base64HmacKey) {
        this.aesKey = new SecretKeySpec(Base64.getDecoder().decode(base64AesKey), "AES");
        this.hmacKey = new SecretKeySpec(Base64.getDecoder().decode(base64HmacKey), "HmacSHA256");
    }

    public byte[] encrypt(byte[] data, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGO);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, parameterSpec);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data, byte[] iv) throws Exception {
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

    public byte[] calculateHmac (byte[] data) throws Exception {
        Mac mac =  Mac.getInstance("HmacSHA256");
        mac.init(hmacKey);
        return mac.doFinal(data);
    }

    public boolean verifyMac(byte[] data, byte[] expectedHmac) throws Exception {
        byte[] calc = calculateHmac(data);
        return MessageDigest.isEqual(calc, expectedHmac);
    }
}
