package GitObjects;


import Engine.Magit;
import Exceptions.DataAlreadyExistsException;
import Exceptions.FileErrorException;
import Exceptions.InvalidDataException;
import Utils.MagitStringResultObject;
import Utils.MagitUtils;
import XMLHandler.XMLHandler;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class User {
    private String userName;
    private boolean isOnline;
    private String filesPath;
    private List<Repository> activeRepositories;

    User(String userName) {
        this.userName = userName;
        isOnline = true;
        activeRepositories = new LinkedList<>();
        filesPath =  MagitUtils.joinPaths("c:\\magit-ex3", userName);
        createFileInServer(filesPath);
    }

    public void createFileInServer(String pathToCreate){
        File serverUserFile = new File(getPath());
        if (!serverUserFile.exists()){
            serverUserFile.mkdir();
        }
    }
    public String getPath() {
        return filesPath;
    }

    private void setPath(String path) {
        filesPath = path;
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

    public boolean addRepositoy(Repository repo) {
        if(!getActiveRepositories().contains(repo)){
            activeRepositories.add(repo);
            return true;
        }
        return false;
    }
}
