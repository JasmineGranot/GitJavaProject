package GitObjects;

import Utils.MagitUtils;
import puk.team.course.magit.ancestor.finder.CommitRepresentative;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class Commit extends GitObjectsBase implements CommitRepresentative {
    private String commiter = null;
    private String msg = null;
    private String date = null;
    private String rootSha1 = null;
    private String last = null;
    private String last2 = null;
    private String commitSha1 = null;

    public void setCommitCreator(String name) {
        commiter = name;
    }
     public boolean isEmpty(){
        return commiter == null && msg == null && date == null && rootSha1 == null;
     }

    private String getCommitCreator() {
        return commiter;
    }

    public void setCommitDate(String date) {
        this.date = date;
    }

    public String getCommitDate() {
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

    public void setCommitSha1(String sha1) {
        commitSha1 = sha1;
    }

    public String getCommitSha1() {
        return commitSha1;
    }

    public void setFirstPrecedingSha1(String lastCommit) {
        last = lastCommit;
    }

    public void setSecondPrecedingSha1(String last2) {
        this.last2 = last2;
    }

    @Override
    public String toString() {
        String lastCommit = getFirstPrecedingSha1();
        String secondLastCommit = getSecondPrecedingSha1();
        if (lastCommit == null || lastCommit.equals("")) {
            lastCommit = "";
        }
        if(secondLastCommit == null || secondLastCommit.equals("")) {
            secondLastCommit = "";
        }

        return String.format("%s%s%s%s%s%s%s%s%s%s%s", getRootSha1(), MagitUtils.DELIMITER, lastCommit,
                MagitUtils.DELIMITER, secondLastCommit, MagitUtils.DELIMITER, getCommitMessage(), MagitUtils.DELIMITER,
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
            setFirstPrecedingSha1(!(dataFields[1].equals("")) ? dataFields[1] : null);

            if(dataFields.length == 6) {
                setSecondPrecedingSha1(!(dataFields[2].equals("")) ? dataFields[2] : null);
                setCommitMessage(dataFields[3]);
                setCommitDate(dataFields[4]);
                setCommitCreator(dataFields[5]);
            }
            else {
                setCommitMessage(dataFields[2]);
                setCommitDate(dataFields[3]);
                setCommitCreator(dataFields[4]);
            }
        }
    }

    @Override
    public boolean isCommit() {
        return true;
    }

    @Override
    public void createFileFromObject(String destinationPath) {}

    public static CommitData getCommitData(String commitSha1, Commit commitObj, String branchName){
        CommitData data = new CommitData();
        data.getDataFromCommit(commitObj, branchName);
        data.setCommitSha1(commitSha1);
        return data;
    }

    @Override
    public String getSha1() {
        return this.doSha1();
    }

    @Override
    public String getFirstPrecedingSha1() {
        if(last != null) {
            return last;
        }
        return "";
    }

    @Override
    public String getSecondPrecedingSha1() {
        if(last2 != null) {
            return last2;
        }
        return "";
    }

    public static class CommitData{
        private String commitSha1 = "";
        private String commitDate = "";
        private String commitMsg = "";
        private String commitWriter = "";
        private String commitsLastCommit = "";
        private String commitsLast2Commit = "";
        private String commitsRootSha1 = "";
        private String branchName = "";
        private List<String> branches = new LinkedList<>();

        CommitData getDataFromCommit(Commit commitToGet, String branchName){
            setCommitDate(commitToGet.getCommitDate());
            setCommitMsg(commitToGet.getCommitMessage());
            setCommitsLastCommit(commitToGet.getFirstPrecedingSha1());
            setCommitsLast2Commit(commitToGet.getSecondPrecedingSha1());
            setCommitsRootSha1(commitToGet.getRootSha1());
            setCommitWriter(commitToGet.getCommitCreator());
            setBranchName(branchName);
            return this;
        }

        public void setBranches(List<String> branches) {
            this.branches = branches;
        }

        public List<String> getBranches() {
            return branches;
        }

        private void setCommitsLast2Commit(String commitsLast2Commit) {
            this.commitsLast2Commit = commitsLast2Commit;
        }

        public String getCommitsLast2Commit() {
            if(commitsLast2Commit == null) {
                return "";
            }
            return commitsLast2Commit;
        }

        public void setBranchName(String branchName) {
            this.branchName = branchName;
        }

        public String getBranchName() {
            return branchName;
        }

        public String getCommitDate() {
            return commitDate;
        }

        void setCommitDate(String commitDate) {
            this.commitDate = commitDate;
        }

        public Date getCommitDateAsDate() throws ParseException {
            SimpleDateFormat format = new SimpleDateFormat((MagitUtils.DATE_PATTERN));
            return format.parse(getCommitDate());
        }

        public String getCommitMsg() {
            return commitMsg;
        }

        private void setCommitMsg(String commitMsg) {
            this.commitMsg = commitMsg;
        }

        public String getCommitSha1() {
            return commitSha1;
        }

        void setCommitSha1(String commitSha1) {
            this.commitSha1 = commitSha1;
        }

        public String getCommitsLastCommit() {
            if(commitsLastCommit == null)
            {
                return "";
            }
            return commitsLastCommit;
        }

        void setCommitsLastCommit(String commitsLastCommit) {
            this.commitsLastCommit = commitsLastCommit;
        }

        public String getCommitsRootSha1() {
            return commitsRootSha1;
        }

        void setCommitsRootSha1(String commitsRootSha1) {
            this.commitsRootSha1 = commitsRootSha1;
        }

        public String getCommitWriter() {
            return commitWriter;
        }

        void setCommitWriter(String commitWriter) {
            this.commitWriter = commitWriter;
        }

        public String toString(){
            return String.format("%s%s%s%s%s", getCommitSha1(), getCommitsLastCommit(), getCommitMsg(),
                    getCommitDate(), getCommitWriter());
        }
    }
}
