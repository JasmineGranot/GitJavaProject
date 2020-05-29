package GitObjects;
import Engine.Magit;
import Utils.MagitUtils;

public class PullRequestObject {
    private String targetToMergeFrom;
    private String baseToMergeInto;
    private String prMsg;
    private String owner;
    private String repoManagerMsg;
    private String status;

    public PullRequestObject() {
        targetToMergeFrom = null;
        baseToMergeInto = null;
        prMsg = null;
        owner = null;
        repoManagerMsg = null;
        status = MagitUtils.OPEN_PULL_REQUEST;
    }
    public PullRequestObject(String s) {
        String[] vals = s.split(MagitUtils.DELIMITER);
        owner = vals[0];
        prMsg = vals[1];
        status = vals[2];
        baseToMergeInto = vals[3];
        targetToMergeFrom = vals[4];
        repoManagerMsg = (s.length() > 5 && !vals[5].equals("undefined"))? vals[5] : null;
    }

    public void setBaseToMergeInto(String baseToMergeInto) {
        this.baseToMergeInto = baseToMergeInto;
    }

    public String getBaseToMergeInto() {
        return baseToMergeInto;
    }

    public void setTargetToMergeFrom(String targetToMergeFrom) {
        this.targetToMergeFrom = targetToMergeFrom;
    }

    public String getTargetToMergeFrom() {
        return targetToMergeFrom;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
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

    public boolean isOpenPullRequest(){
        return getStatus().equals(MagitUtils.OPEN_PULL_REQUEST);
    }

    @Override
    public String toString() {
        String[] s = {getOwner(), getPrMsg(), getStatus(), getBaseToMergeInto(),
                getTargetToMergeFrom(),getRepoManagerMsg()};
        return String.join(MagitUtils.DELIMITER,s);
    }

}
