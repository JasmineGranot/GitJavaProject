package GitObjects;

public class FileToMerge {
    private String fileName;
    private String sha1InFirstCommit;
    private String sha1InSecondCommit;
    private String sha1InAncestor;

    public FileToMerge(String fileName, String sha1InFirstCommit, String sha1InSecondCommit,  String sha1InAncestor) {
        setFileName(fileName);
        setSha1InAncestor(sha1InAncestor);
        setSha1InFirstCommit(sha1InFirstCommit);
        setSha1InSecondCommit(sha1InSecondCommit);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setSha1InAncestor(String sha1InAncestor) {
        this.sha1InAncestor = sha1InAncestor;
    }

    public void setSha1InFirstCommit(String sha1InFirstCommit) {
        this.sha1InFirstCommit = sha1InFirstCommit;
    }

    public void setSha1InSecondCommit(String sha1InSecondCommit) {
        this.sha1InSecondCommit = sha1InSecondCommit;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSha1InAncestor() {
        return sha1InAncestor;
    }

    public String getSha1InFirstCommit() {
        return sha1InFirstCommit;
    }

    public String getSha1InSecondCommit() {
        return sha1InSecondCommit;
    }
}
