package UIUtils;

import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class CommonUsed {
    public static Optional<String> showDialog(String title, String headerText, String contentText){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);

        return dialog.showAndWait();
    }


}
