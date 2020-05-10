package com.curtisnewbie.util;

import java.security.SecureRandom;

import javax.enterprise.context.Dependent;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Util class the generates random string
 * </p>
 */
@Dependent
public class RandomGenerator {

    public final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    protected SecureRandom rand = new SecureRandom();

    /**
     * Generate random string with the specified length
     * 
     * @param len length of the generated random string
     */
    public String randomStr(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(CHARS.charAt(rand.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}