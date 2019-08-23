package Controllers;

import Engine.Magit;
import Exceptions.DataAlreadyExistsException;
import Exceptions.InvalidDataException;
import Utils.MagitStringResultObject;
import Utils.WorkingCopyChanges;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import java.io.File;
import java.net.URL;
import java.nio.file.DirectoryNotEmptyException;
import java.util.Optional;
import java.util.Set;
import UIUtils.CommonUsed;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

public class MainController {

    @FXML private Label userName;
    @FXML private Button changeUserButton;
    @FXML private Label currentBranch;
    @FXML private Button newBranchButton;
    @FXML private Button resetBranchButton;
    @FXML private Button deleteBranchButton;
    @FXML private Button changeRepoButton;
    @FXML private Button loadRepoButton;
    @FXML private Button createNewRepoButton;
    @FXML private Label currentRepo;
    @FXML private Button showStatusButton;
    @FXML private Button commitButton;
    @FXML private Button pushButton;
    @FXML private Button pullButton;
    @FXML private Button mergeButton;
    @FXML private Button cloneButton;
    @FXML private Button fetchButton;
    @FXML private ComboBox<String> branchesOptionsComboBox;
    @FXML private Button checkoutButton;
    @FXML private Pane textPane;

    private Magit myMagit = new Magit();
    private Stage primaryStage;


    @FXML
    void updateMagitUser(ActionEvent event) {
        Optional<String> res = CommonUsed.showDialog("Change user name", "Enter your name:",
                "Name:");
        res.ifPresent(name-> myMagit.setUserName(name));
    }

    @FXML
    void createNewRepository(ActionEvent event) {
        Optional<String> xmlRepoPath = CommonUsed.showDialog("New Repository", "Enter path:",
                "Path:");
        xmlRepoPath.ifPresent(path-> {
                myMagit.createNewRepo(path, "just a custom repo");

        });
        setRepoActionsAvailable();
        currentBranch.textProperty().unbind();
        currentBranch.textProperty().bind(myMagit.getCurrentBranch());
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
        currentBranch.textProperty().unbind();
        currentBranch.textProperty().bind(myMagit.getCurrentBranch());

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
        currentBranch.textProperty().unbind();
        currentBranch.textProperty().bind(myMagit.getCurrentBranch());

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

    //TODO: Handel exceptions
    @FXML
    void checkoutBranch(ActionEvent event) {
        try {
            myMagit.checkoutBranch(branchesOptionsComboBox.getValue(), false);
            currentBranch.textProperty().unbind();
            currentBranch.textProperty().bind(myMagit.getCurrentBranch());
        } catch (InvalidDataException e) {
            e.printStackTrace();
        } catch (DirectoryNotEmptyException e) {
            e.getCause();
        }
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
        Optional<String> commitMessage = CommonUsed.showDialog("New Commit", "Enter the message of the commit:",
                "Message:");

        commitMessage.ifPresent(msg -> {
            MagitStringResultObject result = myMagit.createNewCommit(msg);
            if (result.getIsHasError()){
                System.out.println(result.getErrorMSG());
                System.out.println();
            }
            else {
                System.out.println(result.getData());
                System.out.println();
            }
        });
    }

    @FXML
    void showWCStatus(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/Resources/ShowStatusScreen.fxml");
        fxmlLoader.setLocation(url);
        try {
            VBox statusScreen = fxmlLoader.load(url.openStream());
            //ShowStatusController statusController = fxmlLoader.getController();
            WorkingCopyChanges result =  myMagit.showStatus();
            Set<String> newFiles = result.getNewFiles();
            Set<String> changedFiles = result.getChangedFiles();
            Set<String> deletedFiles = result.getDeletedFiles();
            //statusController.setFilesLists(newFiles, changedFiles, deletedFiles);
            ObservableList<String> newItems = FXCollections.observableArrayList(newFiles);
            ListView<String> newItemsList = new ListView<>(newItems);
            newItemsList.setPrefHeight(92);
            ObservableList<String> changedItems = FXCollections.observableArrayList(changedFiles);
            ListView<String> changedItemsList = new ListView<>(changedItems);
            changedItemsList.setPrefHeight(92);
            ObservableList<String> deletedItems = FXCollections.observableArrayList(deletedFiles);
            ListView<String> deletedItemsList = new ListView<>(deletedItems);
            deletedItemsList.setPrefHeight(92);
            ScrollBar scroller = new ScrollBar();
            scroller.setOrientation(Orientation.VERTICAL);
            statusScreen.getChildren().add(1, newItemsList);
            statusScreen.getChildren().add(3, changedItemsList);
            statusScreen.getChildren().add(5, deletedItemsList);
            textPane.getChildren().add(statusScreen);

        }
        catch(Exception e){
            e.getMessage();
        }
    }

    @FXML
    void fetch(ActionEvent event) {

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
    void clone(ActionEvent event) {

    }

    public void initialize() {
        userName.textProperty().bind(myMagit.getUserName());
        currentRepo.textProperty().bind(myMagit.getRepoName());
        currentBranch.textProperty().bind(myMagit.getCurrentBranch());

        newBranchButton.setDisable(true);
        resetBranchButton.setDisable(true);
        commitButton.setDisable(true);
        showStatusButton.setDisable(true);
        pushButton.setDisable(true);
        pullButton.setDisable(true);
        mergeButton.setDisable(true);
        cloneButton.setDisable(true);
        fetchButton.setDisable(true);
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
        commitButton.setDisable(false);

        branchesOptionsComboBox.setItems(myMagit.getCurrentBranchesNames());
    }
}
