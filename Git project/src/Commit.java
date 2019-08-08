public class Commit extends GitObjectsBase {
    private String commiter = null;
    private String msg = null;
    private String date = null;
    private String root_sha1 = null;
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
        root_sha1 = sha1;
    }

    String getRootSha1() {
        return root_sha1;
    }

    void setLastCommitsha1(String lastCommit) {
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
    void getDataFromFile(String filePath) {
        String data = MagitUtils.unZipAndReadFile(filePath);
        if (data != null) {
            String[] datafields = data.split(MagitUtils.DELIMITER);
            if (data.length() != 0) {
                setRootSha1(datafields[0]);
                setLastCommitsha1(!(datafields[1].equals("null")) ? datafields[1] : null);
                setCommitMessage(datafields[2]);
                setCommitDate(datafields[3]);
                setCommitCreator(datafields[4]);
            }
        }
    }

    @Override
    public boolean isCommit() {
        return true;
    }

    @Override
    void createFileFromObject(String destinationPath) {}
}
