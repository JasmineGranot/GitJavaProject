package GitObjects;
import Utils.MagitUtils;

public class PullRequestObject {
    private Branch targetToMergeFrom;
    private Branch baseToMergeInto;
    private String prMsg;
    private User owner;
    private String repoManagerMsg;
    private String status;
    private Repository pullRequestRepository;
    private boolean approvedByRepoOwner;

    public PullRequestObject() {
        targetToMergeFrom = null;
        baseToMergeInto = null;
        prMsg = null;
        owner = null;
        pullRequestRepository = null;
        repoManagerMsg = null;
        status = MagitUtils.OPEN_PULL_REQUEST;
        approvedByRepoOwner = false;

    }

    public void setApprovedByRepoOwner(boolean approvedByRepoOwner) {
        this.approvedByRepoOwner = approvedByRepoOwner;
    }

    public boolean getApprovedByOwner() {
        return approvedByRepoOwner;
    }

    public void setPullRequestRepository(Repository pullRequestRepository) {
        this.pullRequestRepository = pullRequestRepository;
    }

    public Repository getPullRequestRepository() {
        return pullRequestRepository;
    }

    public void setBaseToMergeInto(Branch baseToMergeInto) {
        this.baseToMergeInto = baseToMergeInto;
    }

    public Branch getBaseToMergeInto() {
        return baseToMergeInto;
    }

    public void setTargetToMergeFrom(Branch targetToMergeFrom) {
        this.targetToMergeFrom = targetToMergeFrom;
    }

    public Branch getTargetToMergeFrom() {
        return targetToMergeFrom;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getOwner() {
        return owner;
    }

    public void setPrMsg(String prMsg) {
        this.prMsg = prMsg;
    }

    public String getPrMsg() {
        return prMsg;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setRepoManagerMsg(String repoManagerMsg) {
        this.repoManagerMsg = repoManagerMsg;
    }

    public String getRepoManagerMsg() {
        return repoManagerMsg;
    }
}
