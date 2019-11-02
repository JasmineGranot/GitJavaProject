package GitObjects;

public class RepositoryWebData {
    private String name;
    private String activeBranch;
    private String numOfBranches;
    private String lastCommitDate;
    private String repoOwner;

    public RepositoryWebData(Repository repo) {
        name = repo.getRepoName();
        repoOwner = repo.getRepoOwner();
        numOfBranches = String.valueOf(repo.getAllBranchesData().size());
        if (repo.getAllBranchesData().size() == 0){
            activeBranch = "";
            lastCommitDate = "";
        }
        else{
            activeBranch = repo.getHeadBranch().getName();
            lastCommitDate = repo.getLastCommit().getCommitDate() != null ? repo.getLastCommit().getCommitDate() : "";
        }
    }

    public String getName() {
        return name;
    }

    public String getActiveBranch() {
        return activeBranch;
    }

    public String getLastCommitDate() {
        return lastCommitDate;
    }

    public String getNumOfBranches() {
        return numOfBranches;
    }
}