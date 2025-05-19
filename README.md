# spring-rsocket-demo
This shows Notifying, broadcasting all the client on addition of new client in the list in RSocket
Project Structure

The project is organized into two main modules: rsocket-client and rsocket-server.  It also includes supporting files at the root level, such as README.md, LICENSE, rsc-commands.txt, and rsc-help.txt.



rsocket-client: This directory contains the source code for the RSocket client application. It includes: 

src/main/java: The main Java source code for the client. 

RSocketShellClient.java: Likely a client that interacts with the server using RSocket.

RsocketShellClientApplication.java: The main Spring Boot application class for the client.

data/Message.java and Notification.java: Java classes representing data structures used for communication with the server.

src/main/resources: Application configuration, such as application.properties.

src/test/java: Test classes for the client.

pom.xml: Maven build file to manage dependencies and build the client application.

rsocket-server: This directory contains the source code for the RSocket server application. It mirrors the client structure: 

src/main/java: The main Java source code for the server. 

RSocketController.java: Spring RSocket controller to handle client requests.

RSocketSecurityConfig.java: Configuration for RSocket security.

RsocketServerApplication.java: The main Spring Boot application class for the server.

RSocketServerResumptionConfig.java: Configuration for RSocket resumption.

data/Message.java and Notification.java: Data structure classes, likely shared with the client.

src/main/resources: application.properties for server configuration.

src/test/java: Test classes for the server.

pom.xml: Maven build file for the server.

mvnw and .mvn/wrapper: Maven Wrapper, which allows building the project without requiring Maven to be installed globally.

README.md: Provides a basic description of the project.

LICENSE: The project's license (GNU General Public License v3).

rsc-commands.txt and rsc-help.txt: Files related to using the rsc (RSocket CLI) tool.
Functionality

Based on the README.md, the primary purpose of this demo is to show "Notifying, broadcasting all the client on addition of new client in the list in RSocket".  This suggests the server can:

Notify clients: Send updates or messages to connected clients.

Broadcast to all clients: Send the same message to multiple connected clients.

Handle client addition: Specifically, it seems to demonstrate how the server informs other clients when a new client connects.

RSocket CLI (rsc)

The rsc-commands.txt and rsc-help.txt files indicate the project likely uses the RSocket command-line interface (rsc) for testing or interacting with the server.  rsc is a tool that allows sending RSocket requests from the command line.  The provided commands show examples of using rsc to perform request-response and fire-and-forget interactions with the server.

