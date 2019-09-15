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
    private String secondSha1 = null;
    private String successMsg = null;
    private String conflictMsg = null;
    private String fileName = null;
    private String filePath = null;

    public void setSecondSha1(String secondSha1) {
        this.secondSha1 = secondSha1;
    }

    public String getSecondSha1() {
        return secondSha1;
    }

    void setConflictMsg(String conflictMsg) {
        this.conflictMsg = conflictMsg;
    }

    public String getConflictMsg() {
        return conflictMsg;
    }

    void setFilePath(String filePath) {
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

    void writeFileToRepository(String filePath, String fileSha1) throws IOException {
        String data;
        if(fileSha1 != null) {
            data = MagitUtils.unZipAndReadFile(fileSha1);
        }
        else{
            data = MagitUtils.unZipAndReadFile(filePath);
        }
        File newFile = new File(filePath);

        if(newFile.exists()) {
            if(newFile.delete()){
                if(newFile.createNewFile()) {
                    MagitUtils.writeToFile(filePath, data);
                }
            }
        }
        else {
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
