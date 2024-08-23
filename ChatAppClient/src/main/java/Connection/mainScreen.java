package Connection;

import Connection.Objects.Group;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class mainScreen extends JFrame {

    private JPanel mainScreen;
    private JButton btnSendMessage;
    private JTextField MessageBar;
    private JButton settingsButton;
    private JButton groupsButton;
    private JTextArea textArea;
    private JLabel groupName;
    private JList GroupList;
    private JList FriendList;
    private JButton btnAddFriend;
    private JButton btnRequests;
    private JTextField UserSearchBar;
    private JLabel FindUser;

    private Client client;
    private LoggedUser loggedUser;
    private static final int maxMessages = 10; // Adjust this as needed

    public mainScreen(LoggedUser loggedUser) throws IOException, InterruptedException {


        this.loggedUser = loggedUser;

        setContentPane(mainScreen);
        setSize(800, 620);
        setTitle(loggedUser.getUsername());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        mainScreen.getRootPane().setDefaultButton(btnSendMessage);

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        GroupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        List<Group> groups = updateGroupList(loggedUser.getUsername());
        List<Group> friendDMs = updateFriendList(loggedUser.getUsername());

        // listener for groups
        GroupList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {

                if (!evt.getValueIsAdjusting() && GroupList.getSelectedValue() != null){

                    int currentGroup = 0;
                    try {
                        System.out.println(GroupList.getSelectedValue());
                        currentGroup = changeGroup(evt, groups);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    loggedUser.setCurrentGroupId(currentGroup);
                    System.out.println("debug group ID:" + currentGroup);

                    System.out.println("got me " + GroupList.getSelectedIndex());
                    FriendList.clearSelection();

                }
            }
        });


        FriendList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {

                if (!evt.getValueIsAdjusting() && FriendList.getSelectedValue() != null) {
                    int currentGroup = 0;
                    try {
                        System.out.println(FriendList.getSelectedValue());
                        currentGroup = changeFriend(evt, friendDMs, loggedUser);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    loggedUser.setCurrentGroupId(currentGroup);
                    System.out.println("debug group ID:" + currentGroup);
                    GroupList.clearSelection();


                }
            }
        });

        groupName.setText("- no group selected -");
        textArea.setText("Select or join a group");

        try {
            client = new Client(loggedUser.getUsername(), this); // Initiate the WebSocket connection for the logged-in user
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        btnSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = MessageBar.getText();

                if (!message.isBlank())
                {
                    sendMsg(loggedUser.getUsername(), message, loggedUser.getCurrentGroupId());
                    System.out.println(message);
                    MessageBar.setText("");
                }
            }
        });

        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ScreenOpening.openRelativeScreen(mainScreen.this, new SettingsScreen(loggedUser));
                dispose();
            }
        });

        groupsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ScreenOpening.OpenGroupManagerScreen(mainScreen.this, new groupManager(loggedUser));
                dispose();
            }
        });

        btnAddFriend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String friendUsername = UserSearchBar.getText().trim();

                if (!friendUsername.isEmpty()) {
                    try {
                        addFriend(loggedUser.getUsername(), friendUsername, friendDMs);
                        UserSearchBar.setText("");
                    } catch (IOException | InterruptedException ex) {
                        JOptionPane.showMessageDialog(mainScreen.this, "Error: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(mainScreen.this, "Please enter a username to add.");
                }
            }
        });
        btnRequests.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                GroupList.clearSelection();
