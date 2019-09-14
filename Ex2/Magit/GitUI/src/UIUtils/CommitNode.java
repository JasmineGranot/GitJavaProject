package UIUtils;

import GitObjects.Commit;
import com.fxgraph.cells.AbstractCell;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.IEdge;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import Controllers.CommitNodeController;
import java.io.IOException;
import java.net.URL;


public class CommitNode extends AbstractCell {
    private Commit.CommitData commitData;
    private CommitNodeController commitNodeController;


    public CommitNode(Commit.CommitData commitData){
        setCommitData(commitData);
    }

    private void setCommitData(Commit.CommitData commitData) {
        this.commitData = commitData;
    }

    public Commit.CommitData getCommitData() {
        return commitData;
    }

    @Override
    public Region getGraphic(Graph graph) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("/Resources/CommitNode.fxml");
            fxmlLoader.setLocation(url);
            GridPane root = fxmlLoader.load(url.openStream());
            commitNodeController = fxmlLoader.getController();
            commitNodeController.setCommitMessage(commitData.getCommitMsg());
            commitNodeController.setCommitDate(commitData.getCommitDate());
            commitNodeController.setCommitWriter(commitData.getCommitWriter());
            commitNodeController.setLastCommitSha1(commitData.getCommitsLastCommit());
            commitNodeController.setRootSha1(commitData.getCommitSha1());
            commitNodeController.setIsBranch(commitData.getBranchName());

            return root;


        } catch (IOException e) {
            return new Label("Error when tried to create graphic node !");
        }
    }

    @Override
    public DoubleBinding getXAnchor(Graph graph, IEdge edge) {
        final Region graphic = graph.getGraphic(this);
        return graphic.layoutXProperty().add(commitNodeController.getCommitNodeRadius());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommitNode that = (CommitNode) o;

        return this.commitData.getCommitDate() != null ?
                this.commitData.getCommitDate().equals(that.commitData.getCommitDate()) :
                that.commitData.getCommitDate() == null;
    }

    @Override
    public int hashCode() {
        return this.commitData.getCommitDate() != null ? this.commitData.getCommitDate().hashCode() : 0;
    }

}

