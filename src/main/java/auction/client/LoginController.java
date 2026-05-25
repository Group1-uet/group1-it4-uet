package auction.client;

import auction.network.Message;
import auction.network.MessageType;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private Button loginButton;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        statusLabel.setText("");
    }

    @FXML
    private void handleLogin() {
        String userId = usernameField.getText().trim();
        if (userId.isEmpty()) {
            statusLabel.setText("Vui lòng nhập tên người dùng!");
            return;
        }

        loginButton.setDisable(true);
        statusLabel.setText("Đang kết nối tới Server...");

        boolean connected = ClientApp.getNetwork().connect(userId, this::onMessageReceived);

        if (!connected) {
            statusLabel.setText("❌ Không thể kết nối tới Server. Vui lòng thử lại.");
            loginButton.setDisable(false);
        }
    }

    private void onMessageReceived(Message msg) {
        if (msg.getType() == MessageType.LOGIN_RESPONSE) {
            String status = msg.get("status");
            if ("SUCCESS".equals(status)) {
                try {
                    statusLabel.setText("✅ Đăng nhập thành công!");
                    ClientApp.switchToAuctionList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                statusLabel.setText("❌ Đăng nhập thất bại!");
                loginButton.setDisable(false);
            }
        } else if (msg.getType() == MessageType.ERROR) {
            statusLabel.setText("❌ Lỗi: " + msg.get("reason"));
            loginButton.setDisable(false);
        }
    }
}
