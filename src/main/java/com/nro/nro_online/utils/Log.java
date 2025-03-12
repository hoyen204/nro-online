package com.nro.nro_online.utils;

import org.apache.log4j.Logger;

public class Log {
    private Log(){}

    private static final Logger logger = Logger.getLogger(Log.class);

    public static void log(String text) {
        logger.debug(text);
    }

    public static void success(String text) {
        logger.info(text + " ✅");
    }

    public static void warning(String text) {
        logger.warn("⚠️ " + text);
    }

    public static void error(String text) {
        logger.error("💥 " + text);
    }

    public static void error(Class<?> clazz, Exception ex, String logs) {
        logger.error(clazz.getName() + ": " + logs + " 🔥", ex);
    }

    public static void error(Class<?> clazz, Exception ex) {
        logger.error(clazz.getName() + ": " + ex.getMessage() + " 🚨", ex);
    }
}