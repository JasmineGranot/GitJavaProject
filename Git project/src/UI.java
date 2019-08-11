import java.nio.file.DirectoryNotEmptyException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;

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
        while (user_input != EXIT){
            showMenu();
            try {
                user_input = reader.nextInt();

                switch (user_input) {
                    case UPDATE_USER:
                        updateMagitUser();
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
                }
            }
            catch (InputMismatchException e){
                System.out.println("Input invalid! Please try again...");
                reader.next();
            }
        }
    }

    private void updateMagitUser(){
        System.out.println("Please enter a new user name: ");
        String userName = reader.nextLine();
        userName = reader.nextLine();
        myMagit.setUserName(userName);
        System.out.println(String.format("User name was changed to: %s", userName));
    }

    private void loadRepository(){}

    private void createNewRepository(){
        System.out.println("Please enter a path for a new Repository:");
        String repoPath = reader.nextLine();
        repoPath = reader.nextLine();

        MagitStringResultObject result = myMagit.createNewRepo(repoPath);
        if(result.haveError){
            System.out.println(result.errorMSG);
            System.out.println();
        }
        else{
            System.out.println(result.data);
            System.out.println();
        }

    }

    private void switchRepository(){
        System.out.println("Please enter a repository's path to switch to...:");
        String branchName = reader.nextLine();
        branchName = reader.nextLine();

        MagitStringResultObject result = myMagit.changeRepository(branchName);

        if(result.haveError){
            System.out.println(result.errorMSG);
        }
        else{
            System.out.println(result.data);
        }
    }

    private void showCurrentCommit(){
        MagitStringResultObject result = myMagit.showFullCommitData();
        if(result.haveError){
            System.out.println(result.errorMSG);
        }
        else{
            System.out.println("============================");
            System.out.println("The current commit full data:");
            System.out.println(result.data);
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
        }
        else{
            System.out.println(result.getMsg());

            if(!newFiles.isEmpty()){
                System.out.println("New files:");
                for(String curr : result.getNewFiles()){
                    System.out.println(curr);
                    System.out.println();
                }
            }

            if(!changedFiles.isEmpty()){
                System.out.println("Changed files:");
                for(String curr : result.getChangedFiles()){
                    System.out.println(curr);
                    System.out.println();
                }
            }


            if(!deletedFiles.isEmpty()){
                System.out.println("Deleted files:");
                for(String curr : result.getDeletedFiles()){
                    System.out.println(curr);
                    System.out.println();
                }
            }
        }
    }

    private void commit() {
        System.out.println("Please enter a message for the new commit:");
        String msg = reader.nextLine();
        msg = reader.nextLine();

        MagitStringResultObject result = myMagit.createNewCommit(msg);
        if (result.haveError){
            System.out.println(result.errorMSG);
        }
        else {
            System.out.println(result.data);
        }
    }

    private void showBranches(){
        MagitStringResultObject result = myMagit.showAllBranches();
        if (result.haveError){
            System.out.println(result.errorMSG);
            System.out.println();
        }
        else {
            System.out.println(result.data);
            System.out.println();
        }
    }

    private void createNewBranch(){
        System.out.println("Please enter a new branch's name...:");
        String branchName = reader.nextLine();
        branchName = reader.nextLine();

        try {
            MagitStringResultObject resultObject = myMagit.addNewBranch(branchName);
            if(resultObject.haveError){
                System.out.println(resultObject.errorMSG);
            }
            else{
                System.out.println(resultObject.data);
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
            if (resultObject.haveError) {
                System.out.println(resultObject.errorMSG);
            } else {
                System.out.println(resultObject.data);
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
            if(resultObject.haveError){
                System.out.println(resultObject.errorMSG);
            }
            else {
                System.out.println(resultObject.data);
            }
        }
        catch (InvalidDataException e){
            System.out.println(e.getMessage());
            checkoutABranch();
        }
        catch (DirectoryNotEmptyException e){
            System.out.println(e.getMessage());
            System.out.println("Please choose from the following options:\n" +
                    "1 for committing the open changes\n" +
                    "2 for continuing with the checkout without committing the changes" );
            try {
                int input = reader.nextInt();
                while (input != 1 && input != 2){
                    System.out.println("Please press only 1 or 2!");
                    input = reader.nextInt();
                }
                if(input == 1){
                    commit();
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
        if(resultObject.haveError){
            System.out.println(resultObject.errorMSG);
        }
        else{
            System.out.println(resultObject.data);
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
            if (resultObject.haveError) {
                System.out.println(resultObject.errorMSG);
            } else {
                System.out.println(resultObject.data);
            }
        }
        catch(DirectoryNotEmptyException e){
            System.out.println(e.getMessage());
            System.out.println("Please choose from the following options:\n" +
                    "1 for committing the open changes\n" +
                    "2 for continuing with the reset without committing the changes" );
            try {
                int input = reader.nextInt();
                while (input != 1 && input != 2){
                    System.out.println("Please press only 1 or 2!");
                    input = reader.nextInt();
                }
                if(input == 1){
                    commit();
                }
                if(input == 2){
                    resetBranchToASpecificCommit(sha1, false);

                }
            }
            catch (InputMismatchException t){
                System.out.println("You can only insert an integer! starting over!");
                resetBranchToSpecificCommit();
            }
        }
    }

    private void showMenu(){

        System.out.println("Welcome to MAGIT!");
        System.out.println("Please choose from the following options: ");
        System.out.println(String.format("For changing the userName press %d", UPDATE_USER));
        System.out.println(String.format("For loading a repository from an XML file press %d", LOAD_REPOSITORY));
        System.out.println(String.format("For switching the active repository press %d", SWITCH_REPOSITORY));
        System.out.println(String.format("For showing the current commit press %d", SHOW_CURRENT_COMMIT));
        System.out.println(String.format("For showing the status of the WC press %d", SHOW_STATUS));
        System.out.println(String.format("For creating a new commit press %d", COMMIT));
        System.out.println(String.format("For showing the active branches press %d", SHOW_BRANCHES));
        System.out.println(String.format("For creating a new branch press %d", CREATE_NEW_BRANCH));
        System.out.println(String.format("For deleting a branch press %d", DELETE_BRANCH));
        System.out.println(String.format("For checkout a branch press %d", CHECKOUT_BRANCH));
        System.out.println(String.format("For showing the active branch's history press %d",
                SHOW_ACTIVE_BRANCH_HISTORY));
        System.out.println(String.format("For creating new repo in the system press %d", NEW_REPOSITORY));
        System.out.println(String.format("For reset head branch to specific commit in the system press %d",
                RESET_BRANCH));
        System.out.println(String.format("For exiting the system press %d", EXIT));

    }
}
