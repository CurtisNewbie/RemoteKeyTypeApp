package com.curtisnewbie.bot;

import java.awt.event.KeyEvent;
import static java.util.Map.entry;
import java.util.Map;

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
public final class Key {

    public static final Map<String, Integer> KEYMAP = Map.ofEntries(entry("arrow_up", KeyEvent.VK_UP),
            entry("arrow_down", KeyEvent.VK_DOWN), entry("arrow_left", KeyEvent.VK_LEFT),
            entry("arrow_right", KeyEvent.VK_RIGHT), entry("space", KeyEvent.VK_SPACE), entry("q", KeyEvent.VK_Q),
            entry("w", KeyEvent.VK_W), entry("e", KeyEvent.VK_E), entry("r", KeyEvent.VK_R), entry("t", KeyEvent.VK_T),
            entry("y", KeyEvent.VK_Y), entry("u", KeyEvent.VK_U), entry("i", KeyEvent.VK_I), entry("o", KeyEvent.VK_O),
            entry("p", KeyEvent.VK_P), entry("a", KeyEvent.VK_A), entry("s", KeyEvent.VK_S), entry("d", KeyEvent.VK_D),
            entry("f", KeyEvent.VK_F), entry("g", KeyEvent.VK_G), entry("h", KeyEvent.VK_H), entry("j", KeyEvent.VK_J),
            entry("k", KeyEvent.VK_K), entry("l", KeyEvent.VK_L), entry("z", KeyEvent.VK_Z), entry("x", KeyEvent.VK_X),
            entry("c", KeyEvent.VK_C), entry("v", KeyEvent.VK_V), entry("b", KeyEvent.VK_B), entry("n", KeyEvent.VK_N),
            entry("m", KeyEvent.VK_M), entry("backspace", KeyEvent.VK_BACK_SPACE), entry("comma", KeyEvent.VK_COMMA),
            entry("period", KeyEvent.VK_PERIOD), entry("enter", KeyEvent.VK_ENTER), entry("slash", KeyEvent.VK_SLASH),
            entry("bslash", KeyEvent.VK_BACK_SLASH));
}