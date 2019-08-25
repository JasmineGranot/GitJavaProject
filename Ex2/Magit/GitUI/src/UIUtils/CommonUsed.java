package UIUtils;

import com.sun.deploy.uitoolkit.impl.text.TextWindow;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import sun.plugin2.message.TextEventMessage;

import javax.security.auth.callback.TextOutputCallback;
import java.util.Optional;

public class CommonUsed {
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
