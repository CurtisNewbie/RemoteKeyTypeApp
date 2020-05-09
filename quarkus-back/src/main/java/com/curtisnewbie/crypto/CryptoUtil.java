package com.curtisnewbie.crypto;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.util.Base64;

import javax.crypto.Cipher;

import org.jboss.logging.Logger;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Util class for Cryptography.
 * </p>
 */
public class CryptoUtil {

    private static final String RSA_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    private static final Logger logger = Logger.getLogger(CryptoUtil.class);

    /**
     * Generate RSA Key Pair
     * 
     * @return RSA Key pair or {@code NULL} if exceptions are thrown.
     */
    public static KeyPair rsaKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            kpg.initialize(KEY_SIZE);
            return kpg.genKeyPair();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Decrypt a ciphered text using private key
     * 
     * @param privateKey
     * @param cipherTxt
     * @return
     */
    public static String decrypt(PrivateKey privateKey, String cipherTxt) {
        Cipher cipher = getRSACipher();
        String plainTxt = null;
        if (cipher != null) {
            try {
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                var encoder = Base64.getEncoder();
                plainTxt = encoder.encodeToString(cipher.doFinal(cipherTxt.getBytes()));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return plainTxt;
    }

    private static Cipher getRSACipher() {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(RSA_ALGORITHM);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return cipher;
    }
}