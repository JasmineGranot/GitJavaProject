package Layout;

import GitObjects.Commit;
import UIUtils.CommitNode;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.layout.Layout;
import java.util.List;

// simple test for scattering commits in imaginary tree, where every 3rd node is in a new 'branch' (moved to the right)
public class CommitTreeLayout implements Layout {

    @Override
    public void execute(Graph graph) {
        final List<ICell> cells = graph.getModel().getAllCells();
        int startX = 15;
        int startY = 20;
        int every3rdNode = 1;
        for (ICell cell : cells) {
            CommitNode c = (CommitNode) cell;
            if (every3rdNode % 3 == 0) {
                graph.getGraphic(c).relocate(startX + 50, startY);
            } else {
                graph.getGraphic(c).relocate(startX, startY);
            }

            if(!c.getCommitData().getCommitsLast2Commit().equals("")) {
                graph.getGraphic(c).relocate(startX - 20, startY);
            }
            startY += 50;
            every3rdNode++;
        }
    }
}

