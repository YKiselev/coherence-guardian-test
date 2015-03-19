package org.uze;

import org.slf4j.Logger;

import java.util.Map;

/**
 * Created by Uze on 20.03.2015.
 */
public final class ThreadUtil {

    private ThreadUtil() {
    }

    public static void dumpAll(Logger logger) {
        final StringBuilder sb = new StringBuilder("\n");
        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            sb.append(entry.getKey()).append('\n');
            for (StackTraceElement element : entry.getValue()) {
                sb.append("  at ").append(element).append('\n');
            }
        }
        logger.info(sb.toString());
    }

}
