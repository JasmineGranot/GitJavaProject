package Engine;

import GitObjects.*;
import Utils.*;
import Exceptions.*;
import XMLHandler.*;
import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Magit {

    private final String NON_EXISTING_REPO_MSG = "No repository is configured at the moment.";

//  ======================== General Functions =============================

    private Repository getRepoForUser(User currentUser, String repoName) {
        List<Repository> userRepos = currentUser.getActiveRepositories();
        return Repository.getRepositoryByNameFromList(userRepos, repoName);
    }

//  ========================================================================
    public WorkingCopyChanges showStatus(User user, String repositoryName){
        String msg;
        WorkingCopyChanges changes = new WorkingCopyChanges();
        Repository repo = getRepoForUser(user, repositoryName);

        if (repo != null) {
            try {
                changes = repo.printWCStatus();
                msg = "Success";
                changes.setMsg(msg);
                changes.setHasErrors(false);
            } catch (IOException e) {
                msg = "The was an unhandled IOException! Exception message: " + e.getMessage();
                changes.setHasErrors(true);
                changes.setErrorMsg(msg);
            } catch (InvalidDataException e) {
                changes.setHasErrors(true);
                changes.setErrorMsg(e.getMessage());
            }
        }
        else{
            msg = String.format("Could not find the '%s' repository for user %s", user, repositoryName);
            changes.setHasErrors(true);
            changes.setErrorMsg(msg);
        }
        return changes;
    }

    public WorkingCopyChanges getFilesChangesBetweenBranches(User user, String repositoryName,
                                                             String target, String base){

        WorkingCopyChanges changes = new WorkingCopyChanges();
        Repository repo = getRepoForUser(user, repositoryName);
        if (repo != null) {
            try {
                changes = repo.printWCStatus();
                changes.setMsg("Success");
                changes.setHasErrors(false);
            } catch (IOException e) {
                changes.setHasErrors(true);
                changes.setErrorMsg("The was an unhandled IOException! Exception message: " + e.getMessage());
            } catch (InvalidDataException e) {
                changes.setHasErrors(true);
                changes.setErrorMsg(e.getMessage());
            }
        }
        else{
            changes.setHasErrors(true);
            changes.setErrorMsg(String.format("Could not find the '%s' repository for user %s", user, repositoryName));
        }
        return changes;

    }
//  ======================== Repository Functions ==========================

    public MagitStringResultObject loadUserData(User user) {
        MagitStringResultObject res = new MagitStringResultObject();
        File userPath = new File(user.getPath());
        if(userPath.exists()){
            File[] listOfUserRepos = userPath.listFiles();
            if (listOfUserRepos != null) {
                for (File curr : listOfUserRepos) {
                    Repository newRepository = new Repository(user, curr.getName());
                    try {
                        newRepository.loadRepositoryFromFile();
                        res.setIsHasError(false);
                    } catch (Exception e) {
                        res.setIsHasError(true);
                        res.setErrorMSG("Got exception while trying to load the users repositories!\n" + "Error message:"
                                + e.getMessage());
                    }
                }
            }
        }
        else {
            userPath.mkdir();
            res.setIsHasError(false);
        }
        return res;
    }

    public ResultList<PullRequestObject> getPullRequests(User user, String repoName){
        ResultList<PullRequestObject> res = new ResultList<>();
        Repository repo = getRepoForUser(user, repoName);
        if(repo != null) {
            try {
                res.setRes(repo.getRepoPullRequsetList());
                res.setHasError(false);
            } catch (Exception e) {
                String errorMsg = "could not fetch open pull requests";
                res.setHasError(true);
                res.setErrorMsg(errorMsg);
            }
        }
        else {
            res.setHasError(true);
            res.setErrorMsg("Repository undefined!");
        }
        return res;
    }

    public MagitStringResultObject getHeadBranchName(User user, String repoName) {
        MagitStringResultObject res = new MagitStringResultObject();
        Repository repo = getRepoForUser(user, repoName);
        if(repo != null) {
            try {
                res.setData(repo.getHeadBranch().getName());
                res.setIsHasError(false);
            } catch (Exception e) {
                String errorMsg = "Something went wrong while trying to pull from remote repository!\n" +
                        "Error message: " + e.getMessage();
                res.setIsHasError(true);
                res.setErrorMSG(errorMsg);
            }
        }
        else {
            res.setIsHasError(true);
            res.setErrorMSG("Repository undefined!");
        }
        return res;
    }

    public MagitStringResultObject getHeadBranchCommitSha1(User user, String repoName) {
        MagitStringResultObject res = new MagitStringResultObject();
        Repository repo = getRepoForUser(user, repoName);
        if(repo != null) {
            try {
                res.setData(repo.getHeadBranch().getCommitSha1());
                res.setIsHasError(false);
            } catch (Exception e) {
                String errorMsg = "Something went wrong while trying to get commit from remote repository!\n" +
                        "Error message: " + e.getMessage();
                res.setIsHasError(true);
                res.setErrorMSG(errorMsg);
            }
        }
        else {
            res.setIsHasError(true);
            res.setErrorMSG("Repository undefined!");
        }
        return res;
    }

    public MagitStringResultObject createNewRepo(User user, String repositoryName, String repoPath) {
        String msg;
        MagitStringResultObject result = new MagitStringResultObject();
        Repository repo = getRepoForUser(user, repositoryName);
        if(repo == null) {
            try {
                repo = new Repository();
                repo.createNewRepository(repoPath, true);
                msg = "Repository created successfully";
                result.setData(msg);
                result.setIsHasError(false);
            } catch (Exception e) {
                msg = e.getMessage();
                result.setErrorMSG(msg);
                result.setIsHasError(true);
            }
        }
        else {
            result.setIsHasError(true);
            result.setErrorMSG("Repository already exists!");
        }
        return result;
    }

    /*public MagitStringResultObject changeRepository(String repoPath) {
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
    }*/

    public MagitStringResultObject loadXMLForUser(User owner, String data) throws JAXBException, DataAlreadyExistsException, FileErrorException {
        XMLHandler xml = new XMLHandler(data, owner.getPath());
        if (xml.isRepoValid()) {
            String newRepoPath = MagitUtils.joinPaths(owner.getPath(), xml.getMagitRepository().getName());
            owner.createFileInServer(newRepoPath);
            return loadRepositoryFromXML(owner, xml,newRepoPath, false);
        }
        return null; //TODO
    }
    public MagitStringResultObject validateXML(User owner, String data) {
        MagitStringResultObject res = new MagitStringResultObject();
        try {
            XMLHandler xml = new XMLHandler(data, owner.getPath());
            if(xml.isRepoValid()) {
                res.setIsHasError(false);
            }
        } catch(Exception e) {
            res.setIsHasError(true);
            res.setErrorMSG(e.getMessage());
        }
        return res;
    }

    private MagitStringResultObject loadRepositoryFromXML(User owner, XMLHandler handler,String newRepoPath,
                                                          boolean toDeleteExistingRepo)
            throws DataAlreadyExistsException, JAXBException, FileErrorException {
        String msg;
        MagitStringResultObject result = new MagitStringResultObject();
        Repository repo = new Repository(owner, handler.getMagitRepository().getName());
        try{
            repo.loadRepoFromXML(handler.getMagitRepository(), newRepoPath, toDeleteExistingRepo);
            if (owner.addRepositoy(repo)){
                msg = "Repository loaded successfully!";
                result.setData(msg);
                result.setIsHasError(false);
            }
            else{
                msg = "Repository already exist!";
                result.setData(msg);
                result.setIsHasError(true);
            }

        }
        catch (DataAlreadyExistsException e){
            throw e;
        } catch (Exception e){
            result.setErrorMSG(e.getMessage());
            result.setIsHasError(true);
        }
        return result;
    }
//  ======================== Branch Functions ==============================

    public MagitStringResultObject getBranches(User user, String repoName) {
        MagitStringResultObject res = new MagitStringResultObject();
        Repository repo = getRepoForUser(user, repoName);
        if(repo != null) {
            try {
                List<String> branches = repo.getAllBranchesData().stream().
                        map(Branch::getName).collect(Collectors.toList());

                res.setDataList(branches);
                res.setIsHasError(false);
                res.setData("got Branches successfully!");
            } catch (Exception e) {
                res.setIsHasError(true);
                String errorMessage = "Something went wrong while trying to get Branches!\n" +
                        "Error message:" + e.getMessage();
                res.setErrorMSG(errorMessage);
            }
        }
        else {
            res.setIsHasError(true);
            res.setErrorMSG("Repository undefined!");
        }
        return res;
    }

    public MagitStringResultObject addNewBranch(User user, String repositoryName, String branchName, String sha1,
                                                boolean toIgnoreRemoteBranchsSha1) {
        MagitStringResultObject resultObject = new MagitStringResultObject();
        String msg;
        try {
            Repository repo = getRepoForUser(user, repositoryName);
            if (repo == null) {
                resultObject.setIsHasError(true);
                resultObject.setErrorMSG(NON_EXISTING_REPO_MSG);
            }
            if (branchName.contains(" ")) {
                msg = "Branch name is invalid, please try again without any spaces.";
                resultObject.setIsHasError(true);
                resultObject.setErrorMSG(msg);
            }
            else if (repo.isCommitSha1PointedByRTB(sha1) && !toIgnoreRemoteBranchsSha1) {
                msg = "The sha1 is currently pointed by a remote branch.";
                resultObject.setIsHasError(true);
                resultObject.setErrorMSG(msg);
            }
            else {
                repo.addBranch(branchName, sha1, null);
                resultObject.setIsHasError(false);
                msg = "Branch was added successfully!";
                resultObject.setData(msg);
            }
        } catch (Exception e) {
            resultObject.setIsHasError(true);
            resultObject.setErrorMSG(e.getMessage());
        }

        return resultObject;
    }

    public MagitStringResultObject deleteBranch(User user, String repositoryName, String branchName)
            throws InvalidDataException {
        MagitStringResultObject resultObject = new MagitStringResultObject();
        String msg;
        Repository repo = getRepoForUser(user, repositoryName);
        if(repo == null) {
            resultObject.setIsHasError(true);
            resultObject.setErrorMSG(NON_EXISTING_REPO_MSG);
            return resultObject;
        }
        if (branchName.equals(repo.getHeadBranch().getName())) {
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

    public MagitStringResultObject checkoutBranch(User user, String repositoryName,
                                                  String branchName, boolean ignoreChanges)
            throws DirectoryNotEmptyException{
        String msg;
        MagitStringResultObject resultObject = new MagitStringResultObject();
        Repository repo = getRepoForUser(user, repositoryName);

        if(repo == null) {
            resultObject.setIsHasError(true);
            resultObject.setErrorMSG(NON_EXISTING_REPO_MSG);
            return resultObject;
        }
        try {
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

    public MagitStringResultObject resetBranch(User user, String repositoryName, String commitSha1, boolean ignore)
            throws DirectoryNotEmptyException{
        MagitStringResultObject resultObject = new MagitStringResultObject();
        String msg;
        Repository repo = getRepoForUser(user, repositoryName);

        if(repo == null) {
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

//    public ResultList<Branch.BrancheData> showAllBranches(String user, String repositoryName) {
//        ResultList<Branch.BrancheData> result = new ResultList<>();
//        Repository repo = getRepoForUser(user, repositoryName);
//
//        if(repo == null) {
//            result.setHasError(true);
//            result.setErrorMsg(NON_EXISTING_REPO_MSG);
//            return result;
//        }
//        try{
//            result.setRes(repo.());
//            result.setHasError(false);
//        }
//        catch(Exception e){
//            result.setHasError(true);
//            result.setErrorMsg("Got Generic Exception!!!\n Error message: " + e.getMessage());
//        }
//        return result;
//    }

//    public MagitStringResultObject showHistoryDataForActiveBranch() {
//        MagitStringResultObject resultObject = new MagitStringResultObject();
//        if (!isRepositoryConfigured()){
//            resultObject.setIsHasError(true);
//            resultObject.setErrorMSG(NON_EXISTING_REPO_MSG);
//            return resultObject;
//        }
//        try{
//            resultObject = repo.getHistoryBranchData();
//            resultObject.setIsHasError(false);
//        }
//        catch (Exception e) {
//            resultObject.setIsHasError(true);
//            resultObject.setErrorMSG(e.getMessage());
//        }
//        return resultObject;
//    }

//  ======================== Commit Functions ==============================

    public MagitStringResultObject showFullCommitData(User user, String repositoryName){
        List<String> msg;
        MagitStringResultObject result = new MagitStringResultObject();
        Repository repo = getRepoForUser(user, repositoryName);

        if(repo == null) {
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

    public MagitStringResultObject createNewCommit(User user, String repositoryName,
                                                   String commitMsg, String secondCommitSha1) {
        MagitStringResultObject result = new MagitStringResultObject();
        boolean success;
        String msg;
        Repository repo = getRepoForUser(user, repositoryName);

        if(repo == null) {            result.setIsHasError(true);
            result.setErrorMSG(NON_EXISTING_REPO_MSG);
            return result;
        }
        try {
            success = repo.createNewCommit(user, commitMsg, secondCommitSha1);
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

    public ResultList getCurrentCommits(User user, String repoName) {
        ResultList<Commit.CommitData> res = new ResultList<>();
        Repository repo = getRepoForUser(user, repoName);
        if(repo != null) {
            res.setRes(repo.currentCommits());
            res.setHasError(false);
        }
        else{
            res.setErrorMsg("Could not get all commits");
            res.setHasError(true);
        }
        return res;
    }

    public ResultList getCurrentWC(User user, String repoName) {
        ResultList<String> res = new ResultList<>();
        Repository repo = getRepoForUser(user, repoName);
        if(repo != null) {
            res.setRes(repo.getCurrentCommitFullFilesData());
            res.setHasError(false);
        }
        else{
            res.setErrorMsg("Could not get all WC");
            res.setHasError(true);
        }
        return res;
    }


//  ======================== Collaborations Functions ======================

    public String merge(User user, String repositoryName, Branch branchToMerge, List<MergeResult> res)
            throws InvalidDataException, IOException, FileErrorException, DataAlreadyExistsException {
        Repository repo = getRepoForUser(user, repositoryName);
        if(repo != null) {
            return repo.merge(branchToMerge, res);
        }
        return null;
    }

    public MagitStringResultObject cloneRemoteToLocal(User user, Repository repoToClone) {
        MagitStringResultObject res = new MagitStringResultObject();
        Repository repo = new Repository(user, repoToClone.getRepoName());
        try {
            repo.clone(repoToClone);
            user.addRepositoy(repo);
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

    public MagitStringResultObject fetch(User user, String repoName) {
        MagitStringResultObject res = new MagitStringResultObject();
        Repository repo = getRepoForUser(user, repoName);
        if(repo != null) {
            try {
                repo.fetch();
                res.setIsHasError(false);
                res.setData("Fetched successfully!");
            } catch (Exception e) {
                String errorMsg = "Something went wrong while trying to fetch!\n" +
                        "Error message: " + e.getMessage();
                res.setIsHasError(true);
                res.setErrorMSG(errorMsg);
            }
        }
        else {
            res.setIsHasError(true);
            res.setErrorMSG("Repository undefined!");
        }
        return res;
    }

    public MagitStringResultObject pull(User user, String repoName) {
        MagitStringResultObject res = new MagitStringResultObject();
        Repository repo = getRepoForUser(user, repoName);
        if(repo != null) {
            try {
                repo.pull();
                res.setIsHasError(false);
                res.setData("Pulled from remote repository successfully!");
            } catch (Exception e) {
                String errorMsg = "Something went wrong while trying to pull from remote repository!\n" +
                        "Error message: " + e.getMessage();
                res.setIsHasError(true);
                res.setErrorMSG(errorMsg);
            }
        }
        else {
            res.setIsHasError(true);
            res.setErrorMSG("Repository undefined!");
        }
        return res;
    }

    public MagitStringResultObject push(User user, String repoName) {
        MagitStringResultObject res = new MagitStringResultObject();
        Repository repo = getRepoForUser(user, repoName);
        if(repo != null) {
            try {
                repo.push();
                res.setIsHasError(false);
                res.setData("Pulled successfully!");
            } catch (Exception e) {
                res.setIsHasError(true);
                String errorMessage = "Something went wrong while trying to pull!\n" +
                        "Error message:" + e.getMessage();
                res.setErrorMSG(errorMessage);
            }
        }
        else {
            res.setIsHasError(true);
            res.setErrorMSG("Repository undefined!");
        }
        return res;
    }
    public MagitStringResultObject createPR(User user, String repoName, String srcBranch,
                                            String targetBranch, String msg) {
        MagitStringResultObject res = new MagitStringResultObject();
        Repository repo = getRepoForUser(user, repoName);
        if(repo != null) {
            try {
                repo.addNewPullRequestToRepository(targetBranch, srcBranch, msg, user);
                res.setIsHasError(false);
                res.setData("Pull Request created successfully!");
            } catch (Exception e) {
                res.setIsHasError(true);
                String errorMessage = "Something went wrong while trying to pull!\n" +
                        "Error message:" + e.getMessage();
                res.setErrorMSG(errorMessage);
            }
        }
        else {
            res.setIsHasError(true);
            res.setErrorMSG("Repository undefined!");
        }
        return res;
    }

}