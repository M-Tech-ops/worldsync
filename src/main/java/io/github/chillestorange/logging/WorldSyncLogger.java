package io.github.chillestorange.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WorldSyncLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger("worldsync");

    private WorldSyncLogger() {
    }

    public static void info(String format, Object... args) {
        LOGGER.info(format, args);
    }

    public static void warn(String msg) {
        LOGGER.warn(msg);
    }

    public static void error(String msg) {
        LOGGER.error(msg);
    }

    public static void error(String msg, Throwable t) {
        LOGGER.error(msg, t);
    }

    public static void debug(String msg) {
        LOGGER.debug(msg);
    }
}