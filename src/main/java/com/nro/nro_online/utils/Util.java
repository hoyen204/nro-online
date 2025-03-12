package com.nro.nro_online.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Util {
    private static final Random rand = ThreadLocalRandom.current();

    private Util() {
    }

    public static boolean canDoWithTime(long lastTime, long miniTimeTarget) {
        return System.currentTimeMillis() - lastTime > miniTimeTarget;
    }

    public static String replace(String text, String regex, String replacement) {
        return text.replace(regex, replacement);
    }

    public static int nextInt(int max) {
        return rand.nextInt(max);
    }

    public static int nextInt(int min, int max) {
        return min + rand.nextInt(max - min + 1);
    }
}
