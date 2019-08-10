import java.io.IOException;

class Magit {

    private String userName = "Administrator";
    private Repository repo = new Repository();

    void setUserName(String newName) {
        this.userName = newName;
    }

    boolean createNewRepo(String repoPath) {
        return repo.createNewRepository(repoPath, true);
    }

    void loadRepositoryFromXml(String repoPath){
        try{
            repo.loadRepoFromXML(repoPath);
        }
        catch (IOException e){
            System.out.println("issue with io");
        }
    }

    void createNewCommit(String commitMsg) {
        repo.createNewCommit(userName, commitMsg);
    }

    void showStatus() {
        System.out.println(String.format("The current repository is: %s", repo.getRootPath()));
        System.out.println(String.format("The current user is: %s", userName));
        repo.printWCStatus();
    }

    MagitStringResultObject showAllBranches() {
        MagitStringResultObject result = new MagitStringResultObject();
        try{
            result.data = repo.showAllBranchesData();
             result.haveError = false;
        }
        catch(Exception e){
            result.haveError = true;
            result.errorMSG = e.getMessage();

        }
        return result;
    }

    void addNewBranch(String branchName) {
        if (branchName.contains(" ")) {
            System.out.println("Branch name is invalid, please remove all spaces.");
        } else {
            repo.addNewBranchToRepo(branchName);
        }
    }

    void deleteBranch(String branchName) {
        try {
            if (branchName.contains(" ")) {
                System.out.println("Branch name is invalid, please remove all spaces.");
            } else {
                repo.removeBranch(branchName);
            }
        } catch (Exception e) {
            System.out.println("issue found");
        }
    }

    void checkoutBranch(String branchName) {
        repo.checkoutBranch(branchName, false);
    }

    void showHistoryDataForActiveBranch() {
        repo.getHistoryBranchData();
    }

    void changeRepository(String newRepoName) {
        try{
            repo.changeRepo(newRepoName);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    String showFullCommitData(){
        return repo.getCurrentCommitFullFilesData();
    }

    void resetBranch(String commitSha1){
        repo.resetCommitInBranch(commitSha1, false);
    }
}





