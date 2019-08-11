import java.io.IOException;

public class Commit extends GitObjectsBase {
    private String commiter = null;
    private String msg = null;
    private String date = null;
    private String rootSha1 = null;
    private String last = null;

    void setCommitCreator(String name) {
        commiter = name;
    }

    private String getCommitCreator() {
        return commiter;
    }

    void setCommitDate(String date) {
        this.date = date;
    }

    private String getCommitDate() {
        return this.date;
    }

    void setCommitMessage(String msg) {
        this.msg = msg;
    }

    String getCommitMessage() {
        return this.msg;
    }

    void setRootSha1(String sha1) {
        rootSha1 = sha1;
    }

    String getRootSha1() {
        return rootSha1;
    }

    void setLastCommitSha1(String lastCommit) {
        last = lastCommit;
    }

    String getLastCommitSha1() {
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

    String exportCommitDataToString() {
        return String.format("%s%s%s%s%s%s%s", getRootSha1(), MagitUtils.DELIMITER, getCommitMessage(),
                MagitUtils.DELIMITER, getCommitDate(), MagitUtils.DELIMITER, getCommitCreator());
    }

    @Override
    void getDataFromFile(String filePath) throws IOException {
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
    void createFileFromObject(String destinationPath) {}
}
