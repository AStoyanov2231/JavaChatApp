package Connection.Objects;

//import com.example.ChatApp.controllers.Users;
//
//import java.util.List;


public class Group {


    private int groupId;
    private String groupName;
    private String users = null;

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public int getGroupId() {return groupId;}

    public void setGroupId(int groupId) {this.groupId = groupId;}

    public String getGroupName() {return groupName;}

    public void setGroupName(String groupName) { this.groupName = groupName;}

    public String toString() {
        return String.format("Name %s ID %d", this.groupName, this.groupId);
    }
}
