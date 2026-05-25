package auction.client;

import auction.network.Message;
import auction.network.MessageType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML private Label titleLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    
    // Fields for Registration
    @FXML private VBox registerFieldsBox;
    @FXML private TextField emailField;
    @FXML private TextField fullNameField;
    @FXML private ComboBox<String> roleComboBox;

    @FXML private Button actionButton;
    @FXML private Hyperlink toggleLink;
    @FXML private Label statusLabel;

    private boolean isLoginMode = true;

    @FXML
    public void initialize() {
        statusLabel.setText("");
        roleComboBox.getItems().addAll("BIDDER", "SELLER", "ADMIN");
        roleComboBox.setValue("BIDDER");
        setMode(true);
    }

    @FXML
    private void handleAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("⚠️ Vui lòng nhập tài khoản và mật khẩu!");
            return;
        }

        actionButton.setDisable(true);
        statusLabel.setText("Đang kết nối tới Server...");

        // Connect first if not connected
        boolean connected = ClientApp.getNetwork().connect(this::onMessageReceived);

        if (!connected) {
            statusLabel.setText("❌ Không thể kết nối tới Server. Vui lòng thử lại.");
            actionButton.setDisable(false);
            return;
        }

        if (isLoginMode) {
            // Send Login request
            Message loginMsg = new Message(MessageType.LOGIN_REQUEST);
            loginMsg.put("userId", username); // server expects userId for username
            loginMsg.put("password", password);
            ClientApp.getNetwork().sendMessage(loginMsg);
            statusLabel.setText("⏳ Đang xác thực...");
        } else {
            // Send Register request
            String email = emailField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String role = roleComboBox.getValue();

            if (email.isEmpty() || fullName.isEmpty()) {
                statusLabel.setText("⚠️ Vui lòng điền đầy đủ thông tin đăng ký!");
                actionButton.setDisable(false);
                return;
            }

            Message registerMsg = new Message(MessageType.REGISTER_REQUEST);
            registerMsg.put("username", username);
            registerMsg.put("password", password);
            registerMsg.put("email", email);
            registerMsg.put("fullName", fullName);
            registerMsg.put("role", role);
            ClientApp.getNetwork().sendMessage(registerMsg);
            statusLabel.setText("⏳ Đang gửi yêu cầu đăng ký...");
        }
    }

    @FXML
    private void handleToggleMode() {
        setMode(!isLoginMode);
    }

    private void setMode(boolean loginMode) {
        this.isLoginMode = loginMode;
        if (loginMode) {
            titleLabel.setText("🔑 Đăng Nhập Hệ Thống");
            registerFieldsBox.setVisible(false);
            registerFieldsBox.setManaged(false);
            actionButton.setText("🚀 Kết nối và Đăng nhập");
            toggleLink.setText("Chưa có tài khoản? Đăng ký ngay");
        } else {
            titleLabel.setText("📝 Đăng Ký Tài Khoản");
            registerFieldsBox.setVisible(true);
            registerFieldsBox.setManaged(true);
            actionButton.setText("✨ Đăng Ký Tài Khoản");
            toggleLink.setText("Đã có tài khoản? Đăng nhập");
        }
        statusLabel.setText("");
    }

    private void onMessageReceived(Message msg) {
        if (msg.getType() == MessageType.LOGIN_RESPONSE) {
            String status = msg.get("status");
            if ("SUCCESS".equals(status)) {
                try {
                    statusLabel.setText("✅ Đăng nhập thành công!");
                    
                    // Save Session info
                    ClientNetwork net = ClientApp.getNetwork();
                    net.setCurrentUserId(msg.get("userId"));
                    net.setCurrentUsername(msg.get("username"));
                    net.setCurrentUserRole(msg.get("role"));
                    net.setCurrentUserBalance(Double.parseDouble(msg.get("balance")));

                    // Scene switching based on user role
                    String role = net.getCurrentUserRole();
                    if ("SELLER".equalsIgnoreCase(role)) {
                        ClientApp.switchToSeller();
                    } else if ("ADMIN".equalsIgnoreCase(role)) {
                        ClientApp.switchToAdmin();
                    } else {
                        ClientApp.switchToAuctionList();
                    }
                } catch (Exception e) {
                    statusLabel.setText("❌ Lỗi chuyển màn hình: " + e.getMessage());
                    e.printStackTrace();
                    actionButton.setDisable(false);
                }
            } else {
                statusLabel.setText("❌ Đăng nhập thất bại: " + msg.get("reason"));
                actionButton.setDisable(false);
            }
        } else if (msg.getType() == MessageType.REGISTER_RESPONSE) {
            String status = msg.get("status");
            if ("SUCCESS".equals(status)) {
                statusLabel.setText("✅ Đăng ký thành công! Hãy đăng nhập.");
                setMode(true); // Switch to login mode
            } else {
                statusLabel.setText("❌ Đăng ký thất bại: " + msg.get("reason"));
            }
            actionButton.setDisable(false);
        } else if (msg.getType() == MessageType.ERROR) {
            statusLabel.setText("❌ Lỗi: " + msg.get("reason"));
            actionButton.setDisable(false);
        }
    }
}
