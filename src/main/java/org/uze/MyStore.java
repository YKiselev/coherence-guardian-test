package org.uze;

import com.tangosol.net.GuardSupport;
import com.tangosol.net.Guardable;
import com.tangosol.net.Guardian.GuardContext;
import com.tangosol.net.cache.CacheStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by Uze on 19.03.2015.
 */
public class MyStore implements CacheStore {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void store(Object key, Object value) {
        storeAll(Collections.singletonMap(key, value));
    }

    @Override
    public void storeAll(Map map) {
        final GuardContext ctx = GuardSupport.getThreadContext();
        if (ctx != null) {
            final GuardContext ctx2 = ctx.getGuardian().guard(new MyGuard());
            GuardSupport.setThreadContext(ctx2);
            ctx.heartbeat(TimeUnit.DAYS.toMillis(365));
        }
        try {
            final long t0 = System.nanoTime();
            final long duration = ThreadLocalRandom.current().nextLong(100, 10000);
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                logger.error("Sleep failed!", e);
            }
            logger.info("storeAll({}) took {} ms.", map.size(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0));
        } finally {
            if (ctx != null) {
                final GuardContext ctx2 = GuardSupport.getThreadContext();
                GuardSupport.setThreadContext(ctx);
                ctx2.release();
                ctx.heartbeat();
            }
        }
    }

    @Override
    public void erase(Object key) {
    }

    @Override
    public void eraseAll(Collection keys) {
    }

    @Override
    public Object load(Object key) {
        final Map map = loadAll(Collections.singletonList(key));
        if (map == null || map.isEmpty()) {
            return null;
        }
        return map.entrySet().iterator().next();
    }

    @Override
    public Map loadAll(Collection keys) {
        return null;
    }

    /**
     *
     */
    static class MyGuard implements Guardable {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private GuardContext context;

        @Override
        public void setContext(GuardContext guardContext) {
            this.context = guardContext;
        }

        @Override
        public GuardContext getContext() {
            return context;
        }

        @Override
        public void recover() {
            logger.warn("Got recover signal");
            context.heartbeat();
        }

        @Override
        public void terminate() {
            logger.warn("Got terminate signal");
        }
    }
}
