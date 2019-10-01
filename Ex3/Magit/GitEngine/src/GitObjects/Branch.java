package GitObjects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Branch {
    private String name;
    private String commitSha1;
    private String trackedBranch;

    public Branch(String name, String commitSha1, String trackedBranch){
        this.name = name;
        setCommitSha1(commitSha1);
        setTrackedBranch(trackedBranch);
    }

    public Branch(String name){
        this.name = name;
        setTrackedBranch(null);
        setCommitSha1(null);
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCommitSha1(String commitSha1) {
        this.commitSha1 = commitSha1;
    }

    public String getName(){
        return name;
    }

    public String getCommitSha1(){
        return commitSha1;
    }

    public BrancheData getBranchData(){
       return new BrancheData();
    }

    public void setTrackedBranch(String trackedBranch) {
        this.trackedBranch = trackedBranch;
    }

    public String getTrackedBranch() {
        return trackedBranch;
    }

    public class BrancheData {
        private String branchName = name;
        private boolean isHead = false;
        private String sha1 = commitSha1;
        private String commitMsg = "";

        public String getBranchName(){
            return branchName;
        }

        public String getSha1(){
            return sha1;
        }

        public String getCommitMsg(){
            return commitMsg;
        }

        public boolean getIsHead(){
            return isHead;
        }

        public void setCommitMsg(String content) {
            commitMsg = content;
        }

        public void setHead(boolean isHead) {
            this.isHead = isHead;
        }
    }
}
