
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.walk;


class Repository {
    private String MAGIT_PATH = Paths.get(getRootPath(), ".magit").toString();
    private String BRANCHES_PATH = Paths.get(MAGIT_PATH, "Branches").toString();
    private String OBJECTS_PATH = Paths.get(MAGIT_PATH, "Objects").toString();

    private String rootPath;
    private Map<String, GitObjectsBase> currentObjects = new HashMap<>();
    private Commit currentCommit = null;
    private String currentUser;

    private boolean isObjectInRepo(String id) {
        return currentObjects.containsKey(id);
    }

    String getRootPath() {
        return rootPath;
    }

    private void setRootPath(String path) {
        this.rootPath = path;
    }

    private String getRootSha1() { // goes to branches->head x then branches->x->lastCommit y and then objects->y

        try {
            File headFile = getCurrentBranchFile();

            BufferedReader reader = new BufferedReader(new FileReader(headFile));
            String currentCommitSha1 = reader.readLine();
            reader.close();

//        String currentCommitSha1 = unZipAndReadFile(head_branch.toString());

            if (currentCommitSha1 != null && !currentCommitSha1.equals("")) {
                Path commitFile = Paths.get(getRootPath(), ".magit", "Objects", currentCommitSha1);
                String commitData = MagitUtils.unZipAndReadFile(commitFile.toString());
                if (commitData != null) {
                    String[] commitFields = commitData.split(MagitUtils.DELIMITER);
                    return commitFields[0];
                }
                return "";

            }
        }
        catch (IOException e){return "";}

        return "";
    }

    private void printSet(Set<String> setToPrint) {
        for (String currentPath : setToPrint) {
            System.out.println(currentPath);
        }
    }

    void printWCStatus() {
        isWorkingCopyIsChanged(true);
    }


    // =========================== Creating New Repo ==================================
    boolean createNewRepository(String newRepositoryPath) {
        boolean isCreated = false;
        try {
            File newFile = new File(newRepositoryPath);
            if (!newFile.exists()) {
                isCreated = newFile.mkdirs();
                if (isCreated) {
                    isCreated = addNewFilesToRepo(newRepositoryPath);
                    changeRepo(newRepositoryPath);
                }
            }
            else{
                System.out.println("Throw repository already exists exception");
            }
            return isCreated;
        } catch (Exception e) {
            e.printStackTrace();
            return isCreated;
        }
    }

    private boolean addNewFilesToRepo(String newRepositoryPath) {
        boolean isCreated = false;
        try {
            String magitPath = MagitUtils.joinPaths(newRepositoryPath, ".magit");
            File newFile = new File(magitPath);
            isCreated = newFile.mkdir();

            newFile = new File(MagitUtils.joinPaths(newRepositoryPath, ".magit\\Branches"));
            isCreated = isCreated && newFile.mkdir();

            Path HeadPath = Paths.get(MagitUtils.joinPaths(newRepositoryPath, ".magit\\Branches\\Head"));
            Writer out1 = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(HeadPath.toString()), StandardCharsets.UTF_8));
            out1.write("master");
            out1.close();

