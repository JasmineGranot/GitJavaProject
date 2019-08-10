import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;

class UI {

    private final int UPDATE_USER = 1, LOAD_REPOSITORY = 2, SWITCH_REPOSITORY = 3, SHOW_CURRENT_COMMIT = 4;
    private final int SHOW_STATUS = 5, COMMIT = 6, SHOW_BRANCHES = 7, CREATE_NEW_BRANCH = 8, DELETE_BRANCH = 9;
    private final int CHECKOUT_BRANCH = 10, SHOW_ACTIVE_BRANCH_HISTORY = 11, NEW_REPOSITORY = 12, RESET_BRANCH = 13;
    private final int EXIT = 14;
    private final int START_LOOP = -1;
    private Scanner reader = new Scanner(System.in);
    private Magit myMagit = new Magit();

    void runProgram() throws IOException {
        // testData();
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

    void loadRepository(){
        System.out.println("Please enter the path of the new Repository xml file:");
        String repoPath = reader.nextLine();
        repoPath = reader.nextLine();
        try {
            myMagit.loadRepositoryFromXml(repoPath);
        }
        catch (Exception e){
            System.out.println("We still want 100.... ");
        }
        System.out.println("Cool!");
    }

    void createNewRepository(){
        System.out.println("Please enter a path for a new Repository:");
        String repoPath = reader.nextLine();
        repoPath = reader.nextLine();
        try {
            boolean isCreated = myMagit.createNewRepo(repoPath);
        }
        catch (Exception e){
            System.out.println("We still want 100.... ");
        }
        System.out.println("Cool!");
    }

    void switchRepository(){
        System.out.println("Please enter a repository's path to switch to...:");
        String branchName = reader.nextLine();
        branchName = reader.nextLine();
        myMagit.changeRepository(branchName);
    }

    void showCurrentCommit(){
        System.out.println("============================");
        System.out.println("The current commit full data:");
        System.out.println(myMagit.showFullCommitData());
        System.out.println("============================");
        System.out.println();
    }

    void showWcStatus(){
        myMagit.showStatus();
    }

    void commit() {
        System.out.println("Please enter a message for this commit of yours...:");
        String msg = reader.nextLine();
        msg = reader.nextLine();

        try {
            myMagit.createNewCommit(msg);

        } catch (Exception e) {
            System.out.println("failed");
        }
    }

    private void showBranches(){
        MagitStringResultObject result = myMagit.showAllBranches();
        if (result.haveError){
            System.out.println(result.errorMSG);
        }
        else{
            System.out.println(result.data);
        }
    }

    void createNewBranch(){
        System.out.println("Please enter a new branch's name...:");
        String branchName = reader.nextLine();
        branchName = reader.nextLine();
        myMagit.addNewBranch(branchName);

    }

    void deleteBranch(){
        System.out.println("Please enter a branch's name to delete...:");
        String branchName = reader.nextLine();
        branchName = reader.nextLine();
        myMagit.deleteBranch(branchName);
    }

    void checkoutABranch(){
        System.out.println("Please enter a branch's name to checkout...:");
        String branchName = reader.nextLine();
        branchName = reader.nextLine();
        myMagit.checkoutBranch(branchName);
    }

    void showActiveBranchHistory(){
        myMagit.showHistoryDataForActiveBranch();
    }

    void resetBranchToSpecificCommit(){
        System.out.println("Please enter a commit's sha1 to checkout...:");
        String commitSha1 = reader.nextLine();
        commitSha1 = reader.nextLine();
        myMagit.resetBranch(commitSha1);
    }
    void testData() {
        try{
            myMagit.createNewRepo("C:\\Users\\97250\\Desktop\\Test");
        }
        catch (Exception e){
            e.getMessage();
        }
        String path = Paths.get("C:\\Users\\97250\\Desktop\\Test\\A\\").toString();
        File newFile = new File(path);
        newFile.mkdir();
        path = Paths.get("C:\\Users\\97250\\Desktop\\Test\\A\\a").toString();
        try{
            Writer out1 = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(path), StandardCharsets.UTF_8));
            out1.write("stam shit");
            out1.flush();
            out1.close();
        }
        catch (IOException e){
            System.out.println("error in closing file");
        }

        path = Paths.get("C:\\Users\\97250\\Desktop\\Test\\b.txt").toString();
        newFile = new File(path);
        try{
            Writer out1 = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(path), StandardCharsets.UTF_8));
            out1.write("shit aher");
            out1.flush();
            out1.close();
        }
        catch (IOException e){
            System.out.println("error in closing file");
        }
    }

    void showMenu(){

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
