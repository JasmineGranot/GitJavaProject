package Controllers;

import Engine.Magit;
import UIUtils.CommonUsed;
import Utils.MagitUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

public class ShowCommitData {

    @FXML private Accordion accordionPane;
    private Magit myMagit;

    void setMyMagit(Magit myMagit) {
        this.myMagit = myMagit;
    }

    void showCommitData(List<String> commitData, Pane statusPane) {
        ListView<String> commitDataListView = new ListView<>();
        TitledPane commitDataTitledPane = new TitledPane();

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/Resources/ShowCommitData.fxml");
        fxmlLoader.setLocation(url);

        try {
            accordionPane = fxmlLoader.load(url.openStream());

            ObservableList<String> commitDataList = FXCollections.observableArrayList(commitData);
            commitDataListView.setItems(commitDataList);
            commitDataTitledPane.setContent(commitDataListView);
            commitDataTitledPane.setText("Commit's data:");
            commitDataTitledPane.setStyle("-fx-text-fill: #052f59;");

            accordionPane.getPanes().add(commitDataTitledPane);

            statusPane.getChildren().clear();
            statusPane.getChildren().add(accordionPane);
            accordionPane.prefWidthProperty().bind(statusPane.widthProperty());
            commitDataTitledPane.prefWidthProperty().bind(accordionPane.widthProperty());

            commitDataListView.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent click) {

                    if (click.getClickCount() == 2) {
                        String select = commitDataListView.getSelectionModel().getSelectedItem();
                        String[] selection = select.split(";");
                        String magitPath = Paths.get(myMagit.getPath().getValue(), ".magit").toString();
                        String objectsPath = Paths.get(magitPath, "Objects").toString();
                        String[] sha1 = selection[1].split(" ");
                        String filePath = Paths.get(objectsPath, sha1[1]).toString();
                        try {
                            String fileContent = MagitUtils.unZipAndReadFile(filePath);
                            CommonUsed.showSuccess(fileContent);
                        } catch (IOException e) {
                            CommonUsed.showError(e.getMessage());
                        }
                    }
                }
            });

        } catch (IOException e) {
            CommonUsed.showError(e.getMessage());
        }


    }
}
