package com.licenta.healthapp.controller;
import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSAUtil {
    private static final Logger logger = LoggerFactory.getLogger(RSAUtil.class);

    public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
        logger.info("Encrypted Text: {}", encryptedText);
        return encryptedText;
    }

    public static String decrypt(String encryptedText, PrivateKey privateKey) throws Exception {
        logger.info("Encrypted Text Received for Decryption: {}", encryptedText);
        byte[] bytes = Base64.getDecoder().decode(encryptedText);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(bytes);
        String decryptedText = new String(decryptedBytes);
        logger.info("Decrypted Text: {}", decryptedText);
        return decryptedText;
    }
}
