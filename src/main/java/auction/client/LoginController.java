package auction.client;

import auction.network.Message;
import auction.network.MessageType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginController {

    // Tab buttons
    @FXML private Button btnTabLogin;
    @FXML private Button btnTabRegister;
    @FXML private Button btnTabForgot;

    // Panels
    @FXML private VBox loginPanel;
    @FXML private VBox registerPanel;
    @FXML private VBox forgotPanel;

    // Login Fields
    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Button loginButton;

    // Register Fields
    @FXML private TextField regUsernameField;
    @FXML private PasswordField regPasswordField;
    @FXML private TextField regEmailField;
    @FXML private ComboBox<String> regRoleComboBox;
    @FXML private Button registerButton;

    // Forgot Password Fields
    @FXML private TextField forgotUsernameField;
    @FXML private TextField forgotEmailField;
    @FXML private PasswordField forgotPasswordField;
    @FXML private Button forgotButton;

    // Feedback Status
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        statusLabel.setText("");
        regRoleComboBox.getItems().addAll("BIDDER", "SELLER", "ADMIN");
        regRoleComboBox.setValue("BIDDER");
        showLoginPanel(); // Default to Login view
    }

    // --- TAB SWITCHING LOGIC ---
    
    @FXML
    private void showLoginPanel() {
        loginPanel.setVisible(true);
        loginPanel.setManaged(true);
        registerPanel.setVisible(false);
        registerPanel.setManaged(false);
        forgotPanel.setVisible(false);
        forgotPanel.setManaged(false);
        statusLabel.setText("");

        setTabActive(btnTabLogin);
        setTabInactive(btnTabRegister);
        setTabInactive(btnTabForgot);
    }

    @FXML
    private void showRegisterPanel() {
        loginPanel.setVisible(false);
        loginPanel.setManaged(false);
        registerPanel.setVisible(true);
        registerPanel.setManaged(true);
        forgotPanel.setVisible(false);
        forgotPanel.setManaged(false);
        statusLabel.setText("");

        setTabInactive(btnTabLogin);
        setTabActive(btnTabRegister);
        setTabInactive(btnTabForgot);
    }

    @FXML
    private void showForgotPanel() {
        loginPanel.setVisible(false);
        loginPanel.setManaged(false);
        registerPanel.setVisible(false);
        registerPanel.setManaged(false);
        forgotPanel.setVisible(true);
        forgotPanel.setManaged(true);
        statusLabel.setText("");

        setTabInactive(btnTabLogin);
        setTabInactive(btnTabRegister);
        setTabActive(btnTabForgot);
    }

    private void setTabActive(Button button) {
        button.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 16; -fx-cursor: hand; -fx-padding: 8 0;");
    }

    private void setTabInactive(Button button) {
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: #a8a8b3; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 16; -fx-cursor: hand; -fx-padding: 8 0;");
    }

    // --- BUTTON EVENT HANDLERS ---

    @FXML
    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("⚠️ Vui lòng nhập đầy đủ Tên đăng nhập và Mật khẩu!");
            return;
        }

        loginButton.setDisable(true);
        statusLabel.setText("⏳ Đang kết nối tới Server...");

        boolean connected = ClientApp.getNetwork().connect(this::onMessageReceived);

        if (connected) {
            Message loginMsg = new Message(MessageType.LOGIN_REQUEST);
            loginMsg.put("userId", username); // Server expects username under 'userId'
            loginMsg.put("password", password);
            ClientApp.getNetwork().sendMessage(loginMsg);
            statusLabel.setText("⏳ Đang xác thực...");
        } else {
            statusLabel.setText("❌ Không thể kết nối tới Server. Vui lòng thử lại.");
            loginButton.setDisable(false);
        }
    }

    @FXML
    private void handleRegister() {
        String username = regUsernameField.getText().trim();
        String password = regPasswordField.getText().trim();
        String email = regEmailField.getText().trim();
        String role = regRoleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || role == null) {
            statusLabel.setText("⚠️ Vui lòng điền đầy đủ thông tin đăng ký!");
            return;
        }

        registerButton.setDisable(true);
        statusLabel.setText("⏳ Đang kết nối gửi yêu cầu đăng ký...");

        boolean connected = ClientApp.getNetwork().connect(this::onMessageReceived);

        if (connected) {
            Message regMsg = new Message(MessageType.REGISTER_REQUEST);
            regMsg.put("username", username);
            regMsg.put("password", password);
            regMsg.put("email", email);
            regMsg.put("fullName", username); // Sử dụng username làm họ tên mặc định
            regMsg.put("role", role);
            ClientApp.getNetwork().sendMessage(regMsg);
            statusLabel.setText("⏳ Đang gửi yêu cầu đăng ký...");
        } else {
            statusLabel.setText("❌ Không thể kết nối tới Server để đăng ký.");
            registerButton.setDisable(false);
        }
    }

    @FXML
    private void handleForgotPassword() {
        String username = forgotUsernameField.getText().trim();
        String email = forgotEmailField.getText().trim();
        String newPassword = forgotPasswordField.getText().trim();

        if (username.isEmpty() || email.isEmpty() || newPassword.isEmpty()) {
            statusLabel.setText("⚠️ Vui lòng điền đầy đủ thông tin khôi phục!");
            return;
        }

        forgotButton.setDisable(true);
        statusLabel.setText("⏳ Đang gửi yêu cầu khôi phục mật khẩu...");

        boolean connected = ClientApp.getNetwork().connect(this::onMessageReceived);

        if (connected) {
            Message forgotMsg = new Message(MessageType.FORGOT_PASSWORD_REQUEST);
            forgotMsg.put("username", username);
            forgotMsg.put("email", email);
            forgotMsg.put("newPassword", newPassword);
            ClientApp.getNetwork().sendMessage(forgotMsg);
        } else {
            statusLabel.setText("❌ Không thể kết nối tới Server để khôi phục mật khẩu.");
            forgotButton.setDisable(false);
        }
    }

    // --- INCOMING NETWORK MESSAGES ---

    private void onMessageReceived(Message msg) {
        if (msg.getType() == MessageType.LOGIN_RESPONSE) {
            String status = msg.get("status");
            if ("SUCCESS".equals(status)) {
                try {
                    statusLabel.setText("✅ Đăng nhập thành công!");
                    
                    // Save Session info to client network
                    ClientNetwork net = ClientApp.getNetwork();
                    net.setCurrentUserId(msg.get("userId"));
                    net.setCurrentUsername(msg.get("username"));
                    net.setCurrentUserRole(msg.get("role"));
                    net.setCurrentUserBalance(Double.parseDouble(msg.get("balance")));

                    // Route to role-specific dashboard
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
                    loginButton.setDisable(false);
                }
            } else {
                statusLabel.setText("❌ " + msg.get("reason"));
                loginButton.setDisable(false);
            }
        } else if (msg.getType() == MessageType.REGISTER_RESPONSE) {
            registerButton.setDisable(false);
            String status = msg.get("status");
            if ("SUCCESS".equals(status)) {
                statusLabel.setText("🎉 Đăng ký thành công! Hãy đăng nhập ngay.");
                showLoginPanel();
                loginUsernameField.setText(regUsernameField.getText().trim());
                loginPasswordField.clear();
            } else {
                statusLabel.setText("❌ Đăng ký thất bại: " + msg.get("reason"));
            }
        } else if (msg.getType() == MessageType.FORGOT_PASSWORD_RESPONSE) {
            forgotButton.setDisable(false);
            String status = msg.get("status");
            if ("SUCCESS".equals(status)) {
                statusLabel.setText("🎉 Đổi mật khẩu thành công! Hãy đăng nhập bằng mật khẩu mới.");
                showLoginPanel();
                loginUsernameField.setText(forgotUsernameField.getText().trim());
                loginPasswordField.clear();
            } else {
                statusLabel.setText("❌ Khôi phục thất bại: " + msg.get("reason"));
            }
        } else if (msg.getType() == MessageType.ERROR) {
            statusLabel.setText("❌ Lỗi: " + msg.get("reason"));
            loginButton.setDisable(false);
            registerButton.setDisable(false);
            forgotButton.setDisable(false);
        }
    }
}
