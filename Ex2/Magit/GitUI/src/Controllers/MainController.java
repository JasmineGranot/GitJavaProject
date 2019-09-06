package Controllers;

import Engine.Magit;
import Exceptions.DataAlreadyExistsException;
import Exceptions.InvalidDataException;
import GitObjects.Commit;
import UIUtils.CommitNode;
import Utils.MagitStringResultObject;
import Utils.WorkingCopyChanges;
import com.fxgraph.graph.PannableCanvas;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import java.io.File;
import java.net.URL;
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

//    @FXML private Text userName;
//    @FXML private Button changeUserButton;
//    @FXML private Text currentBranch;
//    @FXML private Button newBranchButton;
//    @FXML private Button resetBranchButton;
//    @FXML private Button deleteBranchButton;
//    @FXML private Button changeRepoButton;
//    @FXML private Button loadRepoButton;
//    @FXML private Button createNewRepoButton;
//    @FXML private Button showStatusButton;
//    @FXML private Button commitButton;
//    @FXML private Button pushButton;
//    @FXML private Button pullButton;
//    @FXML private Button mergeButton;
//    @FXML private Button cloneButton;
//    @FXML private Button fetchButton;
//    @FXML private ComboBox<String> branchesOptionsComboBox;
//    @FXML private Button checkoutButton;
//    @FXML private Pane textPane;
//    @FXML private Text currentRepo;
//    @FXML private Pane treeViewPane;
//    @FXML private SplitPane mainTextWindow;
//    @FXML private ScrollPane treeScrollPane;
//    @FXML private AnchorPane treeAnchorPane;
//    @FXML private HBox layoutHbox;
//    @FXML private Button changeLayoutButton;
//    @FXML private Text pathRepo;

    @FXML private Text userName;
    @FXML private Button changeUserButton;
    @FXML private Text currentBranch;
    @FXML private Button newBranchButton;
    @FXML private Button resetBranchButton;
    @FXML private Label magitHeader;
    @FXML private Button changeRepoButton;
    @FXML private Button loadRepoButton;
    @FXML private Button createNewRepoButton;
    @FXML private Text currentRepo;
    @FXML private Text pathRepo;
    @FXML private Button changeLayoutButton;
    @FXML private HBox layoutHbox;
    @FXML private Button pinkButton;
    @FXML private Button blueButton;
    @FXML private Button greenButton;
    @FXML private SplitPane mainSplitPane;
    @FXML private Label magitLargeHeader;
    @FXML private ComboBox<String> branchesOptionsComboBox;
    @FXML private Button checkoutButton;
    @FXML private Button deleteBranchButton;
    @FXML private Pane showStatusPane;
    @FXML private AnchorPane treeAnchorPane;
    @FXML private Text commitTreeHeader;
    @FXML private Button showStatusButton;
    @FXML private Button commitButton;
    @FXML private Button pushButton;
    @FXML private Button pullButton;
    @FXML private Button mergeButton;
    @FXML private Button cloneButton;
    @FXML private Button fetchButton;

    private ScrollPane scrollPane = new ScrollPane();
    private PannableCanvas canvas;
    private Scene scene;
    private Magit myMagit = new Magit();
    private Stage primaryStage;
    private ShowStatusController statusController = new ShowStatusController();
    private boolean isShowStatusOpen = false;
    private CommitNodeController commitNodeController = new CommitNodeController();
    private boolean isGreenButtonPressed = false;
    private boolean isBlueButtonPressed = true;
    private boolean isPinkButtonPressed = false;
    private String style;


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
                showStatusPane.getChildren().clear();
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
                showStatusPane.getChildren().clear();
                isShowStatusOpen = false;
            }

            MagitStringResultObject res =
                    myMagit.loadRepositoryFromXML(selectFile.getAbsolutePath(), false);
            if (res.getIsHasError()){
                CommonUsed.showError(res.getErrorMSG());
            }
            else {
                CommonUsed.showSuccess(res.getData());
                setRepoActionsAvailable();
                currentBranch.textProperty().unbind();
                currentBranch.textProperty().bind(myMagit.getCurrentBranch());
                pathRepo.textProperty().bind(myMagit.getPath());
                clearCommitTree();
                showCommitTree();
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
            showStatusPane.getChildren().clear();
            isShowStatusOpen = false;
        }

        MagitStringResultObject res = myMagit.changeRepository(selectFile.getAbsolutePath());
        if (!res.getIsHasError()) {
            CommonUsed.showSuccess(res.getData());
            setRepoActionsAvailable();
            currentBranch.textProperty().unbind();
            currentBranch.textProperty().bind(myMagit.getCurrentBranch());
            pathRepo.textProperty().bind(myMagit.getPath());
            clearCommitTree();
            showCommitTree();
        }
        else {
            CommonUsed.showError(res.getErrorMSG());
        }

    }

    @FXML
    void deleteBranch() {
        try {
            if(isShowStatusOpen){
                showStatusPane.getChildren().clear();
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
            showStatusPane.getChildren().clear();
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
            showStatusPane.getChildren().clear();
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
                    showStatusPane.getChildren().clear();
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
                showStatusPane.getChildren().clear();
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

            statusController.showWcStatus(newFiles, changedFiles, deletedFiles, showStatusPane);
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
        if(canvas != null) {
            canvas.setBackground(Background.EMPTY);
            style = "-fx-background-color: #3B5998";
            canvas.setStyle(style);
            scrollPane.setContent(canvas);
            treeAnchorPane.getChildren().clear();
            treeAnchorPane.getChildren().add(scrollPane);
            setTreeBoundaries();
            primaryStage.getScene().getStylesheets().add(getClass().getResource("/Css/CommitNode.css").toExternalForm());
        }

        isBlueButtonPressed = true;
        isGreenButtonPressed = false;
        isPinkButtonPressed = false;
    }

    @FXML
    void changeStyleToGreen(ActionEvent event) {
        primaryStage.getScene().getStylesheets().clear();
        primaryStage.getScene().getStylesheets().add(getClass().getResource("/Css/Style 2.css").toExternalForm());
        if(canvas != null) {
            canvas.setBackground(Background.EMPTY);
            style = "-fx-background-color: #405d27";
            canvas.setStyle(style);
            scrollPane.setContent(canvas);
            treeAnchorPane.getChildren().clear();
            treeAnchorPane.getChildren().add(scrollPane);
            setTreeBoundaries();
            primaryStage.getScene().getStylesheets().add(getClass().getResource("/Css/CommitNode2.css").toExternalForm());
        }
        isGreenButtonPressed = true;
        isBlueButtonPressed = false;
        isPinkButtonPressed = false;
    }

    @FXML
    void changeStyleToPink(ActionEvent event) {
        primaryStage.getScene().getStylesheets().clear();
        primaryStage.getScene().getStylesheets().add(getClass().getResource("/Css/Style 3.css").toExternalForm());
        if(canvas != null) {
            canvas.setBackground(Background.EMPTY);
            style = "-fx-background-color: #622569";
            canvas.setStyle(style);
            scrollPane.setContent(canvas);
            treeAnchorPane.getChildren().clear();
            treeAnchorPane.getChildren().add(scrollPane);
            setTreeBoundaries();
            primaryStage.getScene().getStylesheets().add(getClass().getResource("/Css/CommitNode3.css").toExternalForm());
        }

        isPinkButtonPressed = true;
        isGreenButtonPressed = false;
        isBlueButtonPressed = false;
    }

    public void setScene(Scene scene){
        this.scene = scene;
    }

    private void clearCommitTree() {
        treeAnchorPane.getChildren().clear();
    }

    private void showCommitTree(){
        Graph commitTreeGraph = new Graph();
        commitNodeController.setSortedCommits(myMagit.getCurrentCommits());
        commitNodeController.createCommitNode(commitTreeGraph);

        canvas = commitTreeGraph.getCanvas();
        canvas.setBackground(Background.EMPTY);

        if(isPinkButtonPressed) {
            style = "-fx-background-color: #622569";
            canvas.setStyle(style);
            scrollPane.setContent(canvas);
            scrollPane.setStyle(style);
            treeAnchorPane.getChildren().clear();
            treeAnchorPane.getChildren().add(scrollPane);
            setTreeBoundaries();
            primaryStage.getScene().getStylesheets().add(getClass().getResource("/Css/CommitNode3.css").toExternalForm());
        }

        if (isGreenButtonPressed) {
            style = "-fx-background-color: #405d27";
            canvas.setStyle(style);
            scrollPane.setContent(canvas);
            treeAnchorPane.getChildren().clear();
            treeAnchorPane.getChildren().add(scrollPane);
            setTreeBoundaries();
            primaryStage.getScene().getStylesheets().add(getClass().getResource("/Css/CommitNode2.css").toExternalForm());
        }
        if (isBlueButtonPressed) {
            style = "-fx-background-color: #3B5998";
            canvas.setStyle(style);
            scrollPane.setContent(canvas);
            treeAnchorPane.getChildren().add(scrollPane);
            setTreeBoundaries();
            primaryStage.getScene().getStylesheets().add(getClass().getResource("/Css/CommitNode.css").toExternalForm());
        }

//        Button moreDataButton = commitNodeController.getButton();
//        moreDataButton.setOnAction(e -> {
//            commitNodeController.showCommitData(commitTreeGraph.getModel().getAddedCells().get(0));
//        });


        Platform.runLater(() -> {
            commitTreeGraph.getUseViewportGestures().set(false);
            commitTreeGraph.getUseNodeGestures().set(false);
        });

    }

    private void setTreeBoundaries() {
        scrollPane.prefHeightProperty().unbind();
        scrollPane.prefWidthProperty().unbind();

        scrollPane.prefWidthProperty().bind(treeAnchorPane.widthProperty());
        scrollPane.prefHeightProperty().bind(treeAnchorPane.heightProperty());

        canvas.prefHeightProperty().bind(treeAnchorPane.heightProperty());
        canvas.prefWidthProperty().bind(treeAnchorPane.widthProperty());

        //scrollPane.setMinViewportHeight(canvas.getHeight());
    }

    public void initialize() {
        layoutHbox.setVisible(false);

        currentRepo.wrappingWidthProperty().set(120);
        userName.wrappingWidthProperty().set(70);
        currentBranch.wrappingWidthProperty().set(50);

        userName.textProperty().bind(myMagit.getUserName());
        currentRepo.textProperty().bind(myMagit.getRepoName());
        currentBranch.textProperty().bind(myMagit.getCurrentBranch());

        treeAnchorPane.prefWidthProperty().bind(mainSplitPane.widthProperty());

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
