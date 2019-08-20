package MagitMain;
import Engine.*;
import Exceptions.*;
import Utils.MagitStringResultObject;
import Utils.WorkingCopyChanges;

import java.nio.file.DirectoryNotEmptyException;
import java.util.*;



class UI {

    private final int UPDATE_USER = 1, LOAD_REPOSITORY = 2, SWITCH_REPOSITORY = 3, SHOW_CURRENT_COMMIT = 4;
    private final int SHOW_STATUS = 5, COMMIT = 6, SHOW_BRANCHES = 7, CREATE_NEW_BRANCH = 8, DELETE_BRANCH = 9;
    private final int CHECKOUT_BRANCH = 10, SHOW_ACTIVE_BRANCH_HISTORY = 11, NEW_REPOSITORY = 12, RESET_BRANCH = 13;
    private final int EXIT = 14;
    private final int START_LOOP = -1;
    private Scanner reader = new Scanner(System.in);
    private Magit myMagit = new Magit();

    void runProgram() {
        int user_input = START_LOOP;
        while (user_input != EXIT) {
            System.out.println("Welcome to MAGIT!");
            showMenu();
            try {
                user_input = reader.nextInt();

                switch (user_input) {
                    case UPDATE_USER:
                        //updateMagitUser();
                        break;
                    case LOAD_REPOSITORY:
                        loadRepository();
                        break;
                    case SWITCH_REPOSITORY:
                        switchRepository();
                        break;
                    case SHOW_CURRENT_COMMIT:
                        showCurrentCommit();
                        break;
                    case SHOW_STATUS:
                        showWcStatus();
                        break;
                    case COMMIT:
                        commit();
                        break;
                    case SHOW_BRANCHES:
                        showBranches();
                        break;
                    case CREATE_NEW_BRANCH:
                        createNewBranch();
                        break;
                    case DELETE_BRANCH:
                        deleteBranch();
                        break;
                    case CHECKOUT_BRANCH:
                        checkoutABranch();
                        break;
                    case SHOW_ACTIVE_BRANCH_HISTORY:
                        showActiveBranchHistory();
                        break;
                    case NEW_REPOSITORY:
                        createNewRepository();
                        break;
                    case RESET_BRANCH:
                        resetBranchToSpecificCommit();
                        break;
                    case EXIT:
                        System.out.println("Thank you for using MAGIT! Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option, please choose again!");
                        System.out.println();
                }
            } catch (InputMismatchException e) {
                System.out.println("Input invalid! Please try again...");
                System.out.println();
                reader.next();
            }
        }
    }

   /* private void updateMagitUser() {
        System.out.println("Please enter a new user name: ");
        String userName = reader.nextLine();
        userName = reader.nextLine();
        myMagit.setUserName(userName);
        System.out.println(String.format("User name was changed to: %s", userName));
        System.out.println();
    }*/

    private void loadRepository() {
        System.out.println("Please enter the repository XML file path:");
        String repoPath = reader.nextLine();
        repoPath = reader.nextLine();

        loadARepository(repoPath, false);
    }

    private void loadARepository(String repoPath, boolean toDeleteExistingRepo) {
        try {
            MagitStringResultObject result = myMagit.loadRepositoryFromXML(repoPath, toDeleteExistingRepo);
            if (result.getIsHasError()) {
                System.out.println(result.getErrorMSG());
                System.out.println();
            } else {
                System.out.println(result.getData());
                System.out.println();
            }
        }
        catch (DataAlreadyExistsException e) {
            System.out.println(e.getMessage());
            System.out.println("Please choose from the following options:\n" +
                        "1. Cancel the procedure\n" +
                        "2. Continue and delete the existing repository" );
                try {
                    int input = reader.nextInt();
                    while (input != 1 && input != 2){
                        System.out.println("Please press only 1 or 2!");
                        input = reader.nextInt();
                    }
                    if(input == 1){
                        return;
                    }
                    if(input == 2){
                        loadARepository(repoPath, true);
                    }
                }
                catch (InputMismatchException t){
                    System.out.println("You can only insert an integer! starting over!");
                    checkoutABranch();
                }
        }
    }


