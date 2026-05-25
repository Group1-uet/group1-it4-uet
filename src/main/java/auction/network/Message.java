package auction.network;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private MessageType type;
    private Map<String, String> payload;

    public Message(MessageType type) {
        this.type = type;
        this.payload = new HashMap<>();
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Map<String, String> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, String> payload) {
        this.payload = payload;
    }

    public void put(String key, String value) {
        this.payload.put(key, value);
    }

    public String get(String key) {
        return this.payload.get(key);
    }
}
