package GitObjects;


import Exceptions.FileErrorException;
import Utils.MagitUtils;
import com.sun.xml.internal.ws.client.sei.ResponseBuilder;

import javax.management.Notification;
import java.io.File;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class User {
    private String userName;
    private boolean isOnline;
    private String filesPath;
    private List<Repository> activeRepositories;
    private List<NotificationObject> userNotifications;
    private Date lastSignOut;

    User(String userName) {
        this.userName = userName;
        isOnline = true;
        lastSignOut = MagitUtils.getTodayAsDate();
        userNotifications = new LinkedList<>();
        activeRepositories = new LinkedList<>();
        filesPath =  MagitUtils.joinPaths("c:\\magit-ex3", userName);
        createFileInServer(filesPath);
    }


    public void createFileInServer(String pathToCreate){
        File serverUserFile = new File(getPath());
        File mainFolder = new File("c:\\magit-ex3");

        if(!mainFolder.exists()){
            mainFolder.mkdir();
        }

        if (!serverUserFile.exists()){
        serverUserFile.mkdir();
        }
    }

    public String getPath() {
        return filesPath;
    }
    public Date getLastSignOut() {
        return lastSignOut;
    }

    private void setPath(String path) {
        filesPath = path;
    }

    public List<Repository> getActiveRepositories() {
        return activeRepositories;
    }

    public List<RepositoryWebData> getActiveRepositoriesWebData() {
        List<RepositoryWebData> repos= new LinkedList<>();
        for (Repository repo : getActiveRepositories()){
            repos.add(repo.getRepoWebData());
        }
        return repos;
    }

    public List<NotificationObject> getUserNotifications() {
        return userNotifications;
    }

    public List<NotificationObject> getUserNotificationsDelta() {
        Stream <NotificationObject> msg =  userNotifications.stream();
        return msg.filter(
                x-> this.getLastSignOut().before(MagitUtils.getStringAsDate(x.getDate()))).collect(Collectors.toList());
    }
    public List<PullRequestObject> getUserPullRequests() {
        List<PullRequestObject> prs = new LinkedList<>();
        for (Repository repo : activeRepositories)
            prs.addAll(repo.getRepoPullRequsetList());
        return prs;
    }

    public List<PullRequestObject> getUserPullRequestsForRepo(String repoName) {
        return new LinkedList<>(getUserRepository(repoName).getRepoPullRequsetList());
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

    public void setOnline(boolean online) {
        isOnline = online;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean addRepositoy(Repository repo) {
        if(!getActiveRepositories().contains(repo)){
            activeRepositories.add(repo);
            return true;
        }
        return false;
    }

    public void addNotification(NotificationObject notification) {
        userNotifications.add(notification);
    }

    public Repository getUserRepository(String repoName) {
        for (Repository repo : getActiveRepositories()){
            if (repo.getRepoName().equals(repoName)){
                return repo;
            }
        }
        return null;
    }

    public boolean isRepoExist(String repoName){
        return getUserRepository(repoName) != null;
    }

    void deleteFolders() throws FileErrorException {
        for (Repository repo : getActiveRepositories()){
            repo.deleteWC(getPath(), true);
        }
        File userFolder = new File(getPath());
        userFolder.delete();
    }

    public void logout() {
        lastSignOut = MagitUtils.getTodayAsDate();
        setOnline(false);
    }

    public void deleteNotification(String msgToFind) {
        for(NotificationObject curr : userNotifications) {
            if(curr.getMsg().equals(msgToFind)) {
                userNotifications.remove(curr);
            }
        }
    }
}
