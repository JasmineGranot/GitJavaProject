package GitObjects;

import Exceptions.FileErrorException;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserManager {
    private List<User> allUsers;

    public UserManager(){
        allUsers = new LinkedList<>();
    }

    public void addUser(String userName) {
        User newUser = new User(userName);
        allUsers.add(newUser);
    }

    public User getUserByName(String userName) {
        for(User curr : allUsers) {
            if(curr.getUserName().equals(userName)) {
                return curr;
            }
        }
        return null;
    }

    public List<String> getAllOnlineUsers(){
        Stream<User> users = allUsers.stream();
        return users.filter(User::getIsOnline).map(User::getUserName).collect(Collectors.toList());

    }

    public boolean isUserOnline(String userName) {
        User currUser = getUserByName(userName);
        if(currUser != null) {
            return currUser.getIsOnline();
        }
        return false;
    }

    public boolean isUserExists(String userName) {
        User currUser = getUserByName(userName);
        return currUser != null;
    }

    public List<Repository> showUserRepos(String userName) {
        User currUser = getUserByName(userName);
        if(currUser != null) {
            return currUser.getActiveRepositories();
        }
        return null;
    }

    public Repository getUserRepository(User user, String repoName){
        return user.getUserRepository(repoName);
    }

    public void disconnectUser(String userName) {
        User currUser = getUserByName(userName);
        if(currUser != null) {
            currUser.setOnline(false);
            currUser.logout();
        }
    }

    public void removeFolders() throws FileErrorException {
        for(User currUser : allUsers){
            currUser.deleteFolders();
        }
    }
}
