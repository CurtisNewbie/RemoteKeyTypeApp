package com.curtisnewbie.auth;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import com.curtisnewbie.bot.Bot;
import org.jboss.logging.Logger;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Class that coordinates the authentication and bot instruction process. This class is designed to
 * be as Thread-safe as possible, thus the methods are {@code synchronized} and designed to be
 * "atomic" to make it more reliable.
 * </p>
 */
@ApplicationScoped
public class AuthCoordinator {

    private static final Logger logger = Logger.getLogger(AuthCoordinator.class);

    /**
     * User is authenticated
     */
    public static final int AUTHENTICATED = 0;

    /**
     * Authentication is rejected
     */
    public static final int REJECTED = 1;

    /**
     * Bot is instructed
     */
    public static final int INSTRUCTED = 2;

    private final Authenticator auth;
    private final Bot bot;

    public AuthCoordinator(Authenticator auth, Bot bot) {
        this.auth = auth;
        this.bot = bot;
    }

    private Session authSession = null;

    /**
     * Authenticate the session or instruct the bot to undertake the requested operation. The
     * decision on which action to take is dependent on the state of this component.
     * <p>
     * I.e., if no one is authenticated, it expects a user to authenticate, than in this case, the
     * {@code command} is treated as authentication token.
     * <p>
     * If one is already authenticated, it compares the session id and instruct to bot to simulate
     * the key press events.
     * 
     * @param session
     * @param command
     * @return result value. e.g., AuthSession#AUTHENTICATED
     * @see AuthCoordinator#AUTHENTICATED
     * @see AuthCoordinator#REJECTED
     * @see AuthCoordinator#INSTRUCTED
     */
    public synchronized int instructOrAuthenticate(Session session, String command) {
        if (this.authSession == null) { // authenticate
            if (auth.isAuthenticated(command)) {
                this.authSession = session;
                return AUTHENTICATED;
            } else if (auth.isKeyExpired()) {
                auth.genAuthKey();
            }
        } else if (this.authSession.getId().equals(session.getId())) {
            bot.instruct(command);
            return INSTRUCTED;
        }
        return REJECTED;
    }

    /**
     * Check whether the given {@code session} is the one that is authenticated by comparing their
     * sessionId, if true, set it to null. As a result, this AuthCoordinator will be expecting new
     * connection for authentication.
     * 
     * @param session
     * @return if the passed session is the one that is authenticated and close
     */
    public synchronized boolean compareAndClose(Session session) {
        if (session != null && session.getId().equals(this.authSession.getId())) {
            this.authSession = null;
            return true;
        }
        return false;
    }

    /**
     * Return whether a new user can authenticate. I.e., Return true if no one is authenticated else
     * false.
     * 
     * @return
     */
    public synchronized boolean canAuthenticate() {
        return this.authSession == null;
    }

    /**
     * Try to generate an authentication key (token), but its' up to the coordinator to decide
     * whether it should generate a new key. Conditions that affect the decision-making includes:
     * <p>
     * 1) whether it has a key already
     * <p>
     * 2) whether the key is expired.
     */
    public synchronized void tryGenAuthKey() {
        if (!auth.hasAuthKey() || auth.isKeyExpired()) {
            auth.genAuthKey();
        }
    }
}
