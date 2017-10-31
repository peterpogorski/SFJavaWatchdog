package statelessservice;

import java.time.Duration;
import java.util.logging.Logger;
import java.util.logging.Level;

import microsoft.servicefabric.services.runtime.ServiceRuntime;

public class ContainerHealthWatchdogServiceHost {

    private static final Logger logger = Logger.getLogger(ContainerHealthWatchdogServiceHost.class.getName());

    public static void main(String[] args) throws Exception{
        try {
            ServiceRuntime.registerStatelessServiceAsync("ContainerHealthWatchdogType", (context)-> new ContainerHealthWatchdogService(), Duration.ofSeconds(10));
            logger.log(Level.INFO, "Registered stateless service of type ContainerHealthWatchdogType");
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception occurred", ex);
            throw ex;
        }
    }
}
