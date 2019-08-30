package GitObjects;

import Utils.MagitUtils;
import com.fxgraph.cells.AbstractCell;
import com.fxgraph.graph.Graph;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


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

    public static CommitData getCommitData(String commitSha1, Commit commitObj){
        CommitData data = new CommitData();
        data.getDataFromCommit(commitObj);
        data.setCommitSha1(commitSha1);
        return data;
    }

    public static class CommitData{
        private String commitSha1 = "";
        private String commitDate = "";
        private String commitMsg = "";
        private String commitWriter = "";
        private String commitsLastCommit = "";
        private String commitsRootSha1 = "";

        CommitData getDataFromCommit(Commit commitToGet){
            setCommitDate(commitToGet.getCommitDate());
            setCommitMsg(commitToGet.getCommitMessage());
            setCommitsLastCommit(commitToGet.getLastCommitSha1());
            setCommitsRootSha1(commitToGet.getRootSha1());
            setCommitWriter(commitToGet.getCommitCreator());
            return this;
        }

        String getCommitDate() {
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

    }

    /*public static class CommitNode extends AbstractCell {
        List<Commit.CommitData> sortedCommits;

        public CommitNode(List<Commit.CommitData> sortedCommits){
            setSortedCommits(sortedCommits);
        }

        private void setSortedCommits(List<CommitData> sortedCommits) {
            this.sortedCommits = sortedCommits;
        }

        @Override
        public Region getGraphic(Graph graph) {
            try {

                FXMLLoader fxmlLoader = new FXMLLoader();
                URL url = getClass().getResource("CommitNode.fxml");
                fxmlLoader.setLocation(url);
                GridPane root = fxmlLoader.load(url.openStream());
                return root;

            } catch (IOException e) {
                return new Label("Error when tried to create graphic node !");
            }
        }
    }*/
}
