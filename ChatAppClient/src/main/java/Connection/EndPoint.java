package Connection;

public class EndPoint {

    private String getAll = "http://localhost:8080/all";
    private String createUser = "http://localhost:8080/createUser";
    private String changePassword = "http://localhost:8080/changePassword";
    private String deleteUser = "http://localhost:8080/deleteUser";
    private String login = "http://localhost:8080/login";
    private String sendMessage = "http://localhost:8080/sendMessage";
    private String getMessages = "http://localhost:8080/getMessages";
    private String createGroup = "http://localhost:8080/createGroup";
    private String searchGroup = "http://localhost:8080/searchGroup";
    private String joinGroup = "http://localhost:8080/addUserToGroup";
    private String joinedGroups = "http://localhost:8080/getJoinedGroups";
    private String FriendList = "http://localhost:8080/getFriends";
    private String addFriend = "http://localhost:8080/addFriend";
    private String sendFriendRequest = "http://localhost:8080/sendFriendRequest";
    private String friendRequests = "http://localhost:8080/pendingRequests";
    private String respondFriendRequests = "http://localhost:8080/respondFriendRequests";

    public String getSendFriendRequest() { return sendFriendRequest; }

    public String getFriendRequests() { return friendRequests; }

    public String getRespondFriendRequests() { return respondFriendRequests; }

    public String getAddFriend() { return addFriend; }

    public String getFriendList() { return FriendList; }

    public String getJoinedGroups() { return joinedGroups; }

    public void setJoinedGroups(String joinedGroups) {
        this.joinedGroups = joinedGroups;
    }

    public String getJoinGroup() {
        return joinGroup;
    }

    public void setJoinGroup(String joinGroup) {this.joinGroup = joinGroup;}

    public String getCreateGroup() {
        return createGroup;
    }

    public void setCreateGroup(String createGroup) {
        this.createGroup = createGroup;
    }

    public String getSearchGroup() {
        return searchGroup;
    }

    public void setSearchGroup(String searchGroup) {
        this.searchGroup = searchGroup;
    }

    public String getLogin() {
        return login;
    }

    public String getCreateUser() {
        return createUser;
    }

    public String getChangePassword() {return changePassword;}

    public String getDeleteUser() {
        return deleteUser;
    }
    public String getGetAll() {return getAll;}

    public String getSendMessage() {return sendMessage;}

    public String getGetMessages() {return getMessages;}

//    private String GET = "GET";
//    private String POST = "POST";
//    private String DELETE = "DELETE";
//
//    public String getGet() {
//        return GET;
//    }
//
//    public String getPost() {
//        return POST;
//    }
//
//    public String getDelete() {
//        return DELETE;
//    }
}
