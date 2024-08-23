package Connection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SignUp extends JFrame {
    private JPanel SignUpPanel;
    private JTextField tfUsername;
    private JTextField tfPassword;
    private JButton btnSignUp;
    private JLabel AlertBox;
    private JLabel PageTitle;
    private JButton btnBackToLogin;

    public SignUp() {
        setContentPane(SignUpPanel);
        PageTitle.setText("CREATE AN ACCOUNT!");
        setSize(600, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        SignUpPanel.getRootPane().setDefaultButton(btnSignUp);

        btnSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String username = tfUsername.getText();
                String password = new String(tfPassword.getText());
                try {
                    if (!username.isEmpty() || !password.isEmpty()){
                        createUser(username, password);
                    }
                    else{
                        AlertBox.setText("Enter username or password please!");
                    }
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnBackToLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ScreenOpening.openRelativeScreen(SignUp.this, new Login());
                        dispose();
                    }
                });
            }
        });

        SignUpPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "pressEnter");
        SignUpPanel.getActionMap().put("pressEnter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tfUsername.hasFocus() || tfPassword.hasFocus()) {
                    btnSignUp.doClick();
                } else {
                    btnBackToLogin.doClick();
                }
            }
        });
    }

    private void createUser(String username, String password) throws IOException, InterruptedException {
        EndPoint endPoint = new EndPoint();
        String url = endPoint.getCreateUser();
        ApiConnector connector = new ApiConnector(url);

        String params = "username=" + username + "&password=" + password;
        String response = connector.sendPostRequest(params);

        if (response.contains("success")) {
            SwingUtilities.invokeLater(() -> {

                JOptionPane.showMessageDialog(this, "Account created successfully! Please log in.");

                String message = "You can now log in !";
                Login loginWindowForNewUser = new Login();
                loginWindowForNewUser.YouCanLoginMessage(message);

                dispose();
            });
        } else {
            AlertBox.setText("Username is taken!");
        }
    }
}