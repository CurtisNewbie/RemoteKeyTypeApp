package com.curtisnewbie.boundary;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import com.curtisnewbie.auth.AuthCoordinator;
import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * The boundary of this WebApp that establishes a communication with clients using websocket
 * </p>
 */
@ServerEndpoint("/bot")
@ApplicationScoped
public class BotWebSocket {

    private static final Logger logger = Logger.getLogger(BotWebSocket.class);
    private final AuthCoordinator authCoordinator;

    public BotWebSocket(AuthCoordinator authCoordinator) {
        this.authCoordinator = authCoordinator;
    }

    /** The sessions that are not authenticated, but are waiting for authentication */
    private Map<String, Session> sessions = new HashMap<>();

    protected void onStart(@Observes StartupEvent se) {
        try {
            InetAddress inet = InetAddress.getLocalHost();
            logger.info(String.format("Your Current IP address: '%s'", inet.getHostAddress()));
        } catch (Exception e) {
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        // only one authenticated session is allowed
        if (!authCoordinator.canAuthenticate()) {
            closeSession(session); // there is an authenticated user, close the connection directly
        } else {
            synchronized (sessions) {
                // waiting for authentication
                sessions.put(session.getId(), session);
            }
            authCoordinator.tryGenAuthKey();
        }
    }

    @OnClose
    public void onClose(Session session) {
        if (authCoordinator.compareAndClose(session)) {
            logger.info("User Disconnected. Waiting for new connection.");
        } else {
            synchronized (sessions) {
                sessions.remove(session.getId());
            }
        }
    }

    @OnError
    public void onError(Throwable t, Session session) {
        if (!session.isOpen()) {
            if (authCoordinator.compareAndClose(session)) {
                logger.info("User Disconnected. Waiting for new connection.");
            } else {
                synchronized (sessions) {
                    sessions.remove(session.getId());
                }
            }
        }
    }

    @OnMessage
    public void onMsg(String msg, Session session) {
        int resultCode =
                authCoordinator.instructOrAuthenticate(session, msg.substring(1, msg.length() - 1));
        if (resultCode == AuthCoordinator.AUTHENTICATED) {
            logger.info("User Authenticated. Listening instructions");
            synchronized (sessions) {
                for (var s : sessions.values()) {
                    if (!s.getId().equals(session.getId()))
                        closeSession(s);
                }
            }
        } else if (resultCode == AuthCoordinator.REJECTED) {
            logger.info("User not authenticated due to incorrect key. Connection Closed.");
            closeSession(session);
        } else {
            logger.info("Bot Instructed " + "'" + msg + "'");
        }
    }

    /**
     * Close a {@code Session}
     * 
     * @param session
     */
    private void closeSession(Session session) {
        try {
            if (session != null && session.isOpen())
                session.close();
        } catch (Exception e) {
        }
    }
}