            HeadPath = Paths.get(MagitUtils.joinPaths(newRepositoryPath, ".magit\\Branches\\master"));
            out1 = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(HeadPath.toString()), StandardCharsets.UTF_8));
            out1.write("");
            out1.close();

            newFile = new File(MagitUtils.joinPaths(newRepositoryPath, ".magit\\Objects"));
            isCreated = isCreated && newFile.mkdir();

            return isCreated;
        } catch (Exception e) {
            e.printStackTrace();
            return isCreated;
        }
    }


    // =========================== Commit =========================================
    // NEW - NEED TO RECHECK
    private boolean isValidCommit(String sha1){
        GitObjectsBase obj = currentObjects.get(sha1);
        return (obj != null && obj.isCommit());
    }


    void createNewCommit(String userName, String commitMsg) {
        try {
            if (isWorkingCopyIsChanged(false)) {
                currentUser = userName;
                FileDetails rootData = updateFilesInSystem(getRootPath());
                Commit newCommit = new Commit();

                newCommit.setCommitCreator(userName);

                newCommit.setCommitDate(MagitUtils.getTodayAsStr());

                newCommit.setCommitMessage(commitMsg);

                newCommit.setRootSha1(rootData.getSha1());

                String commitSha1 = newCommit.doSha1();

                if (currentCommit != null) {
                    newCommit.setLastCommitsha1(getActiveCommitSha1InBranch(getActiveBranchName()));
                }
                currentCommit = newCommit;
                currentObjects.put(commitSha1, newCommit);

                updateCommitInCurrentBranch(commitSha1);
                currentCommit.saveToObjects(commitSha1, rootPath);
            }
            else {
                System.out.println("Nothing has changed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCommitInCurrentBranch(String commitSha1) throws IOException {
        File headFile = getCurrentBranchFile();
        Writer writer = new BufferedWriter(new FileWriter(headFile));
        writer.write(commitSha1);
        writer.flush();
        writer.close();
    }

    private boolean isWorkingCopyIsChanged(boolean toPrint) {
        boolean isChanged = true;
        try {
            Stream<Path> walk = walk(Paths.get(getRootPath()));
            Set<String> WCSet = walk.filter(x -> !x.toAbsolutePath().toString().contains(".magit")).filter(
                    Files::isRegularFile).map(Path::toString).collect(Collectors.toSet());

            Map<String, String> lastCommitFiles = new HashMap<>();
            if (currentCommit != null) {
                getLastCommitFiles(getRootSha1(), rootPath, lastCommitFiles);
            }

            Set<String> existsPath = new HashSet<>(WCSet);
            existsPath.retainAll(lastCommitFiles.keySet());

            Set<String> newFiles = new HashSet<>(WCSet);
            newFiles.removeAll(lastCommitFiles.keySet());

            Set<String> deletedFiles = new HashSet<>(lastCommitFiles.keySet());
            deletedFiles.removeAll(WCSet);

            getChanged(existsPath, lastCommitFiles);

            if (deletedFiles.isEmpty() && newFiles.isEmpty() && existsPath.isEmpty()) {
                isChanged = false;
            }

            if (toPrint) {
                if (!deletedFiles.isEmpty()) {
                    System.out.println("Deleted Files:");
                    printSet(deletedFiles);
                }
                if (!newFiles.isEmpty()) {
                    System.out.println("New Files:");
                    printSet(newFiles);
                }
                if (!existsPath.isEmpty()) {
                    System.out.println("Changed Files:");
                    printSet(existsPath);
                }

            }
            return isChanged;
        } catch (NullPointerException e) {
            System.out.println("repo is not configured... ");
            return false;
        } catch (IOException e) {
            System.out.println("could not get files.... ");
            return false;
        }
    }

    private boolean isFileChanged(String rootPathSha1, String sha1FileToCheck) {
        GitObjectsBase f = currentObjects.get(rootPathSha1);
        if (f.isFolder()) {
            Folder b = (Folder) f;
            for (FileDetails child : b.getFilesList()) {
                if (child.getSha1().equals(sha1FileToCheck)) {
                    return true;
                }
                isFileChanged(child.getSha1(), sha1FileToCheck);
            }
        } else {
            return false;
        }
        return false;
    }

    private void getLastCommitFiles(String rootSha1, String rootPath, Map<String, String> commitFiles) {
        GitObjectsBase f = currentObjects.get(rootSha1);
        if (!f.isFolder()) {
            commitFiles.put(rootPath, rootSha1);
        } else {
            Folder b = (Folder) f;
            for (FileDetails child : b.getFilesList()) {
                String name = Paths.get(rootPath, child.getFileName()).toString();
                getLastCommitFiles(child.getSha1(), name, commitFiles);
            }
        }
    }

    private void getChanged(Set<String> filesToCheck, Map<String, String> oldData) {
        Set<String> changedFiles = new HashSet<>();
        for (String currentFile : filesToCheck) {
            String content = MagitUtils.readFileAsString(currentFile);
            String sha1 = org.apache.commons.codec.digest.DigestUtils.sha1Hex(content);
            if (!oldData.get(currentFile).equals(sha1)) {
                changedFiles.add(currentFile);
            }
        }
        filesToCheck.clear();
        filesToCheck.addAll(changedFiles);
    }

    private FileDetails updateFilesInSystem(String filePath) {
        if (Files.isRegularFile(Paths.get(filePath))) {
            String content = MagitUtils.readFileAsString(filePath);
            String currentFileSha1 = org.apache.commons.codec.digest.DigestUtils.sha1Hex(content);

            if (isObjectInRepo(currentFileSha1) && isFileChanged(getRootSha1(), currentFileSha1)) {
                return getPrevData(currentFileSha1);
            } else {
                Blob newBlob = new Blob();
                newBlob.setFileContent(content);
                currentObjects.put(currentFileSha1, newBlob);
                //                    Path newPath = Paths.get(getRootPath(), ".magit", "Objects", currentFileSha1);
                newBlob.saveToObjects(currentFileSha1, getRootPath());

                return getNewData(currentFileSha1, Paths.get(filePath));
            }
        } else {

            Folder newFolder = new Folder();
            File newFile = new File(filePath);
            String[] newFileList = newFile.list();
            if (newFileList != null) {
                for (String child : newFileList)
                    if (!Paths.get(filePath, child).toString().contains(".magit")) {
                        newFolder.addFile(updateFilesInSystem(Paths.get(filePath, child).toString()));
                    }

                String folderSha1 = newFolder.doSha1();
                if (isObjectInRepo(folderSha1) && isFileChanged(getRootSha1(), folderSha1)) {
                    return getPrevData(folderSha1);
                } else {
                    currentObjects.put(folderSha1, newFolder);

                    newFolder.saveToObjects(folderSha1, getRootPath());

                    return getNewData(folderSha1, Paths.get(filePath));
                }
            } else {
                return null;
            }
        }
    }

    private FileDetails getNewData(String fileSha1, Path filePath) {
        String name = filePath.getFileName().toString();
        String fileType = Files.isRegularFile(filePath) ? "File" : "Folder";
        String todayStr;
        todayStr = MagitUtils.getTodayAsStr();
        return new FileDetails(name, fileSha1, fileType, currentUser, todayStr);

    }

    private FileDetails getPrevData(String sha1) {
        return findParentObjByPath(getRootSha1(), sha1);

    }

    private FileDetails findParentObjByPath(String parentSha1, String destinationSha1) {
        GitObjectsBase parent = currentObjects.get(parentSha1);
        if (parent.isFolder()) {
            Folder parentAsFolder = (Folder) parent;
            Stream<FileDetails> files = parentAsFolder.getFilesList().stream();
            List<FileDetails> det = files.filter(x -> x.getSha1().equals(destinationSha1)).collect(Collectors.toList());
            if (det.isEmpty()) {
                Stream<FileDetails> childsStream = parentAsFolder.getFilesList().stream();
                List<FileDetails> childs = childsStream.filter(x -> x.getType().
                        equals("Folder")).collect(Collectors.toList());
                for (FileDetails child : childs) {
                    return findParentObjByPath(child.getSha1(), destinationSha1);
                }
            } else {
                return det.get(0);

            }
        }
        return null;
    }

    String getCurrentCommitFullFilesData(){
        List<String> commitFiles = new LinkedList<>();
        getAllCurrentCommitDir(getRootSha1(), getRootPath(), commitFiles);
        String filesData = String.join("\n", commitFiles);
        return filesData;

    }

    private void getAllCurrentCommitDir(String rootSha1, String rootPath, List<String> commitFiles) {
        GitObjectsBase f = currentObjects.get(rootSha1);
        if (f.isFolder()) {
            Folder b = (Folder) f;
            for (FileDetails child : b.getFilesList()) {
                commitFiles.add(child.getToStringWithFullPath(rootPath));
                String name = Paths.get(rootPath, child.getFileName()).toString();
                getAllCurrentCommitDir(child.getSha1(), name, commitFiles);
            }
        }
    }

    private void changeHeadCommit(String sha1){
        Path headPath = Paths.get(BRANCHES_PATH, getActiveBranchName());
        MagitUtils.writeToFile(headPath, sha1);
    }


    // ========================== Change Repo ==========================
    private static void deleteWC(String filePath) { // need to check
        File root = new File(filePath);
        String[] files = root.list();
        if (files != null) {
            for (String f : files) {
                File childPath = new File(MagitUtils.joinPaths(filePath, f));
                if (childPath.isDirectory()) {
                    if (!childPath.getName().equals(".magit")) {
                        deleteWC(childPath.getAbsolutePath());
                    }
                } else {
                    childPath.delete();
                }
            }
        }
    }

    private void loadCommitFromSha1(String commitSha1) {
        deleteWC(getRootPath());
        Commit curCommit = (Commit) currentObjects.get(commitSha1);
        loadWCFromRoot(curCommit.getRootSha1(), getRootPath());
    }

    private void loadWCFromRoot(String sha1, String path) {
        GitObjectsBase curObj = currentObjects.get(sha1);

        if (curObj.isFolder()) {
            Folder curObjAsFolder = (Folder) curObj;

            for (FileDetails childDetails : curObjAsFolder.getFilesList()) {
                GitObjectsBase childObj = currentObjects.get(childDetails.getSha1());
                childObj.createFileFromObject(Paths.get(path, childDetails.getFileName()).toString());
                loadWCFromRoot(childDetails.getSha1(),
                        Paths.get(path, childDetails.getFileName()).toString());
            }
        }
    }

    private void updateMainPaths() {
        MAGIT_PATH = Paths.get(getRootPath(), ".magit").toString();
        BRANCHES_PATH = Paths.get(MAGIT_PATH, "Branches").toString();
        OBJECTS_PATH = Paths.get(MAGIT_PATH, "Objects").toString();
    }

    void changeRepo(String newRepo) {
        if (!isValidRepo(newRepo)) {
            System.out.println("Invalid repository!"); // Throw custom exception
        }

        setRootPath(newRepo);
        updateMainPaths();
        String newCommitSha1 = getActiveCommitSha1InBranch(getActiveBranchName());
        if (!newCommitSha1.equals("")) {
            currentCommit = (Commit) currentObjects.get(newCommitSha1);
        }
        else{
            currentCommit = null;
        }
        loadObjectsFromRepo();
    }

    private void loadObjectsFromRepo() {
        currentObjects.clear();
        try {
            String newCommitSha1 = getActiveCommitSha1InBranch(getActiveBranchName());
//            Assuming one commit was made in head branch at some point.
            while (newCommitSha1 != null && !newCommitSha1.equals("")) {
                Commit newCommit = new Commit();
                newCommit.getDataFromFile(MagitUtils.joinPaths(OBJECTS_PATH, newCommitSha1));
                if (!currentObjects.containsKey(newCommitSha1)) {
                    currentObjects.put(newCommitSha1, newCommit);
                }
                String rootSha1 = newCommit.getRootSha1();
                loadObjectsFromRootFolder(rootSha1, getRootPath());
                newCommitSha1 = newCommit.getLastCommitSha1();
            }
        } catch (Exception e) {
        }
    }

    private void loadObjectsFromRootFolder(String sha1, String path) {
        File curFile = new File(path);
        if (curFile.isDirectory()) {
            Folder newFolder = new Folder();
            newFolder.getDataFromFile(MagitUtils.joinPaths(OBJECTS_PATH, sha1));
            currentObjects.put(sha1, newFolder);
            List<FileDetails> fileDetailsList = newFolder.getFilesList();
            if (fileDetailsList != null) {
                for (FileDetails child : fileDetailsList) {
                    loadObjectsFromRootFolder(child.getSha1(), MagitUtils.joinPaths(path, child.getFileName()));
                }
            }
        } else {
            Blob newFile = new Blob();
            newFile.getDataFromFile(MagitUtils.joinPaths(OBJECTS_PATH, sha1));
            currentObjects.put(sha1, newFile);

        }
    }

    private boolean isValidRepo(String newRepo) {
        File newFile = new File(newRepo);
        String[] listOfRepoFiles = newFile.list();
        if (listOfRepoFiles != null) {
            for (String f : listOfRepoFiles) {
                if (f.contains(".magit"))
                    return true;
            }
        }
        return false;
    }

    // ========================= Branches Functions ======================
    void showAllBranchesData() {
        Path headPath = Paths.get(getRootPath(), ".magit", "Branches", "Head");
        String headBranch = MagitUtils.readFileAsString(headPath.toString());
        try {
            Path branchesPath = Paths.get(BRANCHES_PATH);
            Stream<Path> walk = walk(branchesPath);

            Set<Path> branchesNames = walk.filter(x -> x.toAbsolutePath().toString().
                    contains(BRANCHES_PATH)).filter(x -> !(x.toAbsolutePath().toString().
                    contains(headPath.toString()))).filter(Files::isRegularFile).collect(Collectors.toSet());
            for (Path currentBranch : branchesNames) {
                System.out.println("========================");
                System.out.print(String.format("Branch's name is: %s ", currentBranch.getFileName()));
                if (currentBranch.getFileName().toString().equals(headBranch)) {
                    System.out.println("----> Head");
                } else {
                    System.out.println();
                }
                String commitSha1 = MagitUtils.readFileAsString(currentBranch.toString());
                System.out.println(String.format("The commit SHA-1: %s", commitSha1));
                Commit lastComitInBranch = (Commit) currentObjects.get(commitSha1);
                System.out.println(String.format("The commit message:\n %s", lastComitInBranch.getCommitMessage()));
                System.out.println("========================");
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    void addBranch(String newBranchName) {
        if (isBranchExist(newBranchName)) {
            System.out.println("Branch already Exist!");
            return;
        }
        Path newPath = Paths.get(BRANCHES_PATH, newBranchName);
        MagitUtils.writeToFile(newPath, getActiveCommitSha1InBranch(getActiveBranchName()));
    }

    void removeBranch(String branchName) {
        String currentHeadBranch = getActiveBranchName();
        if (!isBranchExist(branchName)) {
            System.out.println("No such branch Exist!"); // TODO: change to throw custom error
        }

        if (branchName.equals(currentHeadBranch)) {
            System.out.println("Head Branch cannot be deleted"); // TODO: change to throw custom error
        } else {
            String branchToDelete = Paths.get(BRANCHES_PATH, branchName).toString();
            File f = new File(branchToDelete);
            f.delete();
        }
    }

    private String getActiveBranchName() {
        Path headPath = Paths.get(BRANCHES_PATH, "Head");
        return MagitUtils.readFileAsString(headPath.toString());
    }

    private String getActiveCommitSha1InBranch(String branchName) {
        Path currentCommit = Paths.get(BRANCHES_PATH, branchName);
        return MagitUtils.readFileAsString(currentCommit.toString());
    }

    void getHistoryBranchData() {
        String CurrentCommitSha1 = getActiveCommitSha1InBranch(getActiveBranchName());
        while (!CurrentCommitSha1.equals("") ) {
            Commit currentCommit = (Commit) currentObjects.get(CurrentCommitSha1);
            System.out.println("====================");
            System.out.println(currentCommit.exportCommitDataToString());
            System.out.println("====================");

            CurrentCommitSha1 = currentCommit.getLastCommitSha1();
        }
    }

    private boolean isBranchExist(String branchName) {
        try {
            Path branchesPath = Paths.get(BRANCHES_PATH);
            Stream<Path> walk = walk(branchesPath);

            Set<Path> branchesNames = walk.filter(x -> x.toAbsolutePath().toString().
                    contains(branchesPath.toString())).filter(x -> x.getFileName().
                    toString().equals(branchName)).collect(Collectors.toSet());
            return !(branchesNames.isEmpty());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void changeHeadBranch(String branchName) {
        Path headPath = Paths.get(BRANCHES_PATH, "Head");
        try {
            FileWriter head = new FileWriter(headPath.toString(), false);
            head.write(branchName);
            head.close();
        } catch (IOException e) {
            System.out.println("Could not change branch");
        }
    }

    private File getCurrentBranchFile() throws IOException {
        Path branchs = Paths.get(BRANCHES_PATH, "Head");
        File headFile = new File(branchs.toString());
        BufferedReader reader = new BufferedReader(new FileReader(headFile));
        String currentBranch = reader.readLine();
        reader.close();
        Path head_branch = Paths.get(getRootPath(), ".magit", "Branches", currentBranch);
        return new File(head_branch.toString());
    }

    void checkoutBranch(String newBranchName, boolean ignoreChanges) {
        if (!isBranchExist(newBranchName)) {
            System.out.println("Branch does not exist"); // TODO
        }
        boolean changesExist = isWorkingCopyIsChanged(false);
        if (changesExist && !ignoreChanges) {
            System.out.println("Open changes found"); // TODO
        }
        changeHeadBranch(newBranchName);
        String lastCommitInBranchSha1 = getActiveCommitSha1InBranch(getActiveBranchName());
        if (!lastCommitInBranchSha1.equals("")) {
            loadCommitFromSha1(lastCommitInBranchSha1);
        }
    }

    void resetCommitInBranch(String commitSha1, boolean ignore){
        if(!isValidCommit(commitSha1)){
            System.out.println("invalid Commit!");
        }

        if(isWorkingCopyIsChanged(false) && !ignore){
            System.out.println("Changed content");
        }

        changeHeadCommit(commitSha1);
        loadCommitFromSha1(commitSha1);
    }
}