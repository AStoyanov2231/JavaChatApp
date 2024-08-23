package Connection;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Client {

    private static final String URL = "ws://localhost:8080/chat-websocket";
    private StompSession session;

    private final String username;
    private final mainScreen screen;

    private List<StompSession.Subscription> subscriptions = new ArrayList<>();

    public Client(String username, mainScreen screen) throws ExecutionException, InterruptedException {

        this.username = username;
        this.screen = screen;

        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSessionHandler clientSessionHandler = new ClientSessionHandler(username, screen);
        this.session = stompClient.connect(URL, clientSessionHandler).get();
    }

    // Method to subscribe to a topic and store the subscription
    public void subscribeToTopic(String topic) {
        if (session != null && session.isConnected()) {
            StompSession.Subscription subscription = session.subscribe(topic, new ClientSessionHandler(username, screen));
            subscriptions.add(subscription);
        }
    }

    // Method to unsubscribe from all topics
    public void unsubscribeAll() {
        for (StompSession.Subscription subscription : subscriptions) {
            subscription.unsubscribe();
        }
        subscriptions.clear(); // Clear the list after unsubscribing
    }

    public void sendPublicMessage(String destination, Object payload) {
        if (session != null && session.isConnected()) {
            session.send(destination, payload);
        }
    }

    public void disconnect() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }
}