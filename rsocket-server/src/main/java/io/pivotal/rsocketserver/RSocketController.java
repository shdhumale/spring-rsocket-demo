package io.pivotal.rsocketserver;


import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import io.pivotal.rsocketserver.data.Notification;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
public class RSocketController {

	static final String SERVER = "Server";
	static final String RESPONSE = "Response";
	static final String STREAM = "Stream";
	static final String CHANNEL = "Channel";

	private RSocketRequester rsocketRequester;
	private final List<RSocketRequester> CLIENTS = new ArrayList<>();
	Logger logger = LoggerFactory.getLogger(RSocketController.class);

	@PreDestroy
	void shutdown() {

		logger.info("Detaching all remaining clients...");
		CLIENTS.stream().forEach(requester -> requester.rsocket().dispose());
		logger.info("Shutting down.");
	}

	@ConnectMapping("shell-client")
	void connectShellClientAndAskForTelemetry(RSocketRequester requester,
			@Payload String client) {

		requester.rsocket()
		.onClose()
		.doFirst(() -> {
			// Add all new clients to a client list
			logger.info("Client: {} CONNECTED.", client);
			CLIENTS.add(requester);
		})
		.doOnError(error -> {
			// Warn when channels are closed by clients
			logger.warn("Channel to client {} CLOSED", client);
		})
		.doFinally(consumer -> {
			// Remove disconnected clients from the client list
			CLIENTS.remove(requester);
			logger.info("Client {} DISCONNECTED", client);
		})
		.subscribe();

		// Callback to client, confirming connection
		requester.route("client-status")
		.data("OPEN")
		.retrieveFlux(String.class)
		.doOnNext(s -> logger.info("Client: {} Free Memory: {}.", client, s))
		
		.subscribe();
	}


	/**
	 * This @MessageMapping is intended to be used "subscribe --> stream" style.
	 * When a new request command is received, a new stream of events is started and returned to the client.
	 *
	 * @param request
	 * @return
	 */
	@PreAuthorize("hasRole('USER')")
	@MessageMapping("stream")
	Flux<Notification> stream(final Notification notification, @AuthenticationPrincipal UserDetails user) {
		logger.info("Received stream request: {}", notification.toString());
		logger.info("Stream initiated by '{}' in the role '{}'", user.getUsername(), user.getAuthorities());

		//        return Flux
		//                // create a new indexed Flux emitting one element every second
		//                .interval(Duration.ofSeconds(1))
		//                // create a Flux of new Messages using the indexed Flux
		//                .map(index -> new Message(SERVER, STREAM, index));
		Iterator<RSocketRequester> ir = CLIENTS.iterator();
		int j = 0;
		while(ir.hasNext())
		{
			j ++;
			logger.info("Loop iterated for value of j: {}", j);
			rsocketRequester = (RSocketRequester)ir.next();        	
			//send-from-server
			// Callback to client, confirming connection
			rsocketRequester.route("send-from-server")
			.data("Send from server")
			.retrieveFlux(String.class)
			.doOnNext(s -> logger.info("from send-from-server server side and message from the client in mono is {}------------------>", notification.toString()))
			.subscribe();
//			       	return Flux.interval(Duration.ofSeconds(10))
//			                    .map(i -> new Notification(notification.getDestination(), notification.getSource(), "Sending message to all client from server inside iterator for send-from-server: " + notification.getText(), "Client Id:" + notification.getClientid()));
		}

		return Flux
				.interval(Duration.ofSeconds(60))
				.map(i -> new Notification(notification.getDestination(), notification.getSource(), "In response to: " + notification.getText(), "Client Id:" + notification.getClientid()));

	}
}
