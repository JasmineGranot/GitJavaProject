package MagitMain;

import Controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;

public class JavaFXUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/Resources/MainScreen.fxml");
        fxmlLoader.setLocation(url);
        BorderPane head = fxmlLoader.load(url.openStream());
        MainController headController = fxmlLoader.getController();

        Scene scene = new Scene(head, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
