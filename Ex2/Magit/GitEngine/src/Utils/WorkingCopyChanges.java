package Utils;

import java.util.HashSet;
import java.util.Set;

public class WorkingCopyChanges {
    private Set<String> changedFiles = new HashSet<>();
    private Set<String> deletedFiles = new HashSet<>();
    private Set<String> newFiles = new HashSet<>();
    private boolean isChanged;
    private String msg;
    private boolean hasErrors;
    private String errorMsg;

    public WorkingCopyChanges(){
        setChanged(false);
        setMsg("");
    }

    public WorkingCopyChanges(Set<String> changedFiles, Set<String> deletedFiles, Set<String> newFiles,
                       boolean isChanged){
        setChangedFiles(changedFiles);
        setDeletedFiles(deletedFiles);
        setNewFiles(newFiles);
        setChanged(isChanged);
    }

    public Set<String> getChangedFiles() {
        return changedFiles;
    }

    private void setChangedFiles(Set<String> changedFiles) {
        this.changedFiles.addAll(changedFiles);
    }

    public Set<String> getDeletedFiles() {
        return deletedFiles;
    }

    private void setDeletedFiles(Set<String> deletedFiles) {
        this.deletedFiles.addAll(deletedFiles);
    }

    public Set<String> getNewFiles() {
        return newFiles;
    }

    private void setNewFiles(Set<String> newFiles) {
        this.newFiles.addAll(newFiles);
    }

    public boolean isChanged() {
        return isChanged;
    }

    private void setChanged(boolean changed) {
        isChanged = changed;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean getHasErrors(){
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors){
        this.hasErrors = hasErrors;
    }
}
