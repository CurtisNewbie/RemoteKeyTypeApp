package com.curtisnewbie.boundary;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.curtisnewbie.auth.Authenticator;
import com.curtisnewbie.bot.Bot;
import com.curtisnewbie.bot.Key;

import org.jboss.logging.Logger;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * The boundary of this WebApp that establishes a communication with clients
 * using websocket
 * </p>
 */
@ServerEndpoint("/bot")
@ApplicationScoped
public class BotWebSocket {

    private final Logger logger = Logger.getLogger(this.getClass());
    private final Object lock = new Object();

    @Inject
    protected Authenticator auth;

    @Inject
    protected Bot bot;

    /** the authenticated session */
    private Session authSession = null;

    /** the sessions that are not authenticated, but are wait for authentication */
    private Map<String, Session> sessions = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        synchronized (lock) {
            // only one authenticated session is allowed
            if (authSession != null && authSession.isOpen()) {
                closeSession(session);
            } else if (authSession != null && !authSession.isOpen()) {
                closeAuthSession();
            } else {
                // waiting for authentication
                sessions.put(session.getId(), session);
                if (!auth.hasAuthKey() || auth.isKeyExpired()) {
                    auth.genAuthKey();
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        synchronized (lock) {
            if (authSession.getId().equals(session.getId())) {
                closeAuthSession();
            } else if (sessions.containsKey(session.getId())) {
                sessions.remove(session.getId());
            }
        }
    }

    @OnError
    public void onError(Throwable t, Session session) {
        synchronized (lock) {
            logger.error(t.getMessage());
            if (!session.isOpen()) {
                if (authSession != null && session.getId() == authSession.getId()) {
                    closeAuthSession();
                } else {
                    sessions.remove(session.getId());
                }
            }
        }
    }

    @OnMessage
    public void onMsg(String msg, Session session) {
        synchronized (lock) {
            if (authSession == null) { // no one authenticated yet
                if (auth.isAuthenticated(msg.substring(1, msg.length() - 1))) {
                    logger.info("User Authenticated. Listening instructions");
                    authSession = session;
                    // clear all sessions that are not authenticated
                    for (var s : sessions.values()) {
                        if (!s.getId().equals(session.getId()))
                            closeSession(s);
                    }
                    sessions.clear();
                } else if (auth.isKeyExpired()) {
                    auth.genAuthKey();
                } else {
                    // close this connection, since the key is incorrect
                    closeSession(session);
                    logger.info("User not authenticated due to incorrect key. Connection Closed.");
                }
            } else if (session.getId().equals(authSession.getId())) { // already authenticated
                // simulate keyboard inputs based on instructioons
                instructBot(msg.substring(1, msg.length() - 1));
                logger.info("Received Instructions: " + msg);
            }
        }
    }

    /**
     * Close a {@code Session}
     * 
     * @param session
     */
    private void closeSession(Session session) {
        try {
            if (session.isOpen())
                session.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Set {@code authSession} to null and log message
     */
    private void closeAuthSession() {
        authSession = null;
        logger.info("User Disconnected. Waiting for new connection.");
    }

    /**
     * Instruct the bot to simulate keyboard inputs
     */
    private void instructBot(String instruction) {
        if (instruction.equals(Key.ARROW_DOWN.getStr())) {
            bot.keyType(Key.ARROW_DOWN.getKeyEvent());
        } else if (instruction.equals(Key.ARROW_LEFT.getStr())) {
            bot.keyType(Key.ARROW_LEFT.getKeyEvent());
        } else if (instruction.equals(Key.ARROW_RIGHT.getStr())) {
            bot.keyType(Key.ARROW_RIGHT.getKeyEvent());
        } else if (instruction.equals(Key.ARROW_UP.getStr())) {
            bot.keyType(Key.ARROW_UP.getKeyEvent());
        } else {
            return;
        }
        logger.info("Pressed " + instruction);
    }
}