package statelessservice;

import java.util.concurrent.CompletableFuture;
import java.util.List;

import system.fabric.CancellationToken;

import microsoft.servicefabric.services.communication.runtime.ServiceInstanceListener;
import microsoft.servicefabric.services.runtime.StatelessService;

public class ContainerHealthWatchdogService extends StatelessService {

    @Override
    protected List<ServiceInstanceListener> createServiceInstanceListeners() {
        // TODO: If your service needs to handle user requests, return the list of ServiceInstanceListeners from here.
        return super.createServiceInstanceListeners();
    }

    @Override
    protected CompletableFuture<?> runAsync(CancellationToken cancellationToken) {
    	try {
	    	return CompletableFuture.runAsync(() -> {
	    		Monitor monitor = new Monitor();
		    	while(!cancellationToken.isCancelled()) {
		    		monitor.monitorHealth();
		    		try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		    	}
	    	});
    	} catch (Exception e) {   
    		e.printStackTrace();
    		return super.runAsync(cancellationToken);
    	}
    }
}
