package Controllers;

import GitObjects.Commit;
import Layout.CommitTreeLayout;
import com.fxgraph.edges.Edge;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.graph.Model;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import java.util.List;
import UIUtils.CommitNode;

public class CommitNodeController {

    @FXML private Pane commitNodePane;
    @FXML private Circle commitNode;
    @FXML private Label commitMsg;
    @FXML private Label commitDate;
    @FXML private Label commitWriter;

    private List<Commit.CommitData> sortedCommits;

    void setSortedCommits(List<Commit.CommitData> sortedCommits) {
        this.sortedCommits = sortedCommits;
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

    void createCommitNode(Graph commitTree) {
        final Model model = commitTree.getModel();
        commitTree.beginUpdate();

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
    }

    public int getCommitNodeRadius() {
        return (int)commitNode.getRadius();
    }

}
