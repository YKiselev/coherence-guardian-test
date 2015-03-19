package org.uze;

import com.tangosol.net.Cluster;
import com.tangosol.net.Guardable;
import com.tangosol.net.Service;
import com.tangosol.net.ServiceFailurePolicy;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Uze on 20.03.2015.
 */
public class MyFailurePolicy implements ServiceFailurePolicy {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onGuardableRecovery(Guardable guardable, Service service) {
        logger.warn("onRecovery({},{})", guardable, service);
        guardable.recover();
    }

    @Override
    public void onGuardableTerminate(Guardable guardable, Service service) {
        logger.error("onTerminate({},{})", guardable, service);
        guardable.terminate();
        halt();
    }

    @Override
    public void onServiceFailed(Cluster cluster) {
        logger.error("onServiceFailed({},{})", cluster);
        halt();
    }

    private void halt() {
        try {
            ThreadUtil.dumpAll(logger);
            LogManager.shutdown();
        } finally {
            Runtime.getRuntime().halt(1);
        }
    }
}
