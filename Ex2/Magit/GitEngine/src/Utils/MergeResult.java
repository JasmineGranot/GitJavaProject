package Utils;

import java.io.File;
import java.io.IOException;

public class MergeResult {
    private boolean hasConflict = false;
    private boolean succeeded = false;
    private String errorMsg = null;
    private String firstContent = null;
    private String secondContent = null;
    private String ancestorContent = null;
//    private String firstBranchName = null;
//    private String secondBranchName = null;
//    private String ancestorBranchName = null;
    private String successMsg = null;
    private String conflictMsg = null;
    private String fileName = null;
    private String filePath = null;


    void setConflictMsg(String conflictMsg) {
        this.conflictMsg = conflictMsg;
    }

    public String getConflictMsg() {
        return conflictMsg;
    }

//    public void setAncestorBranchName(String ancestorBranchName) {
//        this.ancestorBranchName = ancestorBranchName;
//    }
//
//    public String getAncestorBranchName() {
//        return ancestorBranchName;
//    }
//
//    public void setFirstBranchName(String firstBranchName) {
//        this.firstBranchName = firstBranchName;
//    }
//
//    public String getFirstBranchName() {
//        return firstBranchName;
//    }
//
//    public void setSecondBranchName(String secondBranchName) {
//        this.secondBranchName = secondBranchName;
//    }
//
//    public String getSecondBranchName() {
//        return secondBranchName;
//    }


    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    void setFileName(String fileName) {
        File file = new File(fileName);
        this.fileName = file.getName();
    }

    String getFileName() {
        return fileName;
    }

    public void setAncestorContent(String ancestorContent) {
        this.ancestorContent = ancestorContent;
    }

    public String getAncestorContent() {
        return ancestorContent;
    }

    public void setFirstContent(String firstContent) {
        this.firstContent = firstContent;
    }

    public String getFirstContent() {
        return firstContent;
    }

    public void setSecondContent(String secondContent) {
        this.secondContent = secondContent;
    }

    public String getSecondContent() {
        return secondContent;
    }

    public void setSuccessMsg(String successMsg) {
        this.successMsg = successMsg;
    }

    public String getSuccessMsg() {
        return successMsg;
    }

    public boolean getSucceeded() {
        return this.succeeded;
    }

    public boolean getHasConflicts() {
        return this.hasConflict;
    }

    void setHasConflict(boolean hasConflict) {
        this.hasConflict = hasConflict;
    }

    void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    void writeFileToRepository(String filePath) throws IOException {
        String data = MagitUtils.unZipAndReadFile(filePath);
        File newFile = new File(filePath);
        if(newFile.delete()){
            if(newFile.createNewFile()) {
                MagitUtils.writeToFile(filePath, data);
            }
        }
    }

    boolean deleteFileFromRepository(String filePath) {
        File toDeleteFile = new File(filePath);
        return toDeleteFile.delete();
    }
}
