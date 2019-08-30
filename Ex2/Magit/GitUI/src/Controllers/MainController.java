package Controllers;

import Engine.Magit;
import Exceptions.DataAlreadyExistsException;
import Exceptions.InvalidDataException;
import GitObjects.Commit;
import Utils.MagitStringResultObject;
import Utils.WorkingCopyChanges;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import java.io.File;
import java.nio.file.DirectoryNotEmptyException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import UIUtils.CommonUsed;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import com.fxgraph.graph.Graph;


public class MainController {

    @FXML private Text userName;
    @FXML private Button changeUserButton;
    @FXML private Text currentBranch;
    @FXML private Button newBranchButton;
    @FXML private Button resetBranchButton;
    @FXML private Button deleteBranchButton;
    @FXML private Button changeRepoButton;
    @FXML private Button loadRepoButton;
    @FXML private Button createNewRepoButton;
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
    @FXML private Text currentRepo;
    @FXML private Pane treeViewPane;
    @FXML private SplitPane mainTextWindow;
    @FXML private ScrollPane treeScrollPane;
    @FXML private AnchorPane treeAnchorPane;
    @FXML private HBox layoutHbox;
    @FXML private Button changeLayoutButton;
    @FXML private Text pathRepo;


    private Magit myMagit = new Magit();
    private Stage primaryStage;
    private ShowStatusController statusController = new ShowStatusController();
    private boolean isShowStatusOpen = false;


    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    @FXML
    void updateMagitUser() {
        Optional<String> res = CommonUsed.showDialog("Change user name", "Enter your name:",
                "Name:");
        res.ifPresent(name-> myMagit.setUserName(name));
    }

    @FXML
    void createNewRepository() {
        Optional<String> repoPath = CommonUsed.showDialog("New Repository", "Enter path:",
                "Path:");
        repoPath.ifPresent(path-> {
            MagitStringResultObject res = myMagit.createNewRepo(path, "just a custom repo");
            if(isShowStatusOpen){
                textPane.getChildren().clear();
                isShowStatusOpen = false;
            }
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
    void loadRepository() {
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
            if(isShowStatusOpen){
                textPane.getChildren().clear();
                isShowStatusOpen = false;
            }

            MagitStringResultObject res =
                    myMagit.loadRepositoryFromXML(selectFile.getAbsolutePath(), false);
            if (!res.getIsHasError()){
                CommonUsed.showSuccess(res.getData());
                setRepoActionsAvailable();
                currentBranch.textProperty().unbind();
                currentBranch.textProperty().bind(myMagit.getCurrentBranch());
                pathRepo.textProperty().bind(myMagit.getPath());
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
    void switchRepository() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select repository!");

        File selectFile = directoryChooser.showDialog(primaryStage);
        if (selectFile == null) {
            return;
        }

        if(isShowStatusOpen){
            textPane.getChildren().clear();
            isShowStatusOpen = false;
        }

        MagitStringResultObject res = myMagit.changeRepository(selectFile.getAbsolutePath());
        if (!res.getIsHasError()) {
            CommonUsed.showSuccess(res.getData());
            setRepoActionsAvailable();
            currentBranch.textProperty().unbind();
            currentBranch.textProperty().bind(myMagit.getCurrentBranch());
            pathRepo.textProperty().bind(myMagit.getPath());
        }
        else {
            CommonUsed.showError(res.getErrorMSG());
        }

    }

    @FXML
    void deleteBranch() {
        try {
            if(isShowStatusOpen){
                textPane.getChildren().clear();
                isShowStatusOpen = false;
            }

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
    void resetBranchToSpecificCommit() {
        Optional<String> newCommitSha1 = CommonUsed.showDialog("Reset Head", "Enter commit SHA-1:",
                "SHA-1:");

        if(isShowStatusOpen){
            textPane.getChildren().clear();
            isShowStatusOpen = false;
        }

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
    void checkoutBranch() {
        if(isShowStatusOpen){
            textPane.getChildren().clear();
            isShowStatusOpen = false;
        }

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
    void createNewBranch() {
        Optional<String> newBranchName = CommonUsed.showDialog("New Branch", "Enter branch's name:",
                "Name:");
        newBranchName.ifPresent(name-> {
            try {
                if(isShowStatusOpen){
                    textPane.getChildren().clear();
                    isShowStatusOpen = false;
                }

                MagitStringResultObject res = myMagit.addNewBranch(name);
                if (!res.getIsHasError()){
                    CommonUsed.showSuccess(res.getData());
                }
                else {
                    CommonUsed.showError(res.getErrorMSG());
                }

            } catch (InvalidDataException e) {
                CommonUsed.showError(e.getMessage());
                createNewBranch();
            }
        });
    }

    @FXML
    void createNewCommit() {
        Optional<String> commitMessage = CommonUsed.showDialog("New Commit", "Enter the message of the commit:",
                "Message:");

        commitMessage.ifPresent(msg -> {
            if(isShowStatusOpen){
                textPane.getChildren().clear();
                isShowStatusOpen = false;
            }

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
    void showWCStatus() {
        isShowStatusOpen = true;
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
    void fetch() {

    }

    @FXML
    void merge() {

    }

    @FXML
    void pull() {

    }

    @FXML
    void push() {

    }

    @FXML
    void clone(ActionEvent event) {

    }

    @FXML
    void showLayoutButtons(ActionEvent event) {
        layoutHbox.setVisible(true);
    }

    @FXML
    void changeStyleToBlue(ActionEvent event) {
        primaryStage.getScene().getStylesheets().clear();
        primaryStage.getScene().getStylesheets().add(getClass().getResource("/Css/Style1.css").toExternalForm());
    }

    @FXML
    void changeStyleToGreen(ActionEvent event) {
        primaryStage.getScene().getStylesheets().clear();
        primaryStage.getScene().getStylesheets().add(getClass().getResource("/Css/Style 2.css").toExternalForm());
    }

    @FXML
    void changeStyleToPink(ActionEvent event) {
        primaryStage.getScene().getStylesheets().clear();
        primaryStage.getScene().getStylesheets().add(getClass().getResource("/Css/Style 3.css").toExternalForm());
    }

   /* private void createCommitNode(Graph commitTree) {
        List<Commit.CommitData> sortedCommits = myMagit.getCurrentCommits();
        myMagit.createNewCommitNode(commitTree, sortedCommits, treeAnchorPane);
    }*/


    public void initialize() {
        layoutHbox.setVisible(false);

        treeViewPane.visibleProperty().setValue(true);

        currentRepo.wrappingWidthProperty().set(120);
        userName.wrappingWidthProperty().set(70);
        currentBranch.wrappingWidthProperty().set(50);

        userName.textProperty().bind(myMagit.getUserName());
        currentRepo.textProperty().bind(myMagit.getRepoName());
        currentBranch.textProperty().bind(myMagit.getCurrentBranch());

        treeViewPane.setVisible(false);

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
        updateCommitTree();
    }

    private void updateCommitTree(){
        treeViewPane.setVisible(true);
        treeViewPane.prefHeightProperty().bind(mainTextWindow.heightProperty());
        treeScrollPane.prefHeightProperty().bind(textPane.heightProperty());
        treeAnchorPane.prefHeightProperty().bind(textPane.heightProperty().subtract(2));

        //Graph commitTree = new Graph();
        //createCommitNode(commitTree);


    }
}
