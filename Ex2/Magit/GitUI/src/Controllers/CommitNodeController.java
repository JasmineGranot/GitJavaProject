package Controllers;

import GitObjects.Branch;
import GitObjects.Commit;
import Layout.CommitTreeLayout;
import com.fxgraph.edges.Edge;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.graph.Model;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import UIUtils.CommitNode;

public class CommitNodeController {

    @FXML
    private Circle commitNode;

    @FXML private Label commitDate;
    @FXML private Label commitWriter;
    @FXML private Label commitMsg;
    @FXML private Label lastCommitSha1;
    @FXML private Label rootSha1;
    @FXML private Label isBranch;
    @FXML private Label commitSha1Label;
    @FXML private Label rootSha1Label;
    @FXML private Label commitSha1Label2;
    @FXML private Label secondLastCommitSha1;

    private List<Commit.CommitData> sortedCommits;

    void setSortedCommits(List<Commit.CommitData> sortedCommits, Branch branchName) {
        this.sortedCommits = sortedCommits;
        for(Commit.CommitData curr : sortedCommits) {
            if(curr.getCommitSha1().equals(branchName.getCommitSha1())) {
                curr.setBranchName(branchName.getName().getValue());
            }
        }

    }

    void addCommitToSortedCommits(Commit.CommitData commitToAdd, Branch branchName) {
        this.sortedCommits.add(0, commitToAdd);
        for(Commit.CommitData curr : sortedCommits) {
            if(curr.getBranchName() != null) {
                curr.setBranchName("");
            }
        }
        this.sortedCommits.get(0).setBranchName(branchName.getName().getValue());
    }

    public void setCommitMessage(String commitMessage) {
        commitMsg.setText(commitMessage);
        commitMsg.setTooltip(new Tooltip(commitMessage));
    }

    public void setCommitDate(String commitDate) {
        this.commitDate.setText(commitDate);
        this.commitDate.setTooltip(new Tooltip(commitDate));
    }

    public void setCommitWriter(String commitWriter) {
        this.commitWriter.setText(commitWriter);
        this.commitWriter.setTooltip(new Tooltip(commitWriter));
    }

    public void setIsBranch(String isBranch) {
        if(isBranch != null) {
            this.isBranch.setText(isBranch);
            this.isBranch.setTooltip(new Tooltip(isBranch));
        }
        else {
            this.isBranch.setVisible(false);
        }
    }

    public void setLastCommitSha1(String lastCommitSha1) {
        if(!lastCommitSha1.equals("")) {
            this.lastCommitSha1.setText(lastCommitSha1);
            this.lastCommitSha1.setTooltip(new Tooltip(lastCommitSha1));
        }
        else {
            this.lastCommitSha1.setText("None (First commit)");
        }
    }

    public void setRootSha1(String rootSha1) {
        if(!rootSha1.equals("")) {
            this.rootSha1.setText(rootSha1);
            this.rootSha1.setTooltip(new Tooltip(rootSha1));
        }
        else {
            this.rootSha1Label.setVisible(false);
            this.rootSha1.setVisible(false);
        }
    }

    public void setSecondLastCommitSha1(String secondLastCommitSha1) {
        if(!secondLastCommitSha1.equals("")) {
            this.secondLastCommitSha1.setText(secondLastCommitSha1);
            this.secondLastCommitSha1.setTooltip(new Tooltip(secondLastCommitSha1));
        }
        else {
            this.secondLastCommitSha1.setText("None");
        }
    }

    void createCommitNode(Graph commitTree) {
        final Model model = commitTree.getModel();
        commitTree.beginUpdate();

        Map<String, ICell> commitNodes = new HashMap<>();
        Map<String, ICell> commitSha1s = new HashMap<>();
        int index1 = 0;
        int index2 = 0;

        for(Commit.CommitData curr : sortedCommits){
            ICell commitNode = new CommitNode(curr);
            model.addCell(commitNode);
            commitNodes.put(curr.getCommitSha1(), model.getAddedCells().get(index1++));
            if(!curr.getCommitsLast2Commit().equals("")) {
                commitSha1s.put(curr.getCommitsLast2Commit(), model.getAddedCells().get(index2++));
            }
        }

        int sizeOfModelNodes = model.getAddedCells().size();

        ICell[] modelCellsArray = new ICell[sizeOfModelNodes];

        for(int i = 0; i < sizeOfModelNodes; i++){
            modelCellsArray[i] = model.getAddedCells().get(i);
        }

        for(int i = sizeOfModelNodes - 1; i > 0;) {
            final Edge newEdge = new Edge(modelCellsArray[i], modelCellsArray[--i]);
            model.addEdge(newEdge);
        }

        for(String curr : commitSha1s.keySet()) {
            if(commitNodes.containsKey(curr)) {
                final Edge newEdge = new Edge(commitNodes.get(curr), commitSha1s.get(curr));
                model.addEdge(newEdge);
            }
        }

        commitTree.endUpdate();

        commitTree.layout(new CommitTreeLayout());
    }

    public int getCommitNodeRadius() {
        return (int)commitNode.getRadius();
    }

}
