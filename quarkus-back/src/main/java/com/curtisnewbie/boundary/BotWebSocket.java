package com.curtisnewbie.boundary;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    @Inject
    protected Authenticator auth;

    @Inject
    protected Bot bot;

    /** the authenticated session */
    private Session authSession = null;

    /** the sessions that are not authenticated, but are wait for authentication */
    private Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        // only one authenticated session is allowed
        if (authSession != null) {
            try {
                session.close();
            } catch (Exception e) {
                logger.error(e);
            }
        } else {
            // waiting for authentication
            sessions.put(session.getId(), session);
        }
    }

    @OnClose
    public void onClose(Session session) {
        if (authSession.getId().equals(session.getId())) {
            closeAuthSession();
        } else if (sessions.containsKey(session.getId())) {
            sessions.remove(session.getId());
        }
    }

    @OnError
    public void onError(Throwable t, Session session) {
        logger.error(t.getMessage());
        if (!session.isOpen()) {
            closeAuthSession();
        }
    }

    @OnMessage
    public void onMsg(String msg, Session session) {
        if (authSession == null) { // no one authenticated yet
            if (auth.isAuthenticated(msg)) {
                authSession = session;
                sessions.clear(); // clear all sessions that are not authenticated
                logger.info("User Authenticated. Listening instructions");
            } else if (auth.isKeyExpired()) {
                auth.genAuthKey();
            }
        } else if (session.getId().equals(authSession.getId())) { // already authenticated
            // simulate keyboard inputs based on instructioons
            instructBot(msg);
            logger.info("Received Instructions: " + msg);
        } else { // close this connection, since the key is incorrect
            try {
                session.close();
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    /**
     * Close the authenticated session
     */
    private void closeAuthSession() {
        authSession = null;
        logger.info("User Disconnected. Waiting for new connectin.");
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
        }
    }
}