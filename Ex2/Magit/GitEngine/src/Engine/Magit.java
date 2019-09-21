package Engine;

import GitObjects.Branch;
import GitObjects.Commit;
import Utils.*;
import Exceptions.*;
import com.sun.xml.internal.ws.util.StringUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.io.IOError;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Magit {

    private final String NON_EXISTING_REPO_MSG = "No repository is configured at the moment." ;
    private Repository repo = new Repository();
    private Map<String, String> repos = new HashMap<>();
    private StringProperty path = repo.getRootPath();
    private StringProperty userName = new SimpleStringProperty("Administrator");

//  ======================== General Functions =============================

    public void setUserName(String newName) {
        this.userName.setValue(newName);
    }

    public StringProperty getUserName() {return this.userName;}

    public WorkingCopyChanges showStatus() {
        String msg;
        WorkingCopyChanges changes = new WorkingCopyChanges();
        if (!isRepositoryConfigured()){
            changes.setHasErrors(true);
            changes.setErrorMsg(NON_EXISTING_REPO_MSG);
            return changes;
        }
        try {
            changes = repo.printWCStatus();
            msg = "Success";
            changes.setMsg(msg);
            changes.setHasErrors(false);
        }
        catch (IOException e){
            msg = "The was an unhandled IOException! Exception message: " + e.getMessage();
            changes.setHasErrors(true);
            changes.setErrorMsg(msg);
        }
        catch (InvalidDataException e){
            changes.setHasErrors(true);
            changes.setErrorMsg(e.getMessage());
        }
        return changes;
    }

//  ======================== Repository Functions ==========================

    public StringProperty getPath(){
    return path;
}

    public StringProperty getRepoName() {return this.repo.getRepoName();}

    public String getRepoNameByPath(String path) {
        return repos.get(path);
    }

    public MagitStringResultObject createNewRepo(String repoPath, String repoName) {
        String msg;
        MagitStringResultObject result = new MagitStringResultObject();
        try{
            repo.createNewRepository(repoPath, repoName, true);
            msg = "Repository created successfully";
            result.setData(msg);
            result.setIsHasError(false);
            repos.put(repoPath, repoName);
        }
        catch (Exception e){
            msg = e.getMessage();
            result.setErrorMSG(msg);
            result.setIsHasError(true);
        }
        return result;
    }

    public MagitStringResultObject changeRepository(String repoPath) {
        String msg;
        MagitStringResultObject result = new MagitStringResultObject();
        try{
            String repoName = repos.get(repoPath);
            if (repoName == null){
                repoName = Paths.get(repoPath).getFileName().toString();
            }
            repo.changeRepo(repoPath, repoName);
            msg = "Repository changed successfully!";
            result.setData(msg);
            result.setIsHasError(false);
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

    public MagitStringResultObject loadRepositoryFromXML(String xmlFilePath, boolean toDeleteExistingRepo)
            throws DataAlreadyExistsException{
        String msg;
        MagitStringResultObject result = new MagitStringResultObject();
        try{
            repo.loadRepoFromXML(xmlFilePath, toDeleteExistingRepo);
            msg = "Repository loaded successfully!";
            result.setData(msg);
            result.setIsHasError(false);
            repos.put(repo.getRootPath().getValue(), repo.getRepoName().getValue());
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

    private boolean isRepositoryConfigured(){
        return repo.getRootPath() != null;
    }

    public boolean isValidRepository(String path) {
        return repo.isValidRepo(path);
    }

//  ======================== Branch Functions ==============================

    public String findBranchBySha1(String branchSha1) {
        return repo.findBranchBySha1(branchSha1);
    }

    public ObservableList<String> getCurrentBranchesNames(){
        return repo.getCurrentBranchesNames();
    }

    public MagitStringResultObject addNewBranch(String branchName, String sha1,
                                                boolean isRemoteTracking, boolean toIgnoreRemoteBranchsSha1)
            throws InvalidDataException, DataAlreadyExistsException {
        String msg;
        MagitStringResultObject resultObject = new MagitStringResultObject();
        if (!isRepositoryConfigured()){
            resultObject.setIsHasError(true);
            resultObject.setErrorMSG(NON_EXISTING_REPO_MSG);
            return resultObject;
        }
        else if (branchName.contains(" ")) {
            msg = "Branch name is invalid, please try again without any spaces.";
            throw new InvalidDataException(msg);
        }

        else if(repo.isRemote(sha1) && !toIgnoreRemoteBranchsSha1) {
            msg = "The sha1 is currently pointed by a remote branch.\n" +
                    "Would you like to add the branch as a remote tracking branch?\n" +
                    "OK for yes\n" +
                    "Cancel for abort";
            throw new DataAlreadyExistsException(msg);
        }

        else {
            try {
                repo.addBranch(branchName, sha1, isRemoteTracking);
                resultObject.setIsHasError(false);
                msg = "Branch was added successfully!";
                resultObject.setData(msg);
            }
            catch (DataAlreadyExistsException e) {
                resultObject.setIsHasError(true);
                msg = "Had an issue while trying to add the new branch!\n" +
                        "Error message: " + e.getMessage();
                resultObject.setErrorMSG(msg);
            }
            catch (IOException e) {
                resultObject.setIsHasError(true);
                msg = "Had an unhandled IOException!\n" +  "Error message: " + e.getMessage();
                resultObject.setErrorMSG(msg);
            }
        }
        return resultObject;
    }

    public MagitStringResultObject deleteBranch(String branchName) throws InvalidDataException {
        MagitStringResultObject resultObject = new MagitStringResultObject();
        String msg;
        if (!isRepositoryConfigured()){
            resultObject.setIsHasError(true);
            resultObject.setErrorMSG(NON_EXISTING_REPO_MSG);
            return resultObject;
        }
        if (branchName.contains("(Head)")) {
            msg = "Head Branch cannot be deleted";
            throw new InvalidDataException(msg);
        }
        else {
            try {
                repo.removeBranch(branchName);
                msg = "Branch deleted successfully!";
                resultObject.setIsHasError(false);
                resultObject.setData(msg);
            }
            catch (Exception e){
                msg = "Could not delete the branch!\nError message: " + e.getMessage();
                resultObject.setIsHasError(true);
                resultObject.setErrorMSG(msg);
            }
        }
        return resultObject;
    }

    public MagitStringResultObject checkoutBranch(String branchName, boolean ignoreChanges)
            throws DirectoryNotEmptyException{
        String msg;
        MagitStringResultObject resultObject = new MagitStringResultObject();
        if (!isRepositoryConfigured()){
            resultObject.setIsHasError(true);
            resultObject.setErrorMSG(NON_EXISTING_REPO_MSG);
            return resultObject;
        }
        try {
            if(branchName.contains("(Head)")){
                String[] branchNameSplit = branchName.split(" ");
                branchName = branchNameSplit[0];
            }
            repo.checkoutBranch(branchName, ignoreChanges);
            resultObject.setIsHasError(false);
            msg = "Checkout was successful!";
            resultObject.setData(msg);
        }
        catch (DirectoryNotEmptyException e){
            throw e;
        }
        catch (InvalidDataException e){
            msg = "Had an issue while trying to checkout branch!\nError message: " + e.getMessage();
            resultObject.setIsHasError(true);
            resultObject.setErrorMSG(msg);
        }
        catch (Exception e){
            resultObject.setIsHasError(true);
            resultObject.setErrorMSG(e.getMessage());
        }
        return resultObject;
    }

    public MagitStringResultObject resetBranch(String commitSha1, boolean ignore)
            throws DirectoryNotEmptyException{
        MagitStringResultObject resultObject = new MagitStringResultObject();
        String msg;
        if (!isRepositoryConfigured()){
            resultObject.setIsHasError(true);
            resultObject.setErrorMSG(NON_EXISTING_REPO_MSG);
            return resultObject;
        }
        try {
            repo.resetCommitInBranch(commitSha1, ignore);
            resultObject.setIsHasError(false);
            msg = "Reset Branch successfully!";
            resultObject.setData(msg);
        }
        catch (DirectoryNotEmptyException e){
            throw e;
        }
        catch (Exception e){
            resultObject.setIsHasError(true);
            resultObject.setErrorMSG(e.getMessage());
        }
        return resultObject;
    }

    public StringProperty getCurrentBranch(){
        return repo.getCurrentBranch().getName();
    }

    public Branch getBranchByName(String name) {
        return repo.getBranchByName(name);
    }

    public ResultList<Branch.BrancheData> showAllBranches() {
        ResultList<Branch.BrancheData> result = new ResultList<>();
        if (!isRepositoryConfigured()){
            result.setHasError(true);
            result.setErrorMsg(NON_EXISTING_REPO_MSG);
            return result;
        }
        try{
            result.setRes(repo.showAllBranchesData());
            result.setHasError(false);
        }
        catch(Exception e){
            result.setHasError(true);
            result.setErrorMsg("Got Generic Exception!!!\n Error message: " + e.getMessage());
        }
        return result;
    }

    public MagitStringResultObject showHistoryDataForActiveBranch() {
        MagitStringResultObject resultObject = new MagitStringResultObject();
        if (!isRepositoryConfigured()){
            resultObject.setIsHasError(true);
            resultObject.setErrorMSG(NON_EXISTING_REPO_MSG);
            return resultObject;
        }
        try{
            resultObject = repo.getHistoryBranchData();
            resultObject.setIsHasError(false);
        }
        catch (Exception e) {
            resultObject.setIsHasError(true);
            resultObject.setErrorMSG(e.getMessage());
        }
        return resultObject;
    }

//  ======================== Commit Functions ==============================

    public MagitStringResultObject showFullCommitData(){
        List<String> msg;
        MagitStringResultObject result = new MagitStringResultObject();
        if (!isRepositoryConfigured()){
            result.setIsHasError(true);
            result.setErrorMSG(NON_EXISTING_REPO_MSG);
            return result;
        }
        try{
            msg = repo.getCurrentCommitFullFilesData();
            result.setDataList(msg);
            result.setIsHasError(false);
        }
        catch (Exception e){
            String errorMsg = "There was an unhandled exception!";
            result.setErrorMSG(errorMsg + "\nException msg: " + e.getMessage());
            result.setIsHasError(true);
        }
        return result;
    }

    public MagitStringResultObject createNewCommit(String commitMsg, String secondCommitSha1) {
        MagitStringResultObject result = new MagitStringResultObject();
        boolean success;
        String msg;
        if (!isRepositoryConfigured()){
            result.setIsHasError(true);
            result.setErrorMSG(NON_EXISTING_REPO_MSG);
            return result;
        }
        try {
            success = repo.createNewCommit(userName.getValue(), commitMsg, secondCommitSha1);
            if(success){
                msg = "The commit was created successfully!";
                result.setData(msg);
                result.setIsHasError(false);
                result.setChanged(true);
            }
            else{
                msg = "Nothing has changed in the repository!";
                result.setData(msg);
                result.setIsHasError(false);
                result.setChanged(false);
            }
        }
        catch (InvalidDataException e){
            result.setIsHasError(true);
            result.setErrorMSG(e.getMessage());
        }
        catch (FileErrorException e)
        {
            msg = "Something went wrong while trying to update the files in the system!" +
                    "Error message: " + e.getMessage();
            result.setIsHasError(true);
            result.setErrorMSG(msg);
        }
        catch (IOException e)
        {
            msg = "There was an unhandled IOException!\n" +
                    "Error message: " + e.getMessage();
            result.setIsHasError(true);
            result.setErrorMSG(msg);
        }
        return result;
    }

    public List<Commit.CommitData> getCurrentCommits() {
        return repo.currentCommits();
    }

    public Commit getCurrentCommit() {
        return repo.getCurrentCommit();
    }

//  ======================== Collaborations Functions ======================

    public String merge(Branch branchToMerge, List<MergeResult> res)
            throws InvalidDataException, IOException, FileErrorException, DataAlreadyExistsException {
        return repo.merge(branchToMerge, res);
    }

    public MagitStringResultObject cloneRemoteToLocal(String remotePath, String localPath, String repoName) {
        MagitStringResultObject res = new MagitStringResultObject();
        try {
            repo.clone(remotePath, localPath, repoName);
            repos.put(StringUtils.capitalize(localPath), repoName);
            res.setData("Repository cloned successfully!");
            res.setIsHasError(false);
        } catch (Exception e) {
            String errorMessage = "Something went wrong while trying to clone the repository!\n" +
                    "Error message: " + e.getMessage();
            res.setIsHasError(true);
            res.setData(errorMessage);
        }
        return res;
    }

    public MagitStringResultObject fetch() {
        MagitStringResultObject res = new MagitStringResultObject();
        try {
            repo.fetch(repos.get(repo.getRemoteRepoPath(repo.getRootPath().getValue())));
            res.setIsHasError(false);
            res.setData("Fetched successfully!");
        } catch (Exception e) {
            String errorMsg = "Something went wrong while trying to fetch!\n" +
                    "Error message: " + e.getMessage();
            res.setIsHasError(true);
            res.setErrorMSG(errorMsg);
        }
        return res;
    }

    public MagitStringResultObject pull() {
        MagitStringResultObject res = new MagitStringResultObject();
        try {
            repo.pull(repos.get(repo.getRemoteRepoPath(repo.getRootPath().getValue())));
            res.setIsHasError(false);
            res.setData("Pulled from remote repository successfully!");
        } catch (Exception e) {
            String errorMsg = "Something went wrong while trying to pull from remote repository!\n" +
                    "Error message: " + e.getMessage();
            res.setIsHasError(true);
            res.setErrorMSG(errorMsg);
        }
        return res;
    }

    public MagitStringResultObject push() {
        MagitStringResultObject res = new MagitStringResultObject();
        try {
            repo.push(repos.get(repo.getRemoteRepoPath(repo.getRootPath().getValue())));
            res.setIsHasError(false);
            res.setData("Pulled successfully!");
        } catch (Exception e) {
            res.setIsHasError(true);
            String errorMessage = "Something went wrong while trying to pull!\n" +
                    "Error message:" + e.getMessage();
            res.setErrorMSG(errorMessage);
        }
        return res;
    }


}