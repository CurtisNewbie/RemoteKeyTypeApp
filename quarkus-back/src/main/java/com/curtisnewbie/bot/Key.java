package com.curtisnewbie.bot;

import java.awt.event.KeyEvent;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Enum for keys that specifies the associated string values and KeyEvent
 * </p>
 */
public enum Key {

    ARROW_UP("arrow_up", KeyEvent.VK_UP), ARROW_DOWN("arrow_down", KeyEvent.VK_DOWN),
    ARROW_LEFT("arrow_left", KeyEvent.VK_LEFT), ARROW_RIGHT("arrw_right", KeyEvent.VK_RIGHT);

    private final String str;
    private final int keyEvent;

    private Key(String str, int keyEvent) {
        this.str = str;
        this.keyEvent = keyEvent;
    }

    public int getKeyEvent() {
        return this.keyEvent;
    }

    public String getStr() {
        return this.str;
    }
}