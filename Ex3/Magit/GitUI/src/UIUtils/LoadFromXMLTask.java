package UIUtils;

import Engine.Magit;
import Exceptions.DataAlreadyExistsException;
import Utils.MagitStringResultObject;
import Utils.MagitStringResultObject;
import javafx.concurrent.Task;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import java.io.File;

public class LoadFromXMLTask extends Task<MagitStringResultObject> {

    private boolean isShowStatusOpen;
    private Magit myMagit;
    private File selectFile;
    private Text currentBranch;
    private Text pathRepo;
    private Pane showStatusPane;

    public LoadFromXMLTask(boolean isShowStatusOpen, Magit myMagit, File selectFile,
                           Text currentBranch, Text pathRepo, Pane showStatusPane) {
        this.isShowStatusOpen = isShowStatusOpen;
        this.myMagit = myMagit;
        this.selectFile = selectFile;
        this.currentBranch = currentBranch;
        this.pathRepo = pathRepo;
        this.showStatusPane = showStatusPane;
    }

    @Override
    protected MagitStringResultObject call() throws Exception {
        try {
            if(isShowStatusOpen){
                showStatusPane.getChildren().clear();
            }

            return myMagit.loadRepositoryFromXML(selectFile.getAbsolutePath(), false);
        }
        catch(DataAlreadyExistsException e){
            CommonUsed.showError(e.getMessage());
        }
        return null;
    }

}
