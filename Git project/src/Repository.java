import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
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
    private List<Branch> currentBranchs = new LinkedList<>();
    private Commit currentCommit = null;
    private Branch currentBranch = null;
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
            return currentCommit.getRootSha1();
//        try {
//            Branch headBranch = currentBranch;
//            String currentCommitSha1 = headBranch.getCommitSha1();
//            if (currentCommitSha1 != null && !currentCommitSha1.equals("")) {
//
//                Path commitFile = Paths.get(getRootPath(), ".magit", "Objects", currentCommitSha1);
//                String commitData = MagitUtils.unZipAndReadFile(commitFile.toString());
//                if (commitData != null) {
//                    String[] commitFields = commitData.split(MagitUtils.DELIMITER);
//                    return commitFields[0];
//                }
//                return "";
//
//            }
//        }
//        catch (IOException e){return "";}

//        return "";
    }

    private String printSet(Set<String> setToPrint) {
        String finalString = "";
        for (String currentPath : setToPrint) {
            finalString = finalString.concat(currentPath);
        }
        return finalString;
    }

    WorkingCopyChanges printWCStatus() throws IOException, InvalidDataException{
        return isWorkingCopyIsChanged();
    }


    // =========================== Creating New Repo ==================================
    void createNewRepository(String newRepositoryPath)
            throws DataAlreadyExistsException, ErrorCreatingNewFileException, IOException, InvalidDataException{
        String errorMsg;
        File newFile = new File(newRepositoryPath);
        if (!newFile.exists()) {
            if(newFile.mkdirs()){
                addNewFilesToRepo(newRepositoryPath);
                changeRepo(newRepositoryPath);
            }
            else{
                errorMsg = "The system has failed to create the new directory";
                throw new ErrorCreatingNewFileException(errorMsg);
            }
        }
        else {
            errorMsg = "The repository you were trying to create already exists!";
            throw new DataAlreadyExistsException(errorMsg);
        }
    }

    private void addNewFilesToRepo(String newRepositoryPath) throws IOException, ErrorCreatingNewFileException {
        String errorMsg = "The system has failed to create the new directory";

        String magitPath = MagitUtils.joinPaths(newRepositoryPath, ".magit");
        File newFile = new File(magitPath);
        if(!newFile.mkdir()){
            throw new ErrorCreatingNewFileException(errorMsg);
        }

        newFile = new File(MagitUtils.joinPaths(newRepositoryPath, ".magit\\Branches"));
        if(!newFile.mkdir()){
            throw new ErrorCreatingNewFileException(errorMsg);
        }

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
        if(!newFile.mkdir()){
            throw new ErrorCreatingNewFileException(errorMsg);
        }
    }


    // =========================== Commit =========================================
    private boolean isValidCommit(String sha1){
        GitObjectsBase obj = currentObjects.get(sha1);
        return (obj != null && obj.isCommit());
    }

    boolean createNewCommit(String userName, String commitMsg)
            throws InvalidDataException, IOException, FileErrorException{
        String errorMsg;
        boolean isChanged;
        FileDetails rootData;
        try {
            if (isWorkingCopyIsChanged().isChanged()) {
                isChanged = true;
                currentUser = userName;
                try {
                    rootData = updateFilesInSystem(getRootPath());
                }
                catch (IOException e){
                    errorMsg = "Had an issue updating the files in the system!\n" +
                            "Error message: " + e.getMessage();
                    throw new IOException(errorMsg);
                }
                catch (FileErrorException e) {
                    throw new FileErrorException(e.getMessage());
                }

                Commit newCommit = new Commit();
                newCommit.setCommitCreator(userName);
                newCommit.setCommitDate(MagitUtils.getTodayAsStr());
                newCommit.setCommitMessage(commitMsg);

                if(rootData != null) {
                    newCommit.setRootSha1(rootData.getSha1());
                }
                else {
                    errorMsg = "Root is null - cannot get it's sha1!";
                    throw new InvalidDataException(errorMsg);
                }

                String commitSha1 = newCommit.doSha1();

                if (currentCommit != null) {
                    newCommit.setLastCommitSha1(currentBranch.getCommitSha1());
                }
                currentCommit = newCommit;
                currentObjects.put(commitSha1, newCommit);
                updateCommitInCurrentBranch(commitSha1);
                currentCommit.saveToObjects(commitSha1, rootPath);
            }
            else {
                isChanged = false;
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
        return isChanged;
    }

    private void updateCommitInCurrentBranch(String commitSha1) throws IOException {
        File headFile = new File(MagitUtils.joinPaths(BRANCHES_PATH, currentBranch.getName()));
        Writer writer = new BufferedWriter(new FileWriter(headFile));
        writer.write(commitSha1);
        writer.flush();
        writer.close();
        currentBranch.setCommitSha1(commitSha1);
    }

    private WorkingCopyChanges isWorkingCopyIsChanged() throws IOException, InvalidDataException {
        try {
            WorkingCopyChanges newChangesSet;
            boolean isChanged = false;

            Stream<Path> walk = walk(Paths.get(getRootPath()));
            Set<String> WCSet = walk.filter(x -> !x.toAbsolutePath().toString().contains(".magit")).filter
                    (Files::isRegularFile).map(Path::toString).collect(Collectors.toSet());

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

            if(!existsPath.isEmpty() || !newFiles.isEmpty() || !deletedFiles.isEmpty()){
                isChanged = true;
            }

            newChangesSet = new WorkingCopyChanges(existsPath, deletedFiles, newFiles, isChanged);
            return newChangesSet;

        } catch (IOException e) {
            String errorMsg = "Unhandled IOException!\n Exception message: " + e.getMessage();
            throw new IOException(errorMsg);
        }
    }

    private boolean isFileChanged(String rootPathSha1, String sha1FileToCheck) {
        GitObjectsBase f = currentObjects.get(rootPathSha1);

        if (f != null && f.isFolder()) {
            Folder b = (Folder) f;
            for (FileDetails child : b.getFilesList()) {
                if (child.getSha1().equals(sha1FileToCheck)) {
                    return true;
                }
                isFileChanged(child.getSha1(), sha1FileToCheck);
            }
        }

        return false;
    }

    private void getLastCommitFiles(String rootSha1, String rootPath, Map<String, String> commitFiles)
            throws InvalidDataException{
        GitObjectsBase f = currentObjects.get(rootSha1);
        String errorMsg;

        if (f == null){
            errorMsg = "Illegal root folder!";
            throw new InvalidDataException(errorMsg);
        }
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

    private void getChanged(Set<String> filesToCheck, Map<String, String> oldData) throws IOException{
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

    private FileDetails updateFilesInSystem(String filePath) throws IOException, FileErrorException{
        String errorMsg;
            try {
                if (Files.isRegularFile(Paths.get(filePath))) {
                    String content = MagitUtils.readFileAsString(filePath);
                    String currentFileSha1 = org.apache.commons.codec.digest.DigestUtils.sha1Hex(content);

                    if (isObjectInRepo(currentFileSha1) && isFileChanged(getRootSha1(), currentFileSha1)) {
                        return getPrevData(currentFileSha1);
                    }
                    else {
                        Blob newBlob = new Blob();
                        newBlob.setFileContent(content);
                        currentObjects.put(currentFileSha1, newBlob);
//                      Path newPath = Paths.get(getRootPath(), ".magit", "Objects", currentFileSha1);
                        try {
                            newBlob.saveToObjects(currentFileSha1, getRootPath());
                        }
                        catch (IOException e) {
                            errorMsg = "Had an issue saving a blob to the objects!\n" +
                                    "Error message: " + e.getMessage();
                            throw new IOException(errorMsg);
                        }
                        catch (FileErrorException e) {
                            errorMsg = "Had an issue saving a blob to the objects!\n" +
                                    "Error message: " + e.getMessage();
                            throw new FileErrorException(errorMsg);
                        }
                        return getNewData(currentFileSha1, Paths.get(filePath));
                    }

                }
                else {
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
                        }
                        else {
                            currentObjects.put(folderSha1, newFolder);
                            try {
                                newFolder.saveToObjects(folderSha1, getRootPath());
                            }
                            catch (IOException e) {
                                errorMsg = "Had an issue saving a folder to the objects!\n" +
                                        "Error message: " + e.getMessage();
                                throw new IOException(errorMsg);
                            }
                            catch (FileErrorException e) {
                                errorMsg = "Had an issue saving a folder to the objects!\n" +
                                        "Error message: " + e.getMessage();
                                throw new FileErrorException(errorMsg);
                            }
                            return getNewData(folderSha1, Paths.get(filePath));
                        }
                    }
                    else {
                        return null;
                    }
                }
            }
            catch (IOException e){
                errorMsg = "Something went wrong while reading the file as string!\n" +
                        "Error message: " + e.getMessage();
                throw new IOException(errorMsg);
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
            List<FileDetails> det = files.filter(x -> x.getSha1().equals(destinationSha1)).
                    collect(Collectors.toList());

            if (det.isEmpty()) {
                Stream<FileDetails> childsStream = parentAsFolder.getFilesList().stream();
                List<FileDetails> childs = childsStream.filter(x -> x.getType().
                        equals("Folder")).collect(Collectors.toList());
                for (FileDetails child : childs) {
                    findParentObjByPath(child.getSha1(), destinationSha1);
                }
            }
            else {
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
        if (f != null && f.isFolder()) {
            Folder b = (Folder) f;
            for (FileDetails child : b.getFilesList()) {
                commitFiles.add(child.getToStringWithFullPath(rootPath));
                String name = Paths.get(rootPath, child.getFileName()).toString();
                getAllCurrentCommitDir(child.getSha1(), name, commitFiles);
            }
        }
    }

    private void changeHeadCommit(String sha1) throws IOException{
        if (isValidCommit(sha1)) {
            Path headPath = Paths.get(BRANCHES_PATH, currentBranch.getName());
            MagitUtils.writeToFile(headPath, sha1);
            currentBranch.setCommitSha1(sha1);
        }
    }


    // ========================== Change Repo ==========================
    private static void deleteWC(String filePath) throws FileErrorException {
        File root = new File(filePath);
        String[] files = root.list();
        String errorMsg;

        if (files != null) {
            for (String f : files) {
                File childPath = new File(MagitUtils.joinPaths(filePath, f));
                if (childPath.isDirectory()) {
                    if (!childPath.getName().equals(".magit")) {
                        deleteWC(childPath.getAbsolutePath());
                    }
                } else {
                    if(!childPath.delete()){
                        errorMsg = "Had an issue deleting a file!";
                        throw new FileErrorException(errorMsg);
                    }
                }
            }
        }
    }

    private void loadCommitFromSha1(String commitSha1) throws FileErrorException, IOException{
        deleteWC(getRootPath());
        Commit curCommit = (Commit) currentObjects.get(commitSha1);
        loadWCFromRoot(curCommit.getRootSha1(), getRootPath());
    }

    private void loadWCFromRoot(String sha1, String path) throws IOException {
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

    void changeRepo(String newRepo) throws InvalidDataException, IOException, NullPointerException, ErrorCreatingNewFileException {
        String errorMsg;

        if (!isValidRepo(newRepo)) {
            errorMsg = "The repository you entered is missing the .magit library";
            throw new InvalidDataException(errorMsg);
        }

        setRootPath(newRepo);
        updateMainPaths();

        String newHead = getCurrentBranchInRepo();
        String newCommitSha1 = MagitUtils.readFileAsString(MagitUtils.joinPaths(BRANCHES_PATH, newHead));

        currentBranch = new Branch(newHead, newCommitSha1);;

        if (!newCommitSha1.equals("")) {
            if(currentObjects == null){
                errorMsg = "Current objects is empty! Cannot search a sha1 in it!";
                throw new NullPointerException(errorMsg);
            }
            else{
                currentCommit = (Commit) currentObjects.get(newCommitSha1);
            }
        }
        else{
            currentCommit = null;
        }

        loadObjectsFromRepo();
    }

    private void loadObjectsFromRepo() throws IOException, ErrorCreatingNewFileException{
        currentObjects.clear();
        currentBranchs.clear();
        loadBranchesObjectsFromBranchs();

        String newCommitSha1 = currentBranch.getCommitSha1();

//      ========================================================
//      Assuming one commit was made in head branch at some point.
//      ========================================================

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
    }

    private void loadBranchesObjectsFromBranchs () throws IOException, ErrorCreatingNewFileException{
        File branchesFolder = new File(BRANCHES_PATH);
        String[] filesInBranchesFolder = branchesFolder.list();
        String errorMsg;

        if(filesInBranchesFolder == null){
            errorMsg = "There are no files in the branches folder!";
            throw new ErrorCreatingNewFileException(errorMsg);
        }
        for (String currentBranchPath : filesInBranchesFolder) {
            if (!currentBranchPath.equals(MagitUtils.joinPaths(BRANCHES_PATH, "Head"))){
                String commitSha1 = MagitUtils.readFileAsString(MagitUtils.joinPaths(BRANCHES_PATH, currentBranchPath));
                currentBranchs.add(new Branch(commitSha1));
            }

        }
    }


    private void loadObjectsFromRootFolder(String sha1, String path) throws IOException{
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

        }
        else {
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
    String showAllBranchesData() {
        String data = "";
        for (Branch curBranch : currentBranchs) {
            data = data.concat("\n========================\n");
            data = data.concat(String.format("Branch's name is: %s ", curBranch.getName()));
            if (curBranch.getName().equals(currentBranch.getName())) {
                data = data.concat("----> Head");
            }
            String commitSha1 = curBranch.getCommitSha1();
            data = data.concat(String.format("\nThe commit SHA-1: %s", commitSha1));
            Commit lastComitInBranch = (Commit) currentObjects.get(commitSha1);
            data = data.concat(String.format("\nThe commit message:\n %s",
                    lastComitInBranch.getCommitMessage()));
            data = data.concat("\n========================\n");

        }
        return data;
    }

    void addBranch(String newBranchName) throws DataAlreadyExistsException, IOException{
        String errorMsg;
        if (isBranchExist(newBranchName)) {
            errorMsg = "Branch already Exist!";
            throw new DataAlreadyExistsException(errorMsg);
        }
        else {
            // ========================
            // add to objects in .magit
            // ========================

            Path newPath = Paths.get(BRANCHES_PATH, newBranchName);
            MagitUtils.writeToFile(newPath, currentBranch.getCommitSha1());

            // ============================
            // add to system memory objects
            // ============================
            Branch newBranchObj = new Branch(newBranchName, currentBranch.getCommitSha1());
            currentBranchs.add(newBranchObj);
        }
    }

    void removeBranch(String branchName) throws InvalidDataException, FileErrorException {
        String currentHeadBranch = currentBranch.getName();
        String errorMsg;
        if (!isBranchExist(branchName)) {
            errorMsg = "No such branch Exists!";
            throw new InvalidDataException(errorMsg);
        }

        if (branchName.equals(currentHeadBranch)) {
            errorMsg = "Head Branch cannot be deleted";
            throw new InvalidDataException(errorMsg);
        }
        else {
            // =============================
            // delete from objects in .magit
            // =============================

            String branchToDelete = Paths.get(BRANCHES_PATH, branchName).toString();
            File f = new File(branchToDelete);
            if (!f.delete()){
                errorMsg = "Had an issue while trying to delete a file!";
                throw new FileErrorException(errorMsg);
            }

            // =================================
            // delete from system memory objects
            // =================================

            Branch branchObjToDelete = getBranchByName(branchName);
            currentBranchs.remove(branchObjToDelete);
        }
    }

    private String getActiveCommitSha1InBranch(String branchName) {
        String msg;
        Branch branchObj = getBranchByName(branchName);
        if (branchObj == null) {
            msg = "There was a problem getting the active commit sha1!";
            throw new NullPointerException(msg);
        }
        return branchObj.getCommitSha1();
    }

    MagitStringResultObject getHistoryBranchData() throws Exception{
        MagitStringResultObject resultObject = new MagitStringResultObject();
        String CurrentCommitSha1 = currentBranch.getCommitSha1();

        while (CurrentCommitSha1 != null && !CurrentCommitSha1.equals("")) {
            try {
                Commit currentCommit = (Commit) currentObjects.get(CurrentCommitSha1);
                resultObject.data = resultObject.data.concat("=====================\n" +
                        currentCommit.exportCommitDataToString() + "\n=====================\n\n");

                CurrentCommitSha1 = currentCommit.getLastCommitSha1();
            }
            catch (Exception e){
                resultObject.errorMSG = "Somthing went wrong while trying to cast!";
                throw new Exception(resultObject.errorMSG);
            }
        }
        resultObject.haveError = false;
        return resultObject;
    }

    private boolean isBranchExist(String branchName) {
        for(Branch currBranch : currentBranchs){
            if(currBranch.getName().equals(branchName)){
                return true;
            }
        }
        return false;
    }

    private Branch getBranchByName(String branchName){
        for(Branch currBranch : currentBranchs){
            if(currBranch.getName().equals(branchName)){
                return currBranch;
            }
        }
        return null;
    }

    private void changeHeadBranch(String branchName) throws InvalidDataException, IOException{
        String errorMsg;
        Path headPath = Paths.get(BRANCHES_PATH, "Head");
        if (!isBranchExist(branchName)){
            errorMsg = "Branch does not exits in the repository!";
            throw new InvalidDataException(errorMsg);
        }
        else {
            try {
                FileWriter head = new FileWriter(headPath.toString(), false);
                head.write(branchName);
                head.close();
                currentBranch = getBranchByName(branchName);
            }
            catch (IOException e) {
                errorMsg = "There was an unhandled IOException! could not change head brach!";
                throw new IOException(errorMsg);
            }
        }
    }

    private String getCurrentBranchInRepo() throws IOException{
       String currentBranchInRepo;
       Path branchs = Paths.get(BRANCHES_PATH, "Head");
       File headFile = new File(branchs.toString());
       BufferedReader reader = new BufferedReader(new FileReader(headFile));
       currentBranchInRepo = reader.readLine();
       reader.close();
       return currentBranchInRepo;
    }

    void checkoutBranch(String newBranchName, boolean ignoreChanges)
            throws InvalidDataException,  DirectoryNotEmptyException, IOException, FileErrorException{
        String errorMsg;
        if (!isBranchExist(newBranchName)) {
            errorMsg = "Branch does not exist";
            throw new InvalidDataException(errorMsg);
        }

        boolean changesExist = isWorkingCopyIsChanged().isChanged();

        if (changesExist && !ignoreChanges) {
            errorMsg = "Open changes were found in the working copy!";
            throw new DirectoryNotEmptyException(errorMsg);
        }

        changeHeadBranch(newBranchName);
        String lastCommitInBranchSha1 = currentBranch.getCommitSha1();
        if (!lastCommitInBranchSha1.equals("")) {
            loadCommitFromSha1(lastCommitInBranchSha1);
        }
    }

    void resetCommitInBranch(String commitSha1, boolean ignore)
            throws InvalidDataException, IOException, FileErrorException {
        String errorMsg;
        if(!isValidCommit(commitSha1)){
            errorMsg = "Invalid commit!";
            throw new InvalidDataException(errorMsg);
        }

        if(isWorkingCopyIsChanged().isChanged() && !ignore){
            errorMsg = "There are open changes in the repository!";
            throw new DirectoryNotEmptyException(errorMsg);
        }

        changeHeadCommit(commitSha1);
        loadCommitFromSha1(commitSha1);
    }
}