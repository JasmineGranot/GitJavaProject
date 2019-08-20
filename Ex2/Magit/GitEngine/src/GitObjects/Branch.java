package GitObjects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Branch {
    private StringProperty name = new SimpleStringProperty("");
    private String commitSha1 = "";

    public Branch(String name, String commitSha1){
        setName(name);
        setCommitSha1(commitSha1);
    }

    public Branch(String name){
        setName(name);
    }

    private void setName(String name){
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
}
