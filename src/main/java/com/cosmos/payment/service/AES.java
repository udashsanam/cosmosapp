package com.cosmos.payment.service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AES {

    private static final String SECRET = "1234567890123456";

    private static final SecretKey SECRET_KEY =
            new SecretKeySpec(
                    SECRET.getBytes(StandardCharsets.UTF_8),
                    "AES"
            );

    public static String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
        byte[] encryptedBytes =
                cipher.doFinal(
                        plainText.getBytes(StandardCharsets.UTF_8)
                );
        // URL SAFE BASE64
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);
        // URL SAFE BASE64
        byte[] decodedBytes =
                Base64.getUrlDecoder()
                        .decode(encryptedText);
        byte[] decryptedBytes =
                cipher.doFinal(decodedBytes);
        return new String(
                decryptedBytes,
                StandardCharsets.UTF_8
        );
    }
}