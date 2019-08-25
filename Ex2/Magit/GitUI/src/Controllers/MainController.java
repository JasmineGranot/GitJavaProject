package Controllers;

import Engine.Magit;
import Exceptions.DataAlreadyExistsException;
import Exceptions.InvalidDataException;
import GitObjects.Commit;
import Utils.MagitStringResultObject;
import Utils.WorkingCopyChanges;
import com.sun.deploy.uitoolkit.impl.text.TextWindow;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import java.io.File;
import java.net.URL;
import java.nio.file.DirectoryNotEmptyException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import UIUtils.CommonUsed;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import sun.plugin2.message.TextEventMessage;

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
    private ShowStatusController statusController = new ShowStatusController();


    @FXML
    void updateMagitUser(ActionEvent event) {
        Optional<String> res = CommonUsed.showDialog("Change user name", "Enter your name:",
                "Name:");
        res.ifPresent(name-> myMagit.setUserName(name));
        List<Commit.CommitData> sortedCommits = myMagit.getCurrentCommits();

    }

    @FXML
    void createNewRepository(ActionEvent event) {
        Optional<String> repoPath = CommonUsed.showDialog("New Repository", "Enter path:",
                "Path:");
        repoPath.ifPresent(path-> {
            MagitStringResultObject res = myMagit.createNewRepo(path, "just a custom repo");
            if (!res.getIsHasError()) {
                CommonUsed.showSuccess(res.getData());
                setRepoActionsAvailable();
                currentBranch.textProperty().unbind();
                currentBranch.textProperty().bind(myMagit.getCurrentBranch());
            }
            else {
                CommonUsed.showError(res.getErrorMSG());
            }
        });
    }

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

        try {
            MagitStringResultObject res =
                    myMagit.loadRepositoryFromXML(selectFile.getAbsolutePath(), false);
            if (!res.getIsHasError()){
                CommonUsed.showSuccess(res.getData());
                setRepoActionsAvailable();
                currentBranch.textProperty().unbind();
                currentBranch.textProperty().bind(myMagit.getCurrentBranch());
            }
            else {
                CommonUsed.showError(res.getErrorMSG());
            }
        }
        catch(DataAlreadyExistsException e){
            CommonUsed.showError(e.getMessage());
        }
    }

    @FXML
    void switchRepository(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select repository!");

        File selectFile = directoryChooser.showDialog(primaryStage);
        if (selectFile == null) {
            return;
        }
        MagitStringResultObject res = myMagit.changeRepository(selectFile.getAbsolutePath());
        if (!res.getIsHasError()) {
            CommonUsed.showSuccess(res.getData());
            setRepoActionsAvailable();
            currentBranch.textProperty().unbind();
            currentBranch.textProperty().bind(myMagit.getCurrentBranch());
        }
        else {
            CommonUsed.showError(res.getErrorMSG());
        }

    }

    @FXML
    void deleteBranch(ActionEvent event) {
        try {
            MagitStringResultObject res = myMagit.deleteBranch(branchesOptionsComboBox.getValue());
            if (!res.getIsHasError()) {
                CommonUsed.showSuccess(res.getData());
            }
            else {
                CommonUsed.showError(res.getErrorMSG());
            }
        } catch (InvalidDataException e) {
            CommonUsed.showError(e.getMessage());
        }
    }

    @FXML
    void resetBranchToSpecificCommit(ActionEvent event) {
        Optional<String> newCommitSha1 = CommonUsed.showDialog("Reset Head", "Enter commit SHA-1:",
                "SHA-1:");

        resetBranchToASpecificCommit(newCommitSha1, false);
    }

    private void resetBranchToASpecificCommit(Optional<String> newCommitSha1, boolean toIgnoreChanges) {
        newCommitSha1.ifPresent(sha1-> {
            try {
                MagitStringResultObject res = myMagit.resetBranch(sha1, toIgnoreChanges);
                if (!res.getIsHasError()) {
                    CommonUsed.showSuccess(res.getData());
                }
                else {
                    CommonUsed.showError(res.getErrorMSG());
                }
            } catch (DirectoryNotEmptyException e) {
                boolean toContinue = CommonUsed.showDilemma(e.getMessage());
                if(toContinue) {
                    resetBranchToASpecificCommit(newCommitSha1, true);
                }
                else {
                    String errorMsg = "To reset commit first!";
                    CommonUsed.showSuccess(errorMsg);
                }
            }

        });
    }

    @FXML
    void checkoutBranch(ActionEvent event) {
        checkoutABranch(false);
    }

    private void checkoutABranch(boolean toIgnoreChanges) {
        try {
            MagitStringResultObject res = myMagit.checkoutBranch(branchesOptionsComboBox.getValue(), toIgnoreChanges);
            if (!res.getIsHasError()) {
                CommonUsed.showSuccess(res.getData());
                currentBranch.textProperty().unbind();
                currentBranch.textProperty().bind(myMagit.getCurrentBranch());
            }
            else {
                CommonUsed.showError(res.getErrorMSG());
            }
        } catch (DirectoryNotEmptyException e) {
            boolean toContinue = CommonUsed.showDilemma(e.getMessage());
            if(toContinue){
                checkoutABranch(true);
            }
            else {
                String errorMsg = "To checkout commit first!";
                CommonUsed.showSuccess(errorMsg);
            }
        }
    }

    @FXML
    void createNewBranch(ActionEvent event) {
        Optional<String> newBranchName = CommonUsed.showDialog("New Branch", "Enter branch's name:",
                "Name:");
        newBranchName.ifPresent(name-> {
            try {
                MagitStringResultObject res = myMagit.addNewBranch(name);
                if (!res.getIsHasError()){
                    CommonUsed.showSuccess(res.getData());
                }
                else {
                    CommonUsed.showError(res.getErrorMSG());
                }

            } catch (InvalidDataException e) {
                CommonUsed.showError(e.getMessage());
                createNewBranch(event);
            }
        });
    }

    @FXML
    void createNewCommit(ActionEvent event) {
        Optional<String> commitMessage = CommonUsed.showDialog("New Commit", "Enter the message of the commit:",
                "Message:");

        commitMessage.ifPresent(msg -> {
            MagitStringResultObject result = myMagit.createNewCommit(msg);
            if (!result.getIsHasError()){
                CommonUsed.showSuccess(result.getData());
            }
            else {
                CommonUsed.showError(result.getErrorMSG());
            }
        });
    }

    @FXML
    void showWCStatus(ActionEvent event) {
        WorkingCopyChanges result =  myMagit.showStatus();
        if(!result.getHasErrors()){
            Set<String> newFiles = result.getNewFiles();
            Set<String> changedFiles = result.getChangedFiles();
            Set<String> deletedFiles = result.getDeletedFiles();

            statusController.showWcStatus(newFiles, changedFiles, deletedFiles, textPane);
        }
        else {
            CommonUsed.showError(result.getErrorMsg());
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
