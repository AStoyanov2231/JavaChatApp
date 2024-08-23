package Connection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SettingsScreen extends JFrame {
    private JPanel SettingsPanel;
    private JButton btnChangePassword;
    private JButton btnLogOut;
    private JButton btnDeleteAccount;
    private JLabel lbSettings;
    private JTextField textField1;
    private JTextField textField3;
    private JTextField textField2;
    private JButton ContinueButton;
    private JLabel TopLabel;
    private JLabel MidLabel;
    private JLabel BottomLabel;
    private JButton btnCancel;
    private JLabel AlertBox;

    private boolean isDeletingUser = false;
    private boolean isChangingPassword = false;

    public SettingsScreen(LoggedUser loggedUser) {
        setContentPane(SettingsPanel);
        setSize(800, 620);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        textField1.setVisible(false);
        textField3.setVisible(false);
        textField2.setVisible(false);
        ContinueButton.setVisible(false);

        btnChangePassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField1.setVisible(true);
                textField2.setVisible(true);
                textField3.setVisible(true);

                TopLabel.setText("Enter username");
                MidLabel.setText("Old password");
                BottomLabel.setText("New password");

                isDeletingUser = false;
                isChangingPassword = true;

                ContinueButton.setVisible(true);
                ContinueButton.setText("Change Password");
            }
        });

        btnLogOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField1.setVisible(false);
                textField2.setVisible(false);
                textField3.setVisible(false);

                TopLabel.setText("");
                MidLabel.setText("");
                BottomLabel.setText("");

                isDeletingUser = false;
                isChangingPassword= false;

                ScreenOpening.openRelativeScreen(SettingsScreen.this, new Login());
                dispose();
            }
        });

        btnDeleteAccount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField1.setVisible(true);
                textField2.setVisible(true);
                textField3.setVisible(false);

                TopLabel.setText("Enter username");
                MidLabel.setText("Enter password");
                BottomLabel.setText("");

                ContinueButton.setVisible(true);
                ContinueButton.setText("Delete User");

                isDeletingUser = true;
                isChangingPassword = false;
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ScreenOpening.openRelativeScreen(SettingsScreen.this, new mainScreen(loggedUser));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                isDeletingUser = false;
                isChangingPassword = false;
                dispose();
            }
        });
        ContinueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isDeletingUser)
                {
                    String username = textField1.getText();
                    String password = textField2.getText();

                    deleteUser(username,password);
                }
                else if (isChangingPassword)
                {
                    String username = textField1.getText();
                    String oldPassword = textField2.getText();
                    String newPassword = textField3.getText();

                    changeUserPassword(username, oldPassword, newPassword);
                }
            }
        });
    }

    private void changeUserPassword(String username, String oldPassword, String newPassword) {
        try{
            EndPoint endPoint = new EndPoint();
            String url = endPoint.getChangePassword();
            ApiConnector connector = new ApiConnector(url);

            String params = "username=" + username + "&oldPassword=" + oldPassword + "&newPassword=" + newPassword;
            String response = connector.sendPostRequest(params);

            if (response.contains("Password changed successfully")){
                AlertBox.setText("Password changed successfully");
                textField1.setText("");
                textField2.setText("");
                textField3.setText("");
            }
            else AlertBox.setText("Couldn't change password!");
        } catch (IOException e){
            throw new RuntimeException(e);
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private void deleteUser(String username, String password) {
        try {
            EndPoint endPoint = new EndPoint();
            String url = endPoint.getDeleteUser();
            ApiConnector connector = new ApiConnector(url);

            String params = "?username=" + username + "&password=" + password;
            String response = connector.sendDeleteRequest(params);

            if (response.contains("User deleted successfully!")) {
                SwingUtilities.invokeLater(() -> {
                    ScreenOpening.openRelativeScreen(SettingsScreen.this, new SignUp());
                    dispose();
                });
            } else {
                // TODO: create delete Method in ApiConnector
                AlertBox.setText("delete didn't work");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}