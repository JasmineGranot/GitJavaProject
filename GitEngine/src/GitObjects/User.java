package GitObjects;


import Exceptions.DataAlreadyExistsException;
import Utils.MagitStringResultObject;
import Utils.MagitUtils;
import XMLHandler.XMLHandler;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
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

    public MagitStringResultObject loadXMLForUser(BufferedReader data) throws JAXBException, DataAlreadyExistsException {
        XMLHandler xml = new XMLHandler(data, getPath());
        if (xml.isRepoValid()) {
            return loadRepositoryFromXML(xml, false);
        }
        return null; //TODO
    }
    public boolean validateXML(BufferedReader data) throws JAXBException {
        XMLHandler xml = new XMLHandler(data, getPath());
        return xml.isRepoValid();
    }

    private MagitStringResultObject loadRepositoryFromXML(XMLHandler handler, boolean toDeleteExistingRepo)
            throws DataAlreadyExistsException {
        String msg;
        MagitStringResultObject result = new MagitStringResultObject();
        Repository repo = new Repository();
        try{
            repo.loadRepoFromXML(this, handler.getMagitRepositoryPath(), toDeleteExistingRepo);
            activeRepositories.add(repo);
            msg = "Repository loaded successfully!";
            result.setData(msg);
            result.setIsHasError(false);
        }
        catch (DataAlreadyExistsException e){
            throw e;
        }
        catch (IOException e){
            msg = "Unhandled IOException!";
            result.setErrorMSG(msg);
            result.setIsHasError(true);
        }
        catch (Exception e){
            result.setErrorMSG(e.getMessage());
            result.setIsHasError(true);
        }
        return result;
    }
}
