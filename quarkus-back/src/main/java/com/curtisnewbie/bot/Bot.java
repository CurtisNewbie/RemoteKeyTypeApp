package com.curtisnewbie.bot;

import javax.enterprise.context.ApplicationScoped;
import java.awt.*;

import org.jboss.logging.Logger;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Class that simulates keybaord inputs.
 * </p>
 * 
 */
@ApplicationScoped
public class Bot {

    private final Logger logger = Logger.getLogger(this.getClass());
    private final Robot robot = getRobot();

    private Robot getRobot() {
        Robot r = null;
        try {
            r = new Robot();
        } catch (AWTException e) {
            logger.error("This device doesn't support using Robot to simulate keyboard input: " + e.getMessage());
        }
        return r;
    }

    /**
     * Generate key press input
     * 
     * @param keycode keycode, e.g., {@code KeyEvent.VK_1} for 1
     */
    public void keyType(int keycode) {
        if (robot != null) {
            robot.keyPress(keycode);
            robot.delay(40);
            robot.keyRelease(keycode);
        }
    }
}