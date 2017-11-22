# SFJavaWatchdog
A simple Java application that can be deployed on a Service Fabric application to monitor the health status of the containers that are also running on the cluster. The application will report the health of the containers to the Service Fabric Explorer.

## Configuration
In order for the health of a container application to be reported a Health Check must be added to the dockerfile.
