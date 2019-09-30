package GitObjects;

import java.util.LinkedList;
import java.util.List;

public class UserManager {
    private List<User> allUsers;

    UserManager(){
        allUsers = new LinkedList<>();
    }

    void addUser(String userName) {
        User newUser = new User(userName);
        allUsers.add(newUser);
    }

    private User getUserByName(String userName) {
        for(User curr : allUsers) {
            if(curr.getUserName().equals(userName)) {
                return curr;
            }
        }
        return null;
    }

    boolean isUserOnline(String userName) {
        User currUser = getUserByName(userName);
        if(currUser != null) {
            return currUser.getIsOnline();
        }
        return false;
    }

    boolean isUserExists(String userName) {
        User currUser = getUserByName(userName);
        return currUser != null;
    }

    List<Repository> showUserRepos(String userName) {
        User currUser = getUserByName(userName);
        if(currUser != null) {
            return currUser.getActiveRepositories();
        }
        return null;
    }
}
