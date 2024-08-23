package Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

public class ClientSessionHandler extends StompSessionHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientSessionHandler.class);

    private final String username;
    private final mainScreen screen;

    public ClientSessionHandler(String username, mainScreen screen) {
        this.username = username;
        this.screen = screen;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/topic/default", this);
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        if (payload instanceof ChatMessage) {
            ChatMessage msg = (ChatMessage) payload;
            logger.info("Received message: {}", msg.getContent());

            if (!msg.getName().equals(username)) {
                // Handle the received message, e.g., update the UI
                screen.displayMessage(payload, mainScreen.getActiveGroupId());
            }
        }
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ChatMessage.class;
    }

    public static class ChatMessage {
        private String name;
        private String content;
        private int currentGroupId;

        // Constructors, getters, and setters
        public ChatMessage() {}

        public ChatMessage(String name, String content, int currentGroupId) {
            this.name = name;
            this.content = content;
            this.currentGroupId = currentGroupId;
        }

        public int getCurrentGroupId() {
            return currentGroupId;
        }

        public void setCurrentGroupId(int currentGroupId) {
            this.currentGroupId = currentGroupId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
