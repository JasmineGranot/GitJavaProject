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
            result.data = msg;
            result.haveError = false;
        }
        catch (Exception e){
            msg = e.getMessage();
            result.errorMSG = msg;
            result.haveError = true;
        }
        return result;
    }

    public MagitStringResultObject changeRepository(String newRepoName) {
        String msg;
        MagitStringResultObject result = new MagitStringResultObject();
        try{
            repo.changeRepo(newRepoName);
            msg = "engine.Repository changed successfully!";
            result.data = msg;
            result.haveError = false;
        }
        catch (IOException e){
            msg = "Unhandled IOException!";
            result.errorMSG = msg;
            result.haveError = true;
        }
        catch (Exception e){
            result.errorMSG = e.getMessage();
            result.haveError = true;
        }
        return result;
    }

    public MagitStringResultObject showFullCommitData(){
        String msg;
        MagitStringResultObject result = new MagitStringResultObject();
        try{
            msg = repo.getCurrentCommitFullFilesData();
            result.data = msg;
            result.haveError = false;
        }
        catch (Exception e){
            msg = "There was an unhandled exception!";
            result.errorMSG = msg + "\nException msg: " + e.getMessage();
            result.haveError = true;
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
                result.data = msg;
                result.haveError = false;
            }
            else{
                msg = "Nothing has changed in the repository!";
                result.data = msg;
                result.haveError = false;
            }
        }
        catch (InvalidDataException e){
            result.haveError = true;
            result.errorMSG = e.getMessage();
        }
        catch (FileErrorException e)
        {
            msg = "Something went wrong while trying to update the files in the system!" +
                    "Error message: " + e.getMessage();
            result.haveError = true;
            result.errorMSG = msg;
        }
        catch (IOException e)
        {
            msg = "There was an unhandled IOException!\n" +
                    "Error message: " + e.getMessage();
            result.haveError = true;
            result.errorMSG = msg;
        }
        return result;
    }

    public MagitStringResultObject showAllBranches() {
        MagitStringResultObject result = new MagitStringResultObject();
        try{
            result.data = repo.showAllBranchesData();
            result.haveError = false;
        }
        catch(Exception e){
            result.haveError = true;
            result.errorMSG = "Got EXCEPTION!!! " + e.getMessage();
        }
        return result;
    }

    public MagitStringResultObject addNewBranch(String branchName) throws InvalidDataException {
        String msg;
        MagitStringResultObject resultObject = new MagitStringResultObject();
        if (branchName.contains(" ")) {
            msg = "engine.Branch name is invalid, please remove all spaces.";
            throw new InvalidDataException(msg);
        }
        else {
            try {
                repo.addBranch(branchName);
                resultObject.haveError = false;
                msg = "engine.Branch was added successfully!";
                resultObject.data =  msg;
            }
            catch (DataAlreadyExistsException e) {
                resultObject.haveError = true;
                msg = "Had an issue while trying to add the new branch!\n" +
                        "Error message: " + e.getMessage();
                resultObject.errorMSG = msg;
            }
            catch (IOException e) {
                resultObject.haveError = true;
                msg = "Had an unhandled IOException!\n" +  "Error message: " + e.getMessage();
                resultObject.errorMSG = msg;
            }
        }
        return resultObject;
    }

    public MagitStringResultObject deleteBranch(String branchName) throws InvalidDataException {
        MagitStringResultObject resultObject = new MagitStringResultObject();
        String msg;
        if (branchName.contains(" ")) {
            msg = "engine.Branch name is invalid, please remove all spaces.";
            throw new InvalidDataException(msg);
        }
        else {
            try {
                repo.removeBranch(branchName);
                msg = "engine.Branch deleted successfully!";
                resultObject.haveError = false;
                resultObject.data = msg;
            }
            catch (Exception e){
                msg = "Could not delete the branch!\nError message: " + e.getMessage();
                resultObject.haveError = true;
                resultObject.errorMSG = msg;
            }
        }
        return resultObject;
    }

    public MagitStringResultObject checkoutBranch(String branchName, boolean ignoreChanges)
            throws InvalidDataException, DirectoryNotEmptyException{
        String msg;
        MagitStringResultObject resultObject = new MagitStringResultObject();
        if (branchName.contains(" ")) {
            msg = "engine.Branch name is invalid, please remove all spaces.";
            throw new InvalidDataException(msg);
        }
        try {
            repo.checkoutBranch(branchName, ignoreChanges);
            resultObject.haveError = false;
            msg = "Checkout was successful!";
            resultObject.data = msg;
        }
        catch (DirectoryNotEmptyException e){
            throw new DirectoryNotEmptyException(e.getMessage());
        }
        catch (InvalidDataException e){
            msg = "Had an issue while trying to checkout branch!\nError message: " + e.getMessage();
            resultObject.haveError = true;
            resultObject.errorMSG = msg;
        }
        catch (Exception e){
            resultObject.haveError = true;
            resultObject.errorMSG = e.getMessage();
        }
        return resultObject;
    }

    public MagitStringResultObject showHistoryDataForActiveBranch() {
        MagitStringResultObject resultObject = new MagitStringResultObject();
        try{
            resultObject = repo.getHistoryBranchData();
            resultObject.haveError = false;
        }
        catch (Exception e) {
            resultObject.haveError = true;
            resultObject.errorMSG = e.getMessage();
        }
        return resultObject;
    }

    public MagitStringResultObject resetBranch(String commitSha1, boolean ignore)
            throws DirectoryNotEmptyException{
        MagitStringResultObject resultObject = new MagitStringResultObject();
        String msg;
        try {
            repo.resetCommitInBranch(commitSha1, ignore);
            resultObject.haveError = false;
            msg = "Reset engine.Branch successfully!";
            resultObject.data = msg;
        }
        catch (DirectoryNotEmptyException e){
            throw new DirectoryNotEmptyException(e.getMessage());
        }
        catch (Exception e){
            resultObject.haveError = true;
            resultObject.errorMSG = e.getMessage();
        }
        return resultObject;
    }
}





