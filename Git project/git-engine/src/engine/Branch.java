package engine;

public class Branch {
    private String name = "";
    private String commitSha1 = "";

    Branch(String name, String commitSha1){
        setName(name);
        setCommitSha1(commitSha1);
    }
    Branch(String name){
        setName(name);
    }

    private void setName(String name){
        this.name = name;
    }

    void setCommitSha1(String commitSha1) {
        this.commitSha1 = commitSha1;
    }

    String getName(){
        return name;
    }

    String getCommitSha1(){
        return commitSha1;
    }
}
