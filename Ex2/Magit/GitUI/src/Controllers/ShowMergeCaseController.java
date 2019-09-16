package Controllers;

import UIUtils.CommonUsed;
import Utils.MergeResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class ShowMergeCaseController {

    @FXML private Button resolveConflictsButton;
    @FXML private ListView<String> listView;
    private List<MergeResult> mergeResultList;
    private Stage stage;
    private MainController mainController;

    void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    void setStage(Stage stage) {
        this.stage = stage;
    }

    void setMergeResultList(List<MergeResult> mergeResultList) {
        this.mergeResultList = mergeResultList;
    }

    void showMergeCase(List<String> filesStatus) {
        ObservableList<String> observableList = FXCollections.observableArrayList(filesStatus);
        listView.setItems(observableList);
        listView.setVisible(true);
    }

    @FXML
    void showResolveWindow(ActionEvent event) {
        try {
            for (MergeResult curr : mergeResultList) {
                if (curr.getHasConflicts()) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    URL url = getClass().getResource("/Resources/ConflictResolver.fxml");
                    fxmlLoader.setLocation(url);
                    VBox head = fxmlLoader.load(url.openStream());

                    MergeNodeController mergeNodeController = fxmlLoader.getController();

                    Scene scene = new Scene(head, 600, 400);
//                  scene.getStylesheets().add(getClass().getResource("/Css/Style1.css").toExternalForm());

                    Stage newStage = new Stage();
                    newStage.setScene(scene);
                    newStage.show();

                    mergeNodeController.setStage(newStage);
                    mergeNodeController.setFilePath(curr.getFilePath());
                    mergeNodeController.setMainController(mainController);
                    mergeNodeController.resolveConflicts(curr);
                }
            }
            stage.close();
        } catch (IOException e) {
            CommonUsed.showError(e.getMessage());
        }
    }
}
