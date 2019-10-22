package GitObjects;

public class RepositoryWebData {
    private String name;
    private String activeBranch;
    private String numOfBranches;
    private String lastCommitDate;

    public RepositoryWebData(Repository repo) {
        name = repo.getRepoName();
        activeBranch = repo.getHeadBranch().getName();
        numOfBranches = String.valueOf(repo.getAllBranchesData().size());
        lastCommitDate = repo.getLastCommit().getCommitDate();
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