package Controllers;

import Engine.Magit;
import Exceptions.InvalidDataException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.nio.file.DirectoryNotEmptyException;
import java.util.Optional;
import UIUtils.CommonUsed;

public class MainController {

    @FXML private Label userName;

    @FXML private Button changeUserButton;

    @FXML private Label CurrentBranch;

    @FXML private Button newBranchButton;

    @FXML private Button resetBranchButton;

    @FXML private Button ChangeRepoButton;

    @FXML private Button LoadRepoButton;

    @FXML private Button CreateNewRepoButton;

    @FXML private Label CurrentRepo;

    @FXML private Button showStatusButton;

    @FXML private Button CommitButton;

    @FXML private Button PushButton;

    @FXML private Button PullButton;

    @FXML private Button MergeButton;

    @FXML private Button CloneButton;

    @FXML private Button FetchButton;

    @FXML private ComboBox<?> branchesOptionsComboBox;

    @FXML private Button checkoutButton;

    @FXML private Pane InfoBox;

    private Magit myMagit = new Magit();
    private StringProperty myUserName = new SimpleStringProperty();

    public MainController() {
        //myUserName.setValue(myMagit.getUserName());
    }

    @FXML
    void checkoutBranch(ActionEvent event) {
        Optional<String> res = CommonUsed.showDialog("Checkout branch", "Enter branch name:",
                "Name:");

        res.ifPresent(name-> {
            try {
                myMagit.checkoutBranch(name, false);
            } catch (InvalidDataException e) {
                e.printStackTrace();
            } catch (DirectoryNotEmptyException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    void clone(ActionEvent event) {

    }

    @FXML
    void createNewBranch(ActionEvent event) {

    }

    @FXML
    void createNewCommit(ActionEvent event) {

    }

    @FXML
    void createNewRepository(ActionEvent event) {

    }

    @FXML
    void deleteBranch(ActionEvent event) {

    }

    @FXML
    void fetch(ActionEvent event) {

    }

    @FXML
    void loadRepository(ActionEvent event) {

    }

    @FXML
    void merge(ActionEvent event) {

    }

    @FXML
    void pull(ActionEvent event) {

    }

    @FXML
    void push(ActionEvent event) {

    }

    @FXML
    void resetBranchToSpecificCommit(ActionEvent event) {

    }

    @FXML
    void showBranches(ActionEvent event) {

    }

    @FXML
    void showWCStatus(ActionEvent event) {

    }

    @FXML
    void switchRepository(ActionEvent event) {

    }

    @FXML
    void updateMagitUser(ActionEvent event) {
        Optional<String> res = CommonUsed.showDialog("Change user name", "Enter your name:",
                "Name:");
        res.ifPresent(name-> myMagit.setUserName(name));
    }



    public void initialize() {
        userName.textProperty().bind(myMagit.getUserName());
        CurrentBranch.textProperty().bind(myMagit.getCurrentBranch());
    }

}
