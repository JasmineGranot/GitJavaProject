package GitObjects;


import Utils.MagitUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public List<PullRequestObject> getActivePullRequest() {
        List<PullRequestObject> allOpenRequest = new LinkedList<>();
        for (Repository repo : activeRepositories){
            Stream<PullRequestObject> prObjects = repo.getAllPullRequestInRepo().stream();
            allOpenRequest.addAll(prObjects.filter(
                    x->x.getStatus().equals(MagitUtils.OPEN_PULL_REQUEST)).collect(Collectors.toList()));

        }
        return allOpenRequest;
    }



}
