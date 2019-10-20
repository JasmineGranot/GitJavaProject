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

    private void createFileInServer(String pathToCreate){
        File serverUserFile = new File(getPath());
        if (!serverUserFile.exists()){
            serverUserFile.mkdir();
        }
    }
    private String getPath() {
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

    public MagitStringResultObject loadXMLForUser(String data) throws JAXBException, DataAlreadyExistsException, FileErrorException {
        XMLHandler xml = new XMLHandler(data, getPath());
        if (xml.isRepoValid()) {
            String newRepoPath = MagitUtils.joinPaths(getPath(), xml.getMagitRepository().getName());
            createFileInServer(newRepoPath);
            return loadRepositoryFromXML(xml,newRepoPath, false);
        }
        return null; //TODO
    }
    public boolean validateXML(String data) throws JAXBException {
        XMLHandler xml = new XMLHandler(data, getPath());
        return xml.isRepoValid();
    }

    private MagitStringResultObject loadRepositoryFromXML(XMLHandler handler,String newRepoPath,
                                                          boolean toDeleteExistingRepo)
            throws DataAlreadyExistsException, JAXBException, FileErrorException {
        String msg;
        MagitStringResultObject result = new MagitStringResultObject();
        Repository repo = new Repository(this, handler.getMagitRepository().getName());
        try{
            repo.loadRepoFromXML(handler.getMagitRepository(), newRepoPath, toDeleteExistingRepo);
            if(!activeRepositories.contains(repo)){
                activeRepositories.add(repo);
            }
            else{
                msg = "Repository already Exist Error!";
                throw new InvalidDataException(msg);
            }
            msg = "Repository loaded successfully!";
            result.setData(msg);
            result.setIsHasError(false);
        }
        catch (DataAlreadyExistsException e){
            throw e;
        } catch (Exception e){
            result.setErrorMSG(e.getMessage());
            result.setIsHasError(true);
        }
        return result;
    }
}
