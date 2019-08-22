package UIUtils;

import com.sun.deploy.uitoolkit.impl.text.TextWindow;
import javafx.scene.control.Alert;
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

    public static void showError(String headerText, String dialogTitle, String dialogHeaderText, String dialogContent){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Error!");
        alert.setContentText(headerText);

        alert.showAndWait();
    }


}
