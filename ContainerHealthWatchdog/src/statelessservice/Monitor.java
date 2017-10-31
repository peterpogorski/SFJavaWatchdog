package statelessservice;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;

import system.fabric.FabricClient;
import system.fabric.HealthClient;
import system.fabric.health.HealthInformation;
import system.fabric.health.HealthState;
import system.fabric.health.ServiceHealthReport;

public class Monitor {
	
	private FabricClient fabricClinet = null;
	private HealthClient healthClient = null;
	private URI serviceUri = null;
	private boolean wasUnhealthy = false;
	
	public Monitor() {		
		
	}
	
	public void monitorHealth() {
		if(this.fabricClinet == null || this.healthClient == null) {
			this.fabricClinet = new FabricClient();
			this.healthClient = this.fabricClinet.getHealthManager();
		}
		
		try {
			serviceUri = new URI("fabric:/WatchdogApplication/ContainerHealthWatchdog");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		
		boolean allContainersHealthy = true;
	
		StringBuilder stringBuilder = new StringBuilder();
		
		try {
			DockerClient docker = DefaultDockerClient.fromEnv().build();
			List<Container> containers = docker.listContainers(ListContainersParam.allContainers());
			for(Container container: containers) {
				ContainerInfo info = docker.inspectContainer(container.id());
				if(info.state() != null && info.state().health() != null) {
					String healthInformation = info.state().health().toString();
					if(healthInformation.contains("unhealthy")) {
						allContainersHealthy = false;
						stringBuilder.append("Image Name: "+ info.name()+ "\n");
					}
				}
			}
		} catch (InterruptedException | DockerException | DockerCertificateException e) {
			e.printStackTrace();
		}
		
		if(!allContainersHealthy) {
			wasUnhealthy = true;
			sendHealthReport(HealthState.Error, stringBuilder.toString());
		} else {
			if(wasUnhealthy) {
				wasUnhealthy = false;
				sendHealthReport(HealthState.Ok, stringBuilder.toString());
			}
		}
		
	}
	
	private void sendHealthReport(HealthState state, String unhealthyContainers) {
		HealthInformation info = new HealthInformation("Container Health Watchdog", "Health", state);
		if(unhealthyContainers != null) {
			info.setDescription("Container Health reported by Watchdog Service \n"+unhealthyContainers);
		} else {
			info.setDescription("Container Health reported by Watchdog Service");
		}
		info.setRemoveWhenExpired(true);
		info.setTimeToLiveSeconds(Duration.ofMinutes(5));
		ServiceHealthReport report = new ServiceHealthReport(serviceUri, info);
		this.healthClient.reportHealth(report);
	}

}
