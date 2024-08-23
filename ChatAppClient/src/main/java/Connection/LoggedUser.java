package Connection;

import java.util.ArrayList;
import java.util.List;

public class LoggedUser {
    private String username;
    private static int currentGroupId;
    private String currentGroupName;
    private List<String> joinedGroups = new ArrayList<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static int getCurrentGroupId() {
        return currentGroupId;
    }

    public void setCurrentGroupId(int currentGroupId) {
        this.currentGroupId = currentGroupId;
    }

    public String getCurrentGroupName() {
        return currentGroupName;
    }

    public void setCurrentGroupName(String currentGroupName) {
        this.currentGroupName = currentGroupName;
    }

    public List<String> getJoinedGroups() {
        return joinedGroups;
    }

    public void setJoinedGroups(List<String> joinedGroups) {
        this.joinedGroups = joinedGroups;
    }
}