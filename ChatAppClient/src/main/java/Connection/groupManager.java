package Connection;

import Connection.Objects.Group;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class groupManager extends JFrame {
    private JPanel panel1;
    private JButton searchMode;
    private JButton confirmationButton;
    private JButton backButton;
    private JButton createMode;
    private JTextField textField1;
    private JLabel resultLabel;
    private JButton actionButton;
    private JLabel prompt;
    private JLabel lbGroupManager;

    private boolean searchModeActive = false;
    private boolean createModeActive = false;

    public groupManager(LoggedUser loggedUser){
        setContentPane(panel1);
        setTitle("Search and create groups");
        setSize(400, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        panel1.getRootPane().setDefaultButton(actionButton);

        confirmationButton.setVisible(false);
        textField1.setVisible(false);
        actionButton.setVisible(false);
        confirmationButton.setVisible(false);

        searchMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchModeActive = true;
                createModeActive = false;

                confirmationButton.setVisible(false);
                confirmationButton.setVisible(false);
                resultLabel.setVisible(false);

                textField1.setVisible(true);
                prompt.setText("Enter group name");
                actionButton.setVisible(true);
                actionButton.setText("Search");
            }
        });
        confirmationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupName = textField1.getText();

                if (searchModeActive){
                    try {
                        joinGroup(groupName, loggedUser.getUsername());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    resultLabel.setText("Successfully joined group!");
                    confirmationButton.setVisible(false);
                } else {
                    try {
                        createGroup(groupName, loggedUser.getUsername());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    resultLabel.setText("Successfully created group!");
                    confirmationButton.setVisible(false);
                }
            }
        });
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String groupName = textField1.getText();
                confirmationButton.setVisible(false);
                resultLabel.setText("");

                if (groupName.contains("/")) {
                    resultLabel.setVisible(true);
                    resultLabel.setText("Invalid group name");
                } else {
                    if (searchModeActive && !groupName.isBlank()){
                        try {
                            if (searchForGroup(groupName)){

                                System.out.println(isAlreadyInGroup(loggedUser.getUsername(), groupName));

                                if (isAlreadyInGroup(loggedUser.getUsername(), groupName)) {
                                    resultLabel.setVisible(true);
                                    resultLabel.setText("You are already in this group.");
                                } else {
                                    resultLabel.setVisible(true);
                                    resultLabel.setText("Found a group. Join it?");
                                    confirmationButton.setText("Join group");
                                    confirmationButton.setVisible(true);
                                }


                            } else {
                                resultLabel.setVisible(true);
                                resultLabel.setText("No such group found");
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else if (!searchModeActive && !groupName.isBlank()) {

                        try {
                            if (searchForGroup(groupName)){
                                resultLabel.setVisible(true);
                                resultLabel.setText("Group already exists");
                            } else {
                                resultLabel.setVisible(true);
                                resultLabel.setText("Group name is free. Create group?");
                                confirmationButton.setText("Create group");
                                confirmationButton.setVisible(true);
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });
        createMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                createModeActive = true;
                searchModeActive = false;

                confirmationButton.setVisible(false);
                actionButton.setVisible(false);
                resultLabel.setText("");
                resultLabel.setVisible(false);

                prompt.setText("How will the group be called?");
                textField1.setVisible(true);
                actionButton.setVisible(true);
                actionButton.setText("Check for availability");

            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ScreenOpening.openRelativeScreen(groupManager.this, new mainScreen(loggedUser));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                dispose();
            }
        });
    }

    private boolean isAlreadyInGroup(String username, String groupName) throws IOException, InterruptedException {
        EndPoint endPoint = new EndPoint();
        String url = endPoint.getJoinedGroups() + "?username=" + username;
        ApiConnector connector = new ApiConnector(url);

        String groupsResponse = connector.sendGetRequest();


        ObjectMapper mapper = new ObjectMapper();
        List<Group> groups = mapper.readValue(groupsResponse, new TypeReference<List<Group>>() {});

        for (Group group : groups) {
            if (group.getGroupName().equals(groupName)){
                return true;
            }
        }
        return false;
    }

    public static void createGroup(String groupName, String username) throws IOException, InterruptedException {
        EndPoint endPoint = new EndPoint();
        String url = endPoint.getCreateGroup();
        ApiConnector connector = new ApiConnector(url);

        String params = "group_name=" + groupName;
        String response = connector.sendPostRequest(params);

        System.out.println(response);

        joinGroup(groupName, username);
    }

    public static void joinGroup(String groupName, String username) throws IOException, InterruptedException {

        EndPoint endPoint = new EndPoint();
        String url = endPoint.getJoinGroup();
        ApiConnector connector = new ApiConnector(url);

        String params = "username=" + username + "&group_name=" + groupName;
        String response = connector.sendPostRequest(params);

        System.out.println(response);
    }

    // Новото
    public static void joinGroupByUniqueName(String uniqueNumber, String username) throws IOException, InterruptedException {
        EndPoint endPoint = new EndPoint();
        String url = endPoint.getJoinGroup();
        ApiConnector connector = new ApiConnector(url);

        String params = "username=" + username + "&group_name=" + uniqueNumber;
        String response = connector.sendPostRequest(params);

        System.out.println(response);
    }

    private boolean searchForGroup(String groupName) throws IOException, InterruptedException {

        EndPoint endPoint = new EndPoint();
        String url = endPoint.getSearchGroup() + "?group_name=" + groupName;
        ApiConnector connector = new ApiConnector(url);

        String response = connector.sendGetRequest();

        return response.contains("exists");
    }

    public static boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
