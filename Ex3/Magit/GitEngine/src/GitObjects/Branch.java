package GitObjects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Branch {
    private StringProperty name;
    private String commitSha1;

    public Branch(String name, String commitSha1){
        this.name = new SimpleStringProperty(name);
        setCommitSha1(commitSha1);
    }

    public Branch(String name){
        this.name = new SimpleStringProperty(name);
    }

    public void setName(String name){
        this.name.setValue(name);
    }

    public void setCommitSha1(String commitSha1) {
        this.commitSha1 = commitSha1;
    }

    public StringProperty getName(){
        return name;
    }

    public String getCommitSha1(){
        return commitSha1;
    }

    public BrancheData getBranchData(){
       return new BrancheData();
    }

    public class BrancheData {
        private String branchName = name.getValue();
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
