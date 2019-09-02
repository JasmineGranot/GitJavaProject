package Controllers;

import UIUtils.CommonUsed;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import java.io.IOException;
import java.net.URL;
import java.util.Set;


public class ShowStatusController {

    @FXML private Accordion accodionPane;

    void showWcStatus(Set<String> newFiles, Set<String> changedFiles,
                      Set<String> deletedFiles, Pane textPane) {
        TitledPane newFilesTitledPane = new TitledPane();
        ListView<String> newFilsListView = new ListView<>();
        TitledPane changedFilesTitledPane = new TitledPane();
        ListView<String> changedFilesListView = new ListView<>();
        TitledPane deletedFilesTitledPane = new TitledPane();
        ListView<String> deletedFilesListView = new ListView<>();


        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/Resources/ShowStatusScreen.fxml");
        fxmlLoader.setLocation(url);

        try {
            accodionPane =  fxmlLoader.load(url.openStream());

            ObservableList<String> newItems = FXCollections.observableArrayList(newFiles);
            newFilsListView.setItems(newItems);

            ObservableList<String> changedItems = FXCollections.observableArrayList(changedFiles);
            changedFilesListView.setItems(changedItems);

            ObservableList<String> deletedItems = FXCollections.observableArrayList(deletedFiles);
            deletedFilesListView.setItems(deletedItems);

            newFilesTitledPane.setContent(newFilsListView);
            newFilesTitledPane.setText("New Files:");
            newFilesTitledPane.setStyle("-fx-text-fill: #052f59; -fx-font-weight: bold;");

            changedFilesTitledPane.setContent(changedFilesListView);
            changedFilesTitledPane.setText("Changed Files:");
            changedFilesTitledPane.setStyle("-fx-text-fill: #052f59; -fx-font-weight: bold;");

            deletedFilesTitledPane.setContent(deletedFilesListView);
            deletedFilesTitledPane.setText("Deleted Files:");
            deletedFilesTitledPane.setStyle("-fx-text-fill: #052f59; -fx-font-weight: bold;");


            accodionPane.getPanes().addAll(newFilesTitledPane, changedFilesTitledPane, deletedFilesTitledPane);

            textPane.getChildren().add(accodionPane);
            accodionPane.prefWidthProperty().bind(textPane.widthProperty());
            accodionPane.prefHeightProperty().bind(textPane.heightProperty());

        } catch(IOException e) {
            CommonUsed.showError(e.getMessage());
        }
    }

}
