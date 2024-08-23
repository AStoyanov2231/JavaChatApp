package Connection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Login extends JFrame {
    private JPanel LoginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton newUserButton;
    private JLabel lbWelcome;
    private JLabel LogInPageMessage;

    public Login() {
        setContentPane(LoginPanel);
        setSize(800,620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        LoginPanel.getRootPane().setDefaultButton(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                loginUser(username, password);
            }
        });

        // Goes to registration
        newUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ScreenOpening.openRelativeScreen(Login.this, new SignUp());
                dispose();
            }
        });

        LoginPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "pressEnter");
        LoginPanel.getActionMap().put("pressEnter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (usernameField.hasFocus() || passwordField.hasFocus()) {
                    loginButton.doClick();
                } else {
                    newUserButton.doClick();
                }
            }
        });
    }

    public void YouCanLoginMessage(String message) {
        LogInPageMessage.setText(message);
    }

    private void loginUser(String username, String password) {
        try {
            EndPoint endPoint = new EndPoint();
            String url = endPoint.getLogin();
            ApiConnector connector = new ApiConnector(url);

            String params = "username=" + username + "&password=" + password;
            String response = connector.sendPostRequest(params);

            if (response.contains("Login successful!")) {
                LoggedUser loggedUser = new LoggedUser();
                loggedUser.setUsername(username);

                ScreenOpening.openRelativeScreen(Login.this, new mainScreen(loggedUser));
                dispose();

            } else {
                lbWelcome.setText(response);
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            lbWelcome.setText("Exception: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}
