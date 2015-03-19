package org.uze;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.Cluster;
import com.tangosol.net.NamedCache;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Uze on 19.03.2015.
 */
public class App {

    public static void main(String[] args) {
        final Cluster cluster = CacheFactory.ensureCluster();
        try {
            final NamedCache cache1 = CacheFactory.getCache("Cache-1");

            cache1.put("key-1", "value-0123456789");

            final ThreadLocalRandom random = ThreadLocalRandom.current();
            while (true) {
                Thread.sleep(250);

                if (random.nextBoolean()) {
                    final int count = random.nextInt(1, 500);
                    final Map<String, String> map = new HashMap<>(count);
                    for (int i = 0; i < count; i++) {
                        map.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());
                    }

                    cache1.putAll(map);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cluster.shutdown();
        }
    }
}
