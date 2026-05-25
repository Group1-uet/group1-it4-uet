package auction.engine;

import auction.network.Message;
import auction.server.ClientHandler;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager {
    private static NotificationManager instance;
    // Sử dụng CopyOnWriteArrayList để đảm bảo thread-safe khi thêm/xoá client hoặc duyệt danh sách
    private final CopyOnWriteArrayList<ClientHandler> activeClients;

    private NotificationManager() {
        activeClients = new CopyOnWriteArrayList<>();
    }

    public static synchronized NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    public void registerClient(ClientHandler client) {
        activeClients.add(client);
    }

    public void unregisterClient(ClientHandler client) {
        activeClients.remove(client);
    }

    // Broadcast tới tất cả client
    public void broadcast(Message message) {
        for (ClientHandler client : activeClients) {
            client.sendMessage(message);
        }
    }

    // Gửi tin nhắn tới một người cụ thể (thông qua username hoặc id)
    public void sendToUser(String userId, Message message) {
        for (ClientHandler client : activeClients) {
            if (userId.equals(client.getUserId())) {
                client.sendMessage(message);
                break;
            }
        }
    }
}