    private void createNewRepository(){
        System.out.println("Please enter a path for a new Repository:");
        String repoPath = reader.nextLine();
        repoPath = reader.nextLine();

        System.out.println("Please enter a name for a new Repository:");
        String repoName = reader.nextLine();
        repoName = reader.nextLine();

        MagitStringResultObject result = myMagit.createNewRepo(repoPath, repoName);
        if(result.getIsHasError()){
            System.out.println(result.getErrorMSG());
            System.out.println();
        }
        else{
            System.out.println(result.getData());
            System.out.println();
        }

    }

    private void switchRepository() {
        System.out.println("Please enter a repository's path to switch to...:");
        String repoPath = reader.nextLine();
        repoPath = reader.nextLine();

        MagitStringResultObject result = myMagit.changeRepository(repoPath);
        if (result.getIsHasError()) {
            System.out.println(result.getErrorMSG());
            System.out.println();
        } else {
            System.out.println(result.getData());
            System.out.println();
        }
    }

    private void showCurrentCommit(){
        MagitStringResultObject result = myMagit.showFullCommitData();
        if(result.getIsHasError()){
            System.out.println(result.getErrorMSG());
            System.out.println();
        }
        else{
            System.out.println("============================");
            System.out.println("The current commit full data:");
            System.out.println(result.getData());
            System.out.println("============================");
            System.out.println();
        }
    }

    private void showWcStatus(){
        WorkingCopyChanges result =  myMagit.showStatus();
        Set<String> newFiles = result.getNewFiles();
        Set<String> changedFiles = result.getChangedFiles();
        Set<String> deletedFiles = result.getDeletedFiles();

        if(result.getHasErrors()){
            System.out.println(result.getErrorMsg());
            System.out.println();
        }
        else {
            System.out.println(result.getMsg());
            if(newFiles.isEmpty() && changedFiles.isEmpty() && deletedFiles.isEmpty()) {
                System.out.println("There were no changes in the repository!\n");
            }
            else {
                if (!newFiles.isEmpty()) {
                    System.out.println("New files:");
                    for (String curr : result.getNewFiles()) {
                        System.out.println(curr);
                        System.out.println();
                    }
                }

                if (!changedFiles.isEmpty()) {
                    System.out.println("Changed files:");
                    for (String curr : result.getChangedFiles()) {
                        System.out.println(curr);
                        System.out.println();
                    }
                }


                if (!deletedFiles.isEmpty()) {
                    System.out.println("Deleted files:");
                    for (String curr : result.getDeletedFiles()) {
                        System.out.println(curr);
                        System.out.println();
                    }
                }
            }
        }
    }

    private void commit() {
        System.out.println("Please enter a message for the new commit:");
        String msg = reader.nextLine();
        msg = reader.nextLine();

        MagitStringResultObject result = myMagit.createNewCommit(msg);
        if (result.getIsHasError()){
            System.out.println(result.getErrorMSG());
            System.out.println();
        }
        else {
            System.out.println(result.getData());
            System.out.println();
        }
    }

    private void showBranches(){
        MagitStringResultObject result = myMagit.showAllBranches();
        if (result.getIsHasError()){
            System.out.println(result.getErrorMSG());
            System.out.println();
        }
        else {
            System.out.println(result.getData());
            System.out.println();
        }
    }

    private void createNewBranch(){
        System.out.println("Please enter a new branch's name...:");
        String branchName = reader.nextLine();
        branchName = reader.nextLine();

        try {
            MagitStringResultObject resultObject = myMagit.addNewBranch(branchName);
            if(resultObject.getIsHasError()){
                System.out.println(resultObject.getErrorMSG());
                System.out.println();
            }
            else{
                System.out.println(resultObject.getData());
                System.out.println();
            }
        }
        catch (InvalidDataException e){
            System.out.println(e.getMessage());
            createNewBranch();
        }


    }

    private void deleteBranch(){
        System.out.println("Please enter a branch's name to delete...:");
        String branchName = reader.nextLine();
        branchName = reader.nextLine();
        try {
            MagitStringResultObject resultObject = myMagit.deleteBranch(branchName);
            if (resultObject.getIsHasError()) {
                System.out.println(resultObject.getErrorMSG());
                System.out.println();
            }
            else {
                System.out.println(resultObject.getData());
                System.out.println();
            }
        }
        catch (InvalidDataException e) {
            System.out.println(e.getMessage());
            deleteBranch();
        }
    }

    private void checkoutABranch() {
        System.out.println("Please enter a branch's name to checkout...:");
        String branchName = reader.nextLine();
        branchName = reader.nextLine();

        checkoutBranch(branchName, false);
    }

