package GitObjects;

import Utils.MagitUtils;

import java.io.IOException;

public class Commit extends GitObjectsBase {
    private String commiter = null;
    private String msg = null;
    private String date = null;
    private String rootSha1 = null;
    private String last = null;

    public void setCommitCreator(String name) {
        commiter = name;
    }

    private String getCommitCreator() {
        return commiter;
    }

    public void setCommitDate(String date) {
        this.date = date;
    }

    private String getCommitDate() {
        return this.date;
    }

    public void setCommitMessage(String msg) {
        this.msg = msg;
    }

    public String getCommitMessage() {
        return this.msg;
    }

    public void setRootSha1(String sha1) {
        rootSha1 = sha1;
    }

    public String getRootSha1() {
        return rootSha1;
    }

    public void setLastCommitSha1(String lastCommit) {
        last = lastCommit;
    }

    public String getLastCommitSha1() {
        return last;
    }

    @Override
    public String toString() {
        String lastCommit = getLastCommitSha1();
        if (lastCommit == null) {
            lastCommit = "";
        }

        return String.format("%s%s%s%s%s%s%s%s%s", getRootSha1(), MagitUtils.DELIMITER, lastCommit,
                MagitUtils.DELIMITER, getCommitMessage(), MagitUtils.DELIMITER,
                getCommitDate(), MagitUtils.DELIMITER, getCommitCreator());
    }

    public String exportCommitDataToString(String currentCommitSha1) {
        return String.format("Commit sha1: %s\n" +
                        "Commit Message: %s \n" +
                        "Commit Date: %s \n" +
                        "Commit author: %s", currentCommitSha1, getCommitMessage(), getCommitDate(),
                getCommitCreator());
    }

    @Override
    public void getDataFromFile(String filePath) throws IOException {
        String data = MagitUtils.unZipAndReadFile(filePath);
        String[] dataFields = data.split(MagitUtils.DELIMITER);

        if (data.length() != 0) {
            setRootSha1(dataFields[0]);
            setLastCommitSha1(!(dataFields[1].equals("null")) ? dataFields[1] : null);
            setCommitMessage(dataFields[2]);
            setCommitDate(dataFields[3]);
            setCommitCreator(dataFields[4]);
        }
    }

    @Override
    public boolean isCommit() {
        return true;
    }

    @Override
    public void createFileFromObject(String destinationPath) {}
}
