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
}