    private void checkoutBranch(String branchName, boolean ignoreChanges) {
        try {
            MagitStringResultObject resultObject = myMagit.checkoutBranch(branchName, ignoreChanges);
            if(resultObject.getIsHasError()){
                System.out.println(resultObject.getErrorMSG());
                System.out.println();
            }
            else {
                System.out.println(resultObject.getData());
                System.out.println();
            }
        }
        catch (InvalidDataException e){
            System.out.println(e.getMessage());
            checkoutABranch();
        }
        catch (DirectoryNotEmptyException e){
            System.out.println(e.getMessage());
            System.out.println("Please choose from the following options:\n" +
                    "1. Commit the open changes\n" +
                    "2. Continue with the checkout without committing the changes" );
            try {
                int input = reader.nextInt();
                while (input != 1 && input != 2){
                    System.out.println("Please press only 1 or 2!");
                    input = reader.nextInt();
                }
                if(input == 1){
                    commit();
                    checkoutBranch(branchName,false);
                }
                if(input == 2){
                    checkoutBranch(branchName,true);
                }
            }
            catch (InputMismatchException t){
                System.out.println("You can only insert an integer! starting over!");
                checkoutABranch();
            }

        }
    }

    private void showActiveBranchHistory(){
        MagitStringResultObject resultObject = myMagit.showHistoryDataForActiveBranch();
        if(resultObject.getIsHasError()){
            System.out.println(resultObject.getErrorMSG());
            System.out.println();
        }
        else {
            System.out.println(resultObject.getData());
            System.out.println();
        }
    }

    private void resetBranchToSpecificCommit() {
        System.out.println("Please enter a commit's sha1 to checkout...:");
        String commitSha1 = reader.nextLine();
        commitSha1 = reader.nextLine();

        resetBranchToASpecificCommit(commitSha1, true);
    }

    private void resetBranchToASpecificCommit(String sha1, boolean ignore) {
        try {
            MagitStringResultObject resultObject = myMagit.resetBranch(sha1, ignore);
            if (resultObject.getIsHasError()) {
                System.out.println(resultObject.getErrorMSG());
                System.out.println();
            } else {
                System.out.println(resultObject.getData());
                System.out.println();
            }
        }
        catch(DirectoryNotEmptyException e){
            System.out.println(e.getMessage());
            System.out.println("Please choose from the following options:\n" +
                    "1. Commit the open changes\n" +
                    "2. Continue with the reset without committing the changes" );
            try {
                int input = reader.nextInt();
                while (input != 1 && input != 2){
                    System.out.println("Please press only 1 or 2!");
                    input = reader.nextInt();
                }
                if(input == 1){
                    commit();
                    resetBranchToASpecificCommit(sha1, false);
                }
                if(input == 2){
                    resetBranchToASpecificCommit(sha1, true);
                }
            }
            catch (InputMismatchException t){
                System.out.println("You can only insert an integer! starting over!");
                resetBranchToSpecificCommit();
            }
        }
    }

    private void showMenu(){

        System.out.println("Please choose from the following options: ");
        System.out.println(String.format("%d.\tChanging active username", UPDATE_USER));
        System.out.println(String.format("%d.\tLoad repository from XML", LOAD_REPOSITORY));
        System.out.println(String.format("%d.\tSwitch repository", SWITCH_REPOSITORY));
        System.out.println(String.format("%d.\tShow files of current commit", SHOW_CURRENT_COMMIT));
        System.out.println(String.format("%d.\tShow WC Status", SHOW_STATUS));
        System.out.println(String.format("%d.\tCommit", COMMIT));
        System.out.println(String.format("%d.\tShow all active branches", SHOW_BRANCHES));
        System.out.println(String.format("%d.\tCreate new branch", CREATE_NEW_BRANCH));
        System.out.println(String.format("%d.\tDelete branch", DELETE_BRANCH));
        System.out.println(String.format("%d.\tCheckout a branch", CHECKOUT_BRANCH));
        System.out.println(String.format("%d.\tShow head's history", SHOW_ACTIVE_BRANCH_HISTORY));
        System.out.println(String.format("%d.\tCreate a new repository", NEW_REPOSITORY));
        System.out.println(String.format("%d.\tReset head to specific commit", RESET_BRANCH));
        System.out.println(String.format("%d.\tExit", EXIT));
    }
}
