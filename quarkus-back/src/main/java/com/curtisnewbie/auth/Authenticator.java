package com.curtisnewbie.auth;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.curtisnewbie.util.RandomGenerator;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Class that takes care of authentication process.
 * <p>
 * A random key (string) should be generated using
 * {@link Authenticator#genAuthKey()}, everytime a new connection established
 * that asks for authentication. This key is displayed on the terminal, and is
 * valid within the time limit {@link Authenticator#authKeyTime}.
 * <p>
 * Once the key is expired, authenticatin will still fail even though the key is
 * matched. Only when both conditions are matched, the client is authenticated.
 * Without calling this method may result in that the key is always expired or
 * null.
 */
@ApplicationScoped
public class Authenticator {

    private final Logger logger = Logger.getLogger(this.getClass());

    @ConfigProperty(name = "auth.key.valid.sec")
    protected int authKeyValidSec;

    @ConfigProperty(name = "auth.key.length")
    protected int keyLen;

    @Inject
    protected RandomGenerator randGen;

    private String authKey;
    private Long authKeyTime;

    /**
     * Generate a random authenticatin key that is valid for a limited time
     */
    public void genAuthKey() {
        this.authKey = randGen.randomStr(keyLen);
        this.authKeyTime = System.currentTimeMillis() + (1000 * authKeyValidSec);
        this.logger.info(
                String.format("Authentication Key: '%s' Key is valid for %d seconds", this.authKey, authKeyValidSec));
    }

    /**
     * Check whether client is authenticated based on 1) whether the key is expired
     * and 2) whether the keys match. Once the client is successfully authenticated,
     * the key is set to null to prevent second use.
     * 
     * @param key key for authentication
     * @return whether client is authenticated
     */
    public boolean isAuthenticated(String key) {
        var result = this.authKey != null && !isKeyExpired() && key.equals(this.authKey);
        if (result == true) // set key to null to prevent second use
            this.authKey = null;
        return result;
    }

    /**
     * Check whether the current authentication key is expired
     * 
     * @return whether the key is expired
     */
    public boolean isKeyExpired() {
        return System.currentTimeMillis() > authKeyTime;
    }

    /**
     * Check whether {@code authKey} is already generated
     * 
     * @return whether {@code authKey} is already generated
     */
    public boolean hasAuthKey() {
        return authKey != null;
    }
}