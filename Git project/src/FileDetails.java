public class FileDetails {
    private String fileName;
    private String sha1;
    private String type;
    private String lastChanger;
    private String lastChangedDate;


    FileDetails(String fileName, String sha1, String type, String lastChanger, String lastChangedDate){
        setFileName(fileName);
        setSha1(sha1);
        setType(type);
        setLastChanger(lastChanger);
        setLastChangedDate(lastChangedDate);
    }

    String getFileName(){
        return this.fileName;
    }

    private void setFileName(String newName){
        this.fileName = newName;
    }

    String getSha1() {
        return this.sha1;
    }

    private void setSha1(String newSha1){
        this.sha1 = newSha1;
    }

    String getType(){
        return this.type;
    }

    private void setType(String fileType){
        this.type = fileType;
    }

    private String getLastChanger(){
        return this.lastChanger;
    }

    private void setLastChanger(String changerName){
        this.lastChanger = changerName;
    }

    private String getLastChangedDate(){
        return this.lastChangedDate;
    }

    private void setLastChangedDate(String newChangedDate){
        this.lastChangedDate = newChangedDate;
    }

    @Override
    public String toString() {
        return String.format("%s%s%s%s%s%s%s%s%s", getFileName(),MagitUtils.DELIMITER, getSha1(),
                MagitUtils.DELIMITER, getType(), MagitUtils.DELIMITER, getLastChanger(),
                MagitUtils.DELIMITER, getLastChangedDate());
    }

    String getToStringWithFullPath(String path) {
        return String.format("%s%s%s%s%s%s%s%s%s", MagitUtils.joinPaths(path, getFileName()),
                MagitUtils.DELIMITER, getSha1(), MagitUtils.DELIMITER, getType(),
                MagitUtils.DELIMITER, getLastChanger(),
                MagitUtils.DELIMITER, getLastChangedDate());
    }
}
