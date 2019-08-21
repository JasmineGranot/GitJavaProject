package Controllers;

import Engine.Magit;
import Exceptions.DataAlreadyExistsException;
import Exceptions.InvalidDataException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Paths;
import java.util.Optional;
import UIUtils.CommonUsed;

public class MainController {

    @FXML private Label userName;

    @FXML private Button changeUserButton;

    @FXML private Label CurrentBranch;

    @FXML private Button newBranchButton;

    @FXML private Button resetBranchButton;

    @FXML private Button deleteBranchButton;

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
//    private StringProperty myUserName = new SimpleStringProperty();

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
        Optional<String> newBranchName = CommonUsed.showDialog("New Branch", "Enter name:",
                "Name:");
        newBranchName.ifPresent(name-> {
            try {
                myMagit.addNewBranch(name);
            } catch (InvalidDataException e) {
                e.printStackTrace();
            }

        });
    }

    @FXML
    void createNewCommit(ActionEvent event) {

    }

    @FXML
    void createNewRepository(ActionEvent event) {
        Optional<String> xmlRepoPath = CommonUsed.showDialog("New Repository", "Enter path:",
                "Path:");
        xmlRepoPath.ifPresent(path-> {
                myMagit.createNewRepo(path, "just a custom repo");

        });
        setRepoActionsAvailable();
    }

    @FXML
    void deleteBranch(ActionEvent event) {


    }

    @FXML
    void fetch(ActionEvent event) {

    }

    @FXML
    void loadRepository(ActionEvent event) { //TODO: change to file chooser with XML Filte
        Optional<String> xmlRepoPath = CommonUsed.showDialog("Load Repository", "Enter xml path:",
                "Path:");
        xmlRepoPath.ifPresent(name-> {
            try {
                myMagit.loadRepositoryFromXML(name, false);
            } catch (DataAlreadyExistsException e) {
                e.printStackTrace();
            }
        });
        setRepoActionsAvailable();

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
        Optional<String> newCommitSha1 = CommonUsed.showDialog("Reset Head", "Enter commit SHA-1:",
                "SHA-1:");
        newCommitSha1.ifPresent(sha1-> {
            try {
                myMagit.resetBranch(sha1, false);
            } catch (DirectoryNotEmptyException e) {
                e.printStackTrace();
            }

        });
    }

    @FXML
    void showBranches(ActionEvent event) {

    }

    @FXML
    void showWCStatus(ActionEvent event) {

    }

    @FXML
    void switchRepository(ActionEvent event) {
        Optional<String> RepoPath = CommonUsed.showDialog("Change Repository", "Enter Repository path:",
                "Path:");
        RepoPath.ifPresent(name-> myMagit.changeRepository(name)
        );
        setRepoActionsAvailable();
    }

    @FXML
    void updateMagitUser(ActionEvent event) {
        Optional<String> res = CommonUsed.showDialog("Change user name", "Enter your name:",
                "Name:");
        res.ifPresent(name-> myMagit.setUserName(name));
    }



    public void initialize() {
        userName.textProperty().bind(myMagit.getUserName());
        CurrentRepo.textProperty().bind(myMagit.getRepoName());
        CurrentBranch.textProperty().bind(myMagit.getCurrentBranch()); // NOT WORKING


        newBranchButton.setDisable(true);
        resetBranchButton.setDisable(true);
        CommitButton.setDisable(true);
        showStatusButton.setDisable(true); // NOT WORKING
        PushButton.setDisable(true);
        PullButton.setDisable(true);
        MergeButton.setDisable(true);
        CloneButton.setDisable(true);
        FetchButton.setDisable(true);
        branchesOptionsComboBox.setDisable(true);
        checkoutButton.setDisable(true);
        deleteBranchButton.setDisable(true);

    }

    private void setRepoActionsAvailable(){
        showStatusButton.setDisable(false);
        branchesOptionsComboBox.setDisable(false);
        newBranchButton.setDisable(false);    }

}
