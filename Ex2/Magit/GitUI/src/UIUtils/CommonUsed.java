package UIUtils;

import com.sun.deploy.uitoolkit.impl.text.TextWindow;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import sun.plugin2.message.TextEventMessage;

import javax.security.auth.callback.TextOutputCallback;
import java.awt.*;
import java.util.Optional;

public class CommonUsed {
    public static Optional<Pair<String, String>> showMultipleChoiceDialog(String title, String headerText, String contentText1,
                                                            String contentText2){
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField branchName = new TextField();
        branchName.setPromptText(contentText1);
        TextField sha1 = new TextField();
        sha1.setPromptText(contentText2);

        grid.add(new Label(contentText1), 0, 0);
        grid.add(branchName, 1, 0);
        grid.add(new Label(contentText2), 0, 1);
        grid.add(sha1, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(branchName::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return new Pair<>(branchName.getText(), sha1.getText());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static Optional<String> showDialog(String title, String headerText, String contentText){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);

        return dialog.showAndWait();
    }

    public static void showError(String headerText){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Error!");
        alert.setContentText(headerText);

        alert.showAndWait();
    }

    public static void showSuccess(String successMsg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(successMsg);

        alert.showAndWait();
    }

    public static boolean showDilemma(String dilemmaMsg)
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(dilemmaMsg);
        alert.setContentText("Would you like to continue without committing the open changes?");

        ButtonType buttonTypeOne = new ButtonType("Continue");
        ButtonType buttonTypeTwo = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == buttonTypeOne;
    }


}
