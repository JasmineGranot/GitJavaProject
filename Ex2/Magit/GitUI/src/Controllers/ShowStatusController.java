package Controllers;

import Utils.WorkingCopyChanges;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.Set;


public class ShowStatusController {

//    @FXML
//    private VBox statusScreen;

    @FXML
    private Accordion accodionPane;

    @FXML
    private TitledPane newFilesTitledPane = new TitledPane();

    @FXML
    private ListView<String> newFilsListView;

    @FXML
    private TitledPane changedFilesTitledPane = new TitledPane();

    @FXML
    private ListView<String> changedFilesListView;

    @FXML
    private TitledPane deletedFilesTitledPane = new TitledPane();

    @FXML
    private ListView<String> deletedFilesListView;

    void showWcStatus(Set<String> newFiles, Set<String> changedFiles,
                      Set<String> deletedFiles, Pane textPane) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/Resources/ShowStatusScreen.fxml");
        fxmlLoader.setLocation(url);

        try {
            accodionPane =  fxmlLoader.load(url.openStream());

            ObservableList<String> newItems = FXCollections.observableArrayList(newFiles);
            newFilsListView = new ListView<>();
            newFilsListView.setItems(newItems);

            ObservableList<String> changedItems = FXCollections.observableArrayList(changedFiles);
            changedFilesListView = new ListView<>();
            changedFilesListView.setItems(changedItems);

            ObservableList<String> deletedItems = FXCollections.observableArrayList(deletedFiles);
            deletedFilesListView = new ListView<>();
            deletedFilesListView.setItems(deletedItems);

            newFilesTitledPane.setContent(newFilsListView);
            changedFilesTitledPane.setContent(changedFilesListView);
            deletedFilesTitledPane.setContent(deletedFilesListView);

            accodionPane.getPanes().get(0).setContent(newFilesTitledPane);
            accodionPane.getPanes().get(1).setContent(changedFilesTitledPane);
            accodionPane.getPanes().get(2).setContent(deletedFilesTitledPane);

            textPane.getChildren().add(accodionPane);
            accodionPane.maxHeightProperty().bind(textPane.maxHeightProperty());
            accodionPane.maxWidthProperty().bind(textPane.maxWidthProperty());

        } catch(Exception e) {
            e.getMessage();
        }
    }

}