//                FriendList.clearSelection();
                System.out.println(GroupList.getFirstVisibleIndex());
                System.out.println(FriendList.getFirstVisibleIndex());
            }
        });
    }

    private int changeGroup(ListSelectionEvent evt, List<Group> groups) throws IOException, InterruptedException {

        String selectedGroup = (String) GroupList.getSelectedValue();
        System.out.println("change group method: selected group " + selectedGroup);
        int groupId = 0;

        if (client != null) {
            client.unsubscribeAll();
        }

        for (Group group : groups) {
            if (selectedGroup.equals(group.getGroupName())){
                groupId = group.getGroupId();
                groupName.setText(group.getGroupName());
                break;
            }
        }
        textArea.setText("Joined group " + selectedGroup + "\n");
        getMsg(groupId);

        if (client != null) {
            client.subscribeToTopic("/topic/" + groupId);
        }

        System.out.println("Debug: this is the changeGroup method");
        return groupId;
    }

    private int changeFriend(ListSelectionEvent evt, List<Group> groups, LoggedUser loggedUser) throws IOException, InterruptedException {

        String selectedGroup1 = loggedUser.getUsername() + "/" + (String) FriendList.getSelectedValue();
        String selectedGroup2 = (String) FriendList.getSelectedValue() + "/" + loggedUser.getUsername();
        int groupId = 0;

        if (client != null) {
            client.unsubscribeAll();
        }

        for (Group group : groups) {
            if (selectedGroup1.equals(group.getGroupName()) || selectedGroup2.equals(group.getGroupName())){
                groupId = group.getGroupId();
                groupName.setText(group.getGroupName());
                break;
            }
        }
        textArea.setText("Joined friend " + FriendList.getSelectedValue() + "\n");
        getMsg(groupId);

        if (client != null) {
            client.subscribeToTopic("/topic/" + groupId);
        }

        System.out.println("Debug: this is the changeFriend method");
        return groupId;
    }

    private String getJoinedGroups(String username) throws IOException, InterruptedException {
        EndPoint endPoint = new EndPoint();
        String url = endPoint.getJoinedGroups() + "?username=" + username;
        ApiConnector connector = new ApiConnector(url);

        String response = connector.sendGetRequest();

        return response;
    }

    private void addFriend(String username, String friendUsername, List<Group> friendDMs) throws IOException, InterruptedException{
        EndPoint endPoint = new EndPoint();
        String url = endPoint.getAddFriend();
        ApiConnector connector = new ApiConnector(url);

        String params = "username=" + username + "&friendUsername=" + friendUsername;
        String response = connector.sendPostRequest(params);

        String groupUniqueName = friendUsername + "/" + username;
        groupManager.createGroup(groupUniqueName, username);
        groupManager.joinGroupByUniqueName(groupUniqueName, friendUsername);

        if (response.equals("Friend added successfully")){
            JOptionPane.showMessageDialog(this, "Friend added successfully");
            // TODO: re-add
            updateFriendList(username);
        }else {
            JOptionPane.showMessageDialog(this, "Failed to add friend");
        }
    }

    private String getFriends(String username) throws IOException, InterruptedException{
        EndPoint endPoint = new EndPoint();
        String url = endPoint.getFriendList() + "?username=" + username;
        ApiConnector connector = new ApiConnector(url);

        return connector.sendGetRequest();
    }

    private void getMsg(int groupId) throws IOException, InterruptedException {
        EndPoint endPoint = new EndPoint();
        String url = endPoint.getGetMessages() + "?groupId=" + groupId;
        ApiConnector connector = new ApiConnector(url);

        String response = connector.sendGetRequest();

        ObjectMapper mapper = new ObjectMapper();
        List<String> strings = mapper.readValue(response, List.class);

        for (String message : strings){
            textArea.append(message + "\n");
        }

        limitChatHistory();
    }

    private void sendMsg(String username, String message, int currentGroupId) {
        if (client != null) {
            // Display the message in the text area
            textArea.append(username + ": " + message + "\n");

            ClientSessionHandler.ChatMessage chatMessage = new ClientSessionHandler.ChatMessage(username, message, currentGroupId);

            client.sendPublicMessage("/app/chat.sendMessage/" + currentGroupId, chatMessage);

            limitChatHistory();
        }
    }

    private List<Group> updateFriendList(String username) throws IOException, InterruptedException{

        // Getting joined groups
        String groupsResponse = (getJoinedGroups(loggedUser.getUsername()));
        ObjectMapper mapper = new ObjectMapper();
        List<Group> friendDMs = mapper.readValue(groupsResponse, new TypeReference<List<Group>>() {});
        friendDMs.removeIf(group -> !group.getGroupName().contains("/"));

        FriendList.setModel(new AbstractListModel() {
            @Override
            public int getSize() {
                return friendDMs.size();
            }

            @Override
            public Object getElementAt(int i) {
                return friendDMs.stream()
                        .filter(group -> group.getGroupName().contains(loggedUser.getUsername()))
                        .map(group -> {
                            String[] names = group.getGroupName().split("/");
                            return names[0].equals(loggedUser.getUsername()) ? names[1] : names[0];
                        })
                        .toList().get(i);
            }
        });
        return friendDMs;
    }

    private List<Group> updateGroupList(String username) throws IOException, InterruptedException {

        // Getting joined groups
        String groupsResponse = (getJoinedGroups(loggedUser.getUsername()));
        ObjectMapper mapper = new ObjectMapper();
        List<Group> groups = mapper.readValue(groupsResponse, new TypeReference<List<Group>>() {});
        groups.removeIf(group -> group.getGroupName().contains("/"));

        GroupList.setModel(new AbstractListModel() {

            @Override
            public int getSize() {
                return groups.size();
            }
            @Override
            public Object getElementAt(int i) {
                return groups.get(i).getGroupName();
            }
        });
        return groups;
    }

    public void displayMessage(Object message, int groupId){
        ClientSessionHandler.ChatMessage msg = (ClientSessionHandler.ChatMessage) message;

        if (msg.getCurrentGroupId() == groupId) {
            textArea.append(msg.getName() + ": " + msg.getContent() + "\n");
            limitChatHistory();
        }
    }

    private void limitChatHistory(){
        String[] lines = textArea.getText().split("\n");

        if (lines.length > maxMessages){
            int linesToRemove = lines.length - maxMessages;

            StringBuilder newContent = new StringBuilder();

            for (int i = linesToRemove; i < lines.length; i++){
                newContent.append(lines[i]).append("\n");
            }

            textArea.setText(newContent.toString());
        }
    }

    public static int getActiveGroupId(){
        return LoggedUser.getCurrentGroupId();
    }

    // debug method to start the screen
//    public static void main(String[] args) throws IOException, InterruptedException {
//        LoggedUser loggedUser = new LoggedUser();
//        loggedUser.setUsername("m");
//        new mainScreen(loggedUser);
//    }
}