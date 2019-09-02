package Controllers;

import GitObjects.Commit;
import Layout.CommitTreeLayout;
import com.fxgraph.edges.Edge;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.graph.Model;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import java.util.List;
import UIUtils.CommitNode;

public class CommitNodeController {

    @FXML private Circle commitNode;
    @FXML private Label commitMsg;
    private Graph commitTree;
    private Pane textPane;
    @FXML private Button showMoreDataButton;
    private List<Commit.CommitData> sortedCommits;

    void setSortedCommits(List<Commit.CommitData> sortedCommits) {
        this.sortedCommits = sortedCommits;
    }

    Button getButton() {
        return showMoreDataButton;
    }
    void setTextPane(Pane textPane) {
        this.textPane = textPane;
    }

    @FXML
    void showCommitData(ICell commitData) {


    }

    private String commitDataToString(Commit.CommitData commitData) {
        String res, date, msg, writer, rootSha1, lastCommit;
        String DELIMITER = ", ";

        date = commitData.getCommitDate();
        msg = commitData.getCommitMsg();
        writer = commitData.getCommitWriter();
        rootSha1 = commitData.getCommitsRootSha1();
        lastCommit = commitData.getCommitsLastCommit();
        res = date.concat(msg).concat(DELIMITER).concat(writer).
                concat(DELIMITER).concat(rootSha1).concat(DELIMITER).concat(lastCommit);

        return res;
    }

    public void setCommitMessage(String commitMessage) {
        commitMsg.setText(commitMessage);
        commitMsg.setTooltip(new Tooltip(commitMessage));
    }

    private void setCommitTree(Graph commitTree) {
        this.commitTree = commitTree;
    }

    void createCommitNode(Graph commitTree) {
        final Model model = commitTree.getModel();
        commitTree.beginUpdate();

        int index = 0;
        for(Commit.CommitData curr : sortedCommits){
            ICell commitNode = new CommitNode(curr);
            model.addCell(commitNode);
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

        commitTree.endUpdate();
        commitTree.layout(new CommitTreeLayout());
        setCommitTree(commitTree);
    }

    public int getCommitNodeRadius() {
        return (int)commitNode.getRadius();
    }



}
