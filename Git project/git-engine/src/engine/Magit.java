package engine;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;

public class Magit {

    private String userName = "Administrator";
    private Repository repo = new Repository();

    public void setUserName(String newName) {
        this.userName = newName;
    }

    public MagitStringResultObject createNewRepo(String repoPath) {
        String msg;
        MagitStringResultObject result = new MagitStringResultObject();
        try{
            repo.createNewRepository(repoPath);
            msg = "Repository created successfully";
            result.setData(msg);
            result.setIsHasError(false);
        }
        catch (Exception e){
            msg = e.getMessage();
            result.setErrorMSG(msg);
            result.setIsHasError(true);
        }
        return result;
    }

    public MagitStringResultObject changeRepository(String newRepoName) {
        String msg;
        MagitStringResultObject result = new MagitStringResultObject();
        try{
            repo.changeRepo(newRepoName);
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

    public MagitStringResultObject loadRepositoryFromXML(String xmlFilePath) {
        String msg;
        MagitStringResultObject result = new MagitStringResultObject();
        try{
            repo.loadRepoFromXML(xmlFilePath);
            msg = "Repository loaded successfully!";
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

    public MagitStringResultObject showFullCommitData(){
        String msg;
        MagitStringResultObject result = new MagitStringResultObject();
        try{
            msg = repo.getCurrentCommitFullFilesData();
            result.setData(msg);
            result.setIsHasError(false);
        }
        catch (Exception e){
            msg = "There was an unhandled exception!";
            result.setErrorMSG(msg + "\nException msg: " + e.getMessage());
            result.setIsHasError(true);
        }
        return result;
    }

    public WorkingCopyChanges showStatus() {
        String msg;
        String rootPath;
        WorkingCopyChanges changes = new WorkingCopyChanges();

        try {
            rootPath = repo.getRootPath();
        }
        catch(NullPointerException e){
            msg = "There was a problem reading the current repository path!";
            changes.setErrorMsg(msg);
            changes.setHasErrors(true);
            return changes;
        }

        try {
            changes = repo.printWCStatus();
            msg = String.format("The current repository is: %s\n" +
                    "The current user is: %s\n" + "WC status:", rootPath, userName);
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

    public MagitStringResultObject createNewCommit(String commitMsg) {
        MagitStringResultObject result = new MagitStringResultObject();
        boolean success;
        String msg;
        try {
            success = repo.createNewCommit(userName, commitMsg);
            if(success){
                msg = "The commit was created successfully!";
                result.setData(msg);
                result.setIsHasError(false);
            }
            else{
                msg = "Nothing has changed in the repository!";
                result.setData(msg);
                result.setIsHasError(false);
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

    public MagitStringResultObject showAllBranches() {
        MagitStringResultObject result = new MagitStringResultObject();
        try{
            result.setData(repo.showAllBranchesData());
            result.setIsHasError(false);
        }
        catch(Exception e){
            result.setIsHasError(true);
            result.setErrorMSG("Got EXCEPTION!!! " + e.getMessage());
        }
        return result;
    }

    public MagitStringResultObject addNewBranch(String branchName) throws InvalidDataException {
        String msg;
        MagitStringResultObject resultObject = new MagitStringResultObject();
        if (branchName.contains(" ")) {
            msg = "Branch name is invalid, please remove all spaces.";
            throw new InvalidDataException(msg);
        }
        else {
            try {
                repo.addBranch(branchName);
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
        if (branchName.contains(" ")) {
            msg = "Branch name is invalid, please remove all spaces.";
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
            throws InvalidDataException, DirectoryNotEmptyException{
        String msg;
        MagitStringResultObject resultObject = new MagitStringResultObject();
        if (branchName.contains(" ")) {
            msg = "Branch name is invalid, please remove all spaces.";
            throw new InvalidDataException(msg);
        }
        try {
            repo.checkoutBranch(branchName, ignoreChanges);
            resultObject.setIsHasError(false);
            msg = "Checkout was successful!";
            resultObject.setData(msg);
        }
        catch (DirectoryNotEmptyException e){
            throw new DirectoryNotEmptyException(e.getMessage());
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

    public MagitStringResultObject showHistoryDataForActiveBranch() {
        MagitStringResultObject resultObject = new MagitStringResultObject();
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

    public MagitStringResultObject resetBranch(String commitSha1, boolean ignore)
            throws DirectoryNotEmptyException{
        MagitStringResultObject resultObject = new MagitStringResultObject();
        String msg;
        try {
            repo.resetCommitInBranch(commitSha1, ignore);
            resultObject.setIsHasError(false);
            msg = "Reset Branch successfully!";
            resultObject.setData(msg);
        }
        catch (DirectoryNotEmptyException e){
            throw new DirectoryNotEmptyException(e.getMessage());
        }
        catch (Exception e){
            resultObject.setIsHasError(true);
            resultObject.setErrorMSG(e.getMessage());
        }
        return resultObject;
    }
}





