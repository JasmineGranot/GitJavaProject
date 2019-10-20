package GitObjects;


import java.util.LinkedList;
import java.util.List;

public class User {
    private String userName;
    private boolean isOnline;
    private List<Repository> activeRepositories;

    User (String userName) {
        this.userName = userName;
        isOnline = true;
        activeRepositories = new LinkedList<>();
    }

    public List<Repository> getActiveRepositories() {
        return activeRepositories;
    }

    public String getUserName() {
        return userName;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    void setActiveRepositories(List<Repository> activeRepositories) {
        this.activeRepositories = activeRepositories;
    }

    void setOnline(boolean online) {
        isOnline = online;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

}
