package Controllers;

import Engine.Magit;
import Exceptions.DataAlreadyExistsException;
import Exceptions.InvalidDataException;
import GitObjects.Branch;
import GitObjects.Folder;
import Utils.MagitStringResultObject;
import Utils.ResultList;
import XMLHandler.XMLValidator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;

import java.awt.*;
import java.io.File;
import java.nio.file.DirectoryNotEmptyException;
import java.util.List;
import java.util.Optional;
import UIUtils.CommonUsed;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javax.crypto.spec.PSource;

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
    @FXML private ComboBox<String> branchesOptionsComboBox;
    @FXML private Button checkoutButton;
    @FXML private Pane InfoBox;

    private Magit myMagit = new Magit();
    private Stage primaryStage;
    private SimpleBooleanProperty isBranchSelected = new SimpleBooleanProperty(false);


    //TODO: Handel exceptions
    @FXML
    void checkoutBranch(ActionEvent event) {
        try {
            myMagit.checkoutBranch(branchesOptionsComboBox.getValue(), false);
            CurrentBranch.textProperty().unbind();
            CurrentBranch.textProperty().bind(myMagit.getCurrentBranch());
        } catch (InvalidDataException e) {
            e.printStackTrace();
        } catch (DirectoryNotEmptyException e) {
            e.getCause();
        }
    }

    @FXML
    void clone(ActionEvent event) {

    }

    @FXML
    void createNewBranch(ActionEvent event) {
        Optional<String> newBranchName = CommonUsed.showDialog("New Branch", "Enter branch's name:",
                "Name:");
        newBranchName.ifPresent(name-> {
            try {
                myMagit.addNewBranch(name);
            } catch (InvalidDataException e) {
                CommonUsed.showError(e.getMessage(), "New Branch", "Enter branch's name:"
                , "Name: ");
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
        CurrentBranch.textProperty().unbind();
        CurrentBranch.textProperty().bind(myMagit.getCurrentBranch());
    }

    //TODO: Handel exceptions
    @FXML
    void deleteBranch(ActionEvent event) {
        try {
            myMagit.deleteBranch(branchesOptionsComboBox.getValue());
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void fetch(ActionEvent event) {

    }

    //TODO: change to file chooser with XML File
    @FXML
    void loadRepository(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML file!");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML file", "*.xml"));

        File selectFile = fileChooser.showOpenDialog(primaryStage);
        if (selectFile == null) {
            return;
        }

        /*ProgressBar progressBar = new ProgressBar(0);
        ProgressIndicator progressIndicator = new ProgressIndicator(0);

        progressBar.setVisible(true);
        Button startButton = new Button("Start");
        Button okButton = new Button("OK");
        okButton.setVisible(false);

        final Label statusLabel = new Label();
        statusLabel.setMinWidth(250);
        statusLabel.setTextFill(Color.BLACK);


        startButton.setOnAction(eventThread -> {
            try{
                startButton.setDisable(true);
                startButton.setVisible(false);
                progressBar.setProgress(0);
                progressIndicator.setProgress(0);
                myMagit.loadRepositoryFromXML(selectFile.getAbsolutePath(), false);
            }catch(DataAlreadyExistsException e){
                System.out.println(e.getMessage());
            }
        });*/
        try{
            myMagit.loadRepositoryFromXML(selectFile.getAbsolutePath(), false);
        }
        catch(DataAlreadyExistsException e){
            e.getMessage();
        }
        setRepoActionsAvailable();
        CurrentBranch.textProperty().unbind();
        CurrentBranch.textProperty().bind(myMagit.getCurrentBranch());

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
        MagitStringResultObject obj;
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select repository!");

        File selectFile = directoryChooser.showDialog(primaryStage);
        if (selectFile == null) {
            return;
        }
        obj = myMagit.changeRepository(selectFile.getAbsolutePath());
        setRepoActionsAvailable();
        CurrentBranch.textProperty().unbind();
        CurrentBranch.textProperty().bind(myMagit.getCurrentBranch());

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
        CurrentBranch.textProperty().bind(myMagit.getCurrentBranch());

        newBranchButton.setDisable(true);
        resetBranchButton.setDisable(true);
        CommitButton.setDisable(true);
        showStatusButton.setDisable(true);
        PushButton.setDisable(true);
        PullButton.setDisable(true);
        MergeButton.setDisable(true);
        CloneButton.setDisable(true);
        FetchButton.setDisable(true);
        branchesOptionsComboBox.setDisable(true);
        checkoutButton.setDisable(true);
        deleteBranchButton.setDisable(true);
        branchesOptionsComboBox.setOnAction(e -> {
            deleteBranchButton.setDisable(false);
            checkoutButton.setDisable(false);
        });
    }

    private void setRepoActionsAvailable(){
        showStatusButton.setDisable(false);
        branchesOptionsComboBox.setDisable(false);
        newBranchButton.setDisable(false);
        resetBranchButton.setDisable(false);

        branchesOptionsComboBox.setItems(myMagit.getCurrentBranchesNames());
    }
}
