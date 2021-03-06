package Engine;

import Exceptions.*;
import GitObjects.*;
import Utils.*;
import XMLHandler.*;
import Parser.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.util.StringUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import puk.team.course.magit.ancestor.finder.AncestorFinder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.nio.file.Files.walk;


class Repository {
    private String MAGIT_PATH = Paths.get(getRootPathAsString(), ".magit").toString();
    private String BRANCHES_PATH = Paths.get(MAGIT_PATH, "Branches").toString();
    private String OBJECTS_PATH = Paths.get(MAGIT_PATH, "Objects").toString();
    private StringProperty rootPath = new SimpleStringProperty("Undefined");

    private Map<String, GitObjectsBase> currentObjects = new HashMap<>();
    private List<Branch> currentBranchs = new LinkedList<>();
    private Commit currentCommit = null;

    private final Branch UNDEFINED_BRANCH = new Branch("Undefined");
    private Branch currentBranch = UNDEFINED_BRANCH;
    private ObservableList<String> currentBranchesNames = FXCollections.observableArrayList();
    private Map<String, String> sha1OfRemoteBranchInRemoteRepo = new HashMap<>();
    private Map<String, Boolean> isRemoteTrackingBranchInLocalRepo = new HashMap<>();

    private String currentUser;

    private StringProperty repoName = new SimpleStringProperty("Undefined");
    private Map<String, String> localAndRemote = new HashMap<>();
    private String remoteRepository;

//  ====================================== Utils =======================================

    StringProperty getRepoName() {
        return repoName;
    }

    String getRemoteRepoPath(String localRepoPath) {
        return localAndRemote.get(getRemotePathByLocal(localRepoPath));
    }

    String getRemotePathByLocal(String localPath){
        String smallCaseToFind = localPath.toLowerCase();
        for (String repoPath: localAndRemote.keySet()){
            if (repoPath.toLowerCase().equals(smallCaseToFind)){
                return repoPath;
            }
        }
        return null;
    }
    private boolean isObjectInRepo(String id) {
        return currentObjects.containsKey(id);
    }

    StringProperty getRootPath() {
        return rootPath;
    }

    private String getRootPathAsString() {
        if (rootPath != null){
            return rootPath.getValue();
        }
        return null;
    }

    private void setRootPath(String path) {
        this.rootPath.setValue(path);
    }

    private String getRootSha1() { // goes to branches->head x then branches->x->lastCommit y and then objects->y
            return currentCommit.getRootSha1();
    }

    private void setRepoName(String name){
        repoName.setValue(name);
    }

    Branch getCurrentBranch(){
        return currentBranch;
    }

    Commit getCurrentCommit() {
        return currentCommit;
    }

    WorkingCopyChanges printWCStatus() throws IOException, InvalidDataException {
        return isWorkingCopyIsChanged();
    }

    ObservableList<String> getCurrentBranchesNames() {
        return currentBranchesNames;
    }

    String findBranchBySha1(String sha1) {
        if(!sha1OfRemoteBranchInRemoteRepo.isEmpty()) {
            return sha1OfRemoteBranchInRemoteRepo.get(sha1);
        }
        return null;
    }

    private Branch getCurrentBranchInGivenRepo(String path) {
        try {

            String head = MagitUtils.joinPaths(path, ".magit", "Branches", "Head");
            String headContent = MagitUtils.readFileAsString(head);

            return getBranchByName(headContent);
        } catch (IOException e) {
            return null;
        }
    }

    private Branch getGivenBranchInGivenRepo(String branchName, String path) {
        try {
            String branchPath = MagitUtils.joinPaths(path, ".magit", "Branches", branchName);
            String branchContent = MagitUtils.readFileAsString(branchPath);
            return new Branch(branchName, branchContent);
        } catch (IOException e) {
            return null;
        }
    }
    private Commit getCurrentCommitInGivenRepo(String path){
        try {
            Branch head = getCurrentBranchInGivenRepo(path);
            if (head != null) {
                Commit commit = new Commit();
                commit.getDataFromFile(MagitUtils.joinPaths(
                        path, ".magit", "Objects", head.getCommitSha1()));
                return commit;
            } else {
                return null;
            }
        }
        catch (IOException e) {
            return null;
        }
    }

    private String getRootPathSha1OfGivenRepo(String path) throws IOException {
        Commit commit = getCurrentCommitInGivenRepo(path);
        return commit.getRootSha1();

    }

//  =========================== Creating New Repo ==================================
    void createNewRepository(String newRepositoryPath, String repoName, boolean addMaster)
            throws DataAlreadyExistsException, ErrorCreatingNewFileException, IOException {
        String errorMsg;
        //patch for big C instead of c.
        newRepositoryPath = newRepositoryPath.replace("C:", "c:");

        File newFile = new File(newRepositoryPath);
        if (newFile.exists()) {
            if (!isValidRepo(newRepositoryPath)) {
                addNewFilesToRepo(newRepositoryPath, addMaster);
            }
            else {
                errorMsg = "The repository you were trying to create already exists!";
                throw new DataAlreadyExistsException(errorMsg);
            }
        }
        else{
            if(newFile.mkdirs()){
                addNewFilesToRepo(newRepositoryPath, addMaster);
            }
            else{
                errorMsg = "The system has failed to create the new directory";
                throw new ErrorCreatingNewFileException(errorMsg);
            }
        }

    }

    private void addNewFilesToRepo(String newRepositoryPath, boolean addMaster) throws IOException, ErrorCreatingNewFileException {
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
        if (addMaster){
            out1.write("master");
        }
        else{
            out1.write("");
        }
        out1.close();

        if (addMaster) {
            HeadPath = Paths.get(MagitUtils.joinPaths(newRepositoryPath, ".magit\\Branches\\master"));
            out1 = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(HeadPath.toString()), StandardCharsets.UTF_8));
            out1.write("");
            out1.close();
        }

        newFile = new File(MagitUtils.joinPaths(newRepositoryPath, ".magit\\Objects"));
        if(!newFile.mkdir()){
            throw new ErrorCreatingNewFileException(errorMsg);
        }
    }

//  ====================== loading Repo from XML ==============================

    void loadRepoFromXML(String repoXMLPath, boolean toDeleteExistingRepo)
            throws DataAlreadyExistsException, ErrorCreatingNewFileException,
            IOException, InvalidDataException, FileErrorException, JAXBException {
        String errorMsg;
        String currentRepo = getRootPathAsString();

        try {
            File file = new File(repoXMLPath);
            JAXBContext jaxbContext = JAXBContext.newInstance("Parser");
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            MagitRepository newMagitRepo = (MagitRepository) jaxbUnmarshaller.unmarshal(file);
            String localRepoName = newMagitRepo.getName();


            if (MagitUtils.isRepositoryExist(newMagitRepo.getLocation()) && !toDeleteExistingRepo){
                errorMsg = "The repository already exists in the system!";
                throw new DataAlreadyExistsException(errorMsg);
            }

            if (MagitUtils.isRepositoryExist(newMagitRepo.getLocation()) && toDeleteExistingRepo){
                deleteWC(newMagitRepo.getLocation(), toDeleteExistingRepo);
                File rootFile = new File(newMagitRepo.getLocation());
                rootFile.delete();
            }

            XMLValidator validation = new XMLValidator(newMagitRepo, repoXMLPath);
            XMLValidationResult validationResult = validation.StartChecking();

            if(validationResult.isValid()) {
                currentBranchs.clear();
                currentObjects.clear();
                currentBranchesNames.clear();
                createNewRepository(newMagitRepo.getLocation(),localRepoName, false);
                setRootPath(newMagitRepo.getLocation());
                updateMainPaths();
                loadBranchesDataFromMagitRepository(newMagitRepo);
                setRepoName(localRepoName);
                MagitRepository.MagitRemoteReference remoteRepo = newMagitRepo.getMagitRemoteReference();
                if(remoteRepo != null){
                    localAndRemote.put(Paths.get(newMagitRepo.getLocation()).toString(),remoteRepo.getLocation());
                    fetch(remoteRepo.getName());
                }

            }
            else {
                throw new InvalidDataException(validationResult.getMessage());
            }
        }
        catch (JAXBException e) {
            errorMsg = "Error while trying to load a repository from xml file!\n" +
                    "Error msg: " + e.getMessage();
            throw new JAXBException(errorMsg);
        }
        catch (DataAlreadyExistsException e){
            throw e;
        }
        catch (Exception e){
            if(currentRepo.equals("Undefined")) {
                errorMsg = "Something went wrong while trying to load a new repository from XML file!\n" +
                        "Error message: " + e.getMessage();
                throw new FileErrorException(errorMsg);
            }
            changeRepo(currentRepo, repoName.getValue());
        }
    }

    private void loadBranchesDataFromMagitRepository(MagitRepository repoFromXML)
            throws IOException, FileErrorException, InvalidDataException {
        String errorMsg;
        if(XMLUtils.isEmptyRepo(repoFromXML)){//TODO: add head
            return;
        }
        String head = repoFromXML.getMagitBranches().getHead();
        List<MagitSingleBranch> localBranches = repoFromXML.getMagitBranches().getMagitSingleBranch().stream()
                .filter(x-> !x.isIsRemote()).collect(Collectors.toList());
        for (MagitSingleBranch curBranch : localBranches){
            String commitId = curBranch.getPointedCommit().getId();
            MagitSingleCommit relevantCommit = XMLUtils.getMagitSingleCommitByID(repoFromXML, commitId);

            if (relevantCommit == null) {
                errorMsg = "Illegal branch!";
                throw new InvalidDataException(errorMsg);
            }

            Commit newCommit = loadCommitFromMagitSingleCommit(repoFromXML, relevantCommit);

            if (newCommit == null) {
                errorMsg = "Couldn't load the commit!";
                throw new FileErrorException(errorMsg);
            }
            else{
                String commitSha1 = newCommit.doSha1();

                // ====================
                // add commit to .magit
                // ====================
                newCommit.saveToMagitObjects(commitSha1, getRootPathAsString());

                // ====================
                // add commit to memory
                // ====================
                currentObjects.put(commitSha1, newCommit);

                if(curBranch.isTracking()) {
                    isRemoteTrackingBranchInLocalRepo.put(curBranch.getName(), true);
                }


                if(curBranch.getName().equals(head)){
                    currentBranch.setName(curBranch.getName());
                }
                addBranchToFileSystem(curBranch.getName(), commitSha1);
            }

            if (curBranch.getName().equals((head))) {
                currentBranch = getBranchByName(curBranch.getName());
                changeHeadBranch(curBranch.getName());
                if (currentBranch == UNDEFINED_BRANCH) {
                    errorMsg = "Error while trying to create the new branch!";
                    throw new FileErrorException(errorMsg);
                }
                GitObjectsBase BranchCommit = currentObjects.get(currentBranch.getCommitSha1());

                if (BranchCommit != null) {
                    currentCommit = (Commit) BranchCommit;
                    loadWCFromCommitSha1(currentBranch.getCommitSha1(), null);
                }
                else {
                    currentCommit = null;
                }
            }
        }
    }

    private Commit loadCommitFromMagitSingleCommit(MagitRepository repoFromXML, MagitSingleCommit commitToLoad)
            throws IOException, FileErrorException, InvalidDataException {
        String errorMsg;

        Commit newCommit = new Commit();
        newCommit.setCommitDate(commitToLoad.getDateOfCreation());
        newCommit.setCommitCreator(commitToLoad.getAuthor());
        newCommit.setCommitMessage(commitToLoad.getMessage());

        if(commitToLoad.getPrecedingCommits() != null) {
            List<PrecedingCommits.PrecedingCommit> historyCommits = commitToLoad.getPrecedingCommits().getPrecedingCommit();

            if (historyCommits.size() > 0) {
                String firstCommitId = historyCommits.get(0).getId();
                MagitSingleCommit secondLastCommit = null;
                newCommit.setSecondPrecedingSha1(null);
                if(historyCommits.size() == 2) {
                    String secondCommitId = historyCommits.get(1).getId();
                    secondLastCommit = XMLUtils.getMagitSingleCommitByID(repoFromXML, secondCommitId);
                }
                MagitSingleCommit lastCommit = XMLUtils.getMagitSingleCommitByID(repoFromXML, firstCommitId);
                if (lastCommit != null && !lastCommit.equals("")) {
                    String commitSha1 = loadCommitFromMagitSingleCommit(repoFromXML, lastCommit).doSha1();
                    if(secondLastCommit != null) {
                        String secondCommitSha1 = loadCommitFromMagitSingleCommit(repoFromXML, secondLastCommit).doSha1();
                        if(secondCommitSha1 != null) {
                            newCommit.setSecondPrecedingSha1(secondCommitSha1);
                        }
                    }
                    if (commitSha1 != null) {
                        newCommit.setFirstPrecedingSha1(commitSha1);
                    }
                }
            }
        }
        String rootFolderId = commitToLoad.getRootFolder().getId();

        if (rootFolderId == null) {
            errorMsg = "Illegal commit!";
            throw new InvalidDataException(errorMsg);
        }
        else {
            MagitSingleFolder relevantFolder = XMLUtils.getMagitFolderByID(repoFromXML, rootFolderId);

            if (relevantFolder == null) {
                errorMsg = String.format("Folder id %s could not be found", rootFolderId);
                throw new InvalidDataException(errorMsg);
            }
            if (!relevantFolder.isIsRoot()) {
                errorMsg = String.format("throw Folder id %s should be root folder", rootFolderId);
                throw new InvalidDataException(errorMsg);
            }

            Sha1Obj rootSha1 = new Sha1Obj();
            loadRootFromMagitSingleFolder(repoFromXML, relevantFolder, rootSha1);
            newCommit.setRootSha1(rootSha1.sha1String);
            String commitSha1 = newCommit.doSha1();
            currentObjects.put(commitSha1, newCommit);
            newCommit.saveToMagitObjects(commitSha1, getRootPathAsString());
        }
        return newCommit;
    }

    private void loadRootFromMagitSingleFolder(MagitRepository repoFromXML,
                                               MagitSingleFolder relevantFolder, Sha1Obj rootSha1)
            throws IOException, FileErrorException, InvalidDataException {
        String errorMsg;
        Folder currentFolder = new Folder();
        for (Item f : relevantFolder.getItems().getItem()) {
            if (f.getType().equals("blob")) {
                MagitBlob newMagitBlob = XMLUtils.getMagitBlobByID(repoFromXML, f.getId());
                Blob newBlob = new Blob();

                if(newMagitBlob == null){
                    errorMsg = "Commit XML issue - file doesn't exists!";
                    throw new InvalidDataException(errorMsg);
                }

                newBlob.setFileContent(newMagitBlob.getContent());
                FileDetails blobDetails = getDataFromMagitBlob(newMagitBlob);
                String blobSha1 = newBlob.doSha1();
                blobDetails.setSha1(blobSha1);

                // ========================
                // add to objects in system memory
                // ========================
                currentObjects.put(blobSha1, newBlob);

                // ========================
                // add to objects in .magit
                // ========================
                newBlob.saveToMagitObjects(blobSha1, getRootPathAsString());

                // ========================
                // add to containing folder object
                // ========================
                currentFolder.addFile(blobDetails);
            }
            else {
                MagitSingleFolder newMagitFolder = XMLUtils.getMagitFolderByID(repoFromXML, f.getId());

                if(newMagitFolder == null) {
                    errorMsg = "Commit XML issue - folder doesn't exists!";
                    throw new InvalidDataException(errorMsg);
                }

                FileDetails folderDetails = getDataFromMagitFolder(newMagitFolder);
                loadRootFromMagitSingleFolder(repoFromXML, newMagitFolder, rootSha1);
                folderDetails.setSha1(rootSha1.sha1String);
                currentFolder.addFile(folderDetails);
            }
        }
        String currentFolderSha1 = currentFolder.doSha1();

        // ========================
        // add to objects in system memory
        // ========================
        currentObjects.put(currentFolderSha1, currentFolder);

        // ========================
        // add to objects in .magit
        // ========================
        currentFolder.saveToMagitObjects(currentFolderSha1, getRootPathAsString());

        rootSha1.sha1String = currentFolderSha1;
    }

    private FileDetails getDataFromMagitBlob(MagitBlob relevantFile) {
        return new FileDetails(relevantFile.getName(), null, "File",
                relevantFile.getLastUpdater(), relevantFile.getLastUpdateDate());

    }

    private FileDetails getDataFromMagitFolder(MagitSingleFolder relevantFolder) {
        String name = relevantFolder.isIsRoot()? "": relevantFolder.getName();

        return new FileDetails(name, null, "Folder",
                relevantFolder.getLastUpdater(), relevantFolder.getLastUpdateDate());

    }


    // ========================== Change Repo ==========================

    void changeRepo(String newRepo, String repoName) throws InvalidDataException, IOException, NullPointerException,
            ErrorCreatingNewFileException {
        String errorMsg;

        if (!isValidRepo(newRepo)) {
            errorMsg = "The repository you entered is missing the .magit library";
            throw new InvalidDataException(errorMsg);
        }

        if(!localAndRemote.isEmpty()) {
            remoteRepository = getRemoteRepoPath(newRepo);
        }
        // patch for windows choosing file..
        setRootPath(newRepo);
        updateMainPaths();
        loadObjectsFromRepo();
        String newHead = getCurrentBranchInRepo();
        Branch headBranch = getBranchByName(newHead);
        currentBranch = headBranch;
        if (headBranch != null) {
            String newCommitSha1 = headBranch.getCommitSha1();
            currentCommit = (Commit) currentObjects.get(newCommitSha1);
        }
        setRepoName(repoName);

    }

    private static void deleteWC(String filePath, boolean deleteMagit) throws FileErrorException {
        File root = new File(filePath);
        String[] files = root.list();
        String errorMsg;

        if (files != null) {
            for (String f : files) {
                File childPath = new File(MagitUtils.joinPaths(filePath, f));
                if (childPath.isDirectory()) {
                    if (!childPath.getName().equals(".magit") || deleteMagit) {
                        deleteWC(childPath.getAbsolutePath(), deleteMagit);
                        if (!childPath.delete()) {
                            errorMsg = "Had an issue deleting a file!";
                            throw new FileErrorException(errorMsg);
                        }
                    }
                }
                else {
                    if (!childPath.delete()) {
                        errorMsg = "Had an issue deleting a file!";
                        throw new FileErrorException(errorMsg);
                    }
                }
            }
        }
    }

    private void loadWCFromCommitSha1(String commitSha1, String path) throws FileErrorException, IOException{
        if(path!= null) {
            deleteWC(path, false);
        }
        else {
            deleteWC(getRootPathAsString(), false);
        }
        Commit curCommit = (Commit) currentObjects.get(commitSha1);
        if(path != null) {
            loadWCFromRoot(curCommit.getRootSha1(), path);
        }
        else {
            loadWCFromRoot(curCommit.getRootSha1(), getRootPathAsString());
        }
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
        String rootPathAsString = getRootPathAsString();
        if(rootPathAsString != null) {
            MAGIT_PATH = Paths.get(getRootPathAsString(), ".magit").toString();
            BRANCHES_PATH = Paths.get(MAGIT_PATH, "Branches").toString();
            OBJECTS_PATH = Paths.get(MAGIT_PATH, "Objects").toString();
        }
    }

    private void loadObjectsFromRepo() throws IOException, ErrorCreatingNewFileException{
        currentObjects.clear();
        currentBranchs.clear();
        currentBranchesNames.clear();
        loadBranchesObjectsFromBranchs();

//      ========================================================
//      Assuming one commit was made in head branch at some point.
//      ========================================================
        for (Branch curBranch : currentBranchs) {
            String newCommitSha1 = curBranch.getCommitSha1();

            while (newCommitSha1 != null && !newCommitSha1.equals("")) {
                Commit newCommit = new Commit();
                newCommit.getDataFromFile(MagitUtils.joinPaths(OBJECTS_PATH, newCommitSha1));

                if (!currentObjects.containsKey(newCommitSha1)) {
                    currentObjects.put(newCommitSha1, newCommit);
                }

                String rootSha1 = newCommit.getRootSha1();
                loadObjectsFromRootFolder(rootSha1, getRootPathAsString());
                newCommitSha1 = newCommit.getFirstPrecedingSha1();
            }
        }
    }

    private void loadBranchesObjectsFromBranchs () throws IOException, ErrorCreatingNewFileException{
        String branchesPath = BRANCHES_PATH;
        File branchesFolder = new File(branchesPath);
        String[] filesInBranchesFolder = branchesFolder.list();
        String errorMsg;

        if(filesInBranchesFolder == null){
            errorMsg = "There are no files in the branches folder!";
            throw new ErrorCreatingNewFileException(errorMsg);
        }
        for (String currentBranchPath : filesInBranchesFolder) {
            if (new File(MagitUtils.joinPaths(branchesPath, currentBranchPath)).isFile()) {
                if (!currentBranchPath.equals("Head")) {
                    String commitSha1 = MagitUtils.readFileAsString(MagitUtils.joinPaths(branchesPath, currentBranchPath));
                    Branch newBranch = new Branch(currentBranchPath, commitSha1);
                    currentBranchs.add(newBranch);
                    if (newBranch.getName().getValue().equals(getCurrentBranchInRepo()) ||
                            newBranch.getName().getValue().equals(currentBranch.getName().getValue())) {
                        currentBranchesNames.add(newBranch.getName().getValue().concat(" (Head)"));
                    } else {
                        currentBranchesNames.add(newBranch.getName().getValue());
                    }
                }
            }
        }
    }

    private void loadObjectsFromRootFolder(String sha1, String path) throws IOException{
        File curFile = new File(path);
        if (curFile.isDirectory()) {
            Folder newFolder = new Folder();
            newFolder.getDataFromFile(MagitUtils.joinPaths(OBJECTS_PATH, sha1));
            if (!currentObjects.containsKey(sha1)) {
                currentObjects.put(sha1, newFolder);
            }
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

    boolean isValidRepo(String newRepo) {
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

    ObservableList<Branch.BrancheData> showAllBranchesData() {
        ObservableList<Branch.BrancheData> branchDataList = FXCollections.observableArrayList();
        Branch.BrancheData data;
        for (Branch curBranch : currentBranchs) {
            data = curBranch.getBranchData();
            if (curBranch.getName().equals(currentBranch.getName())) {
                data.setHead(true);
            }
            String commitSha1 = curBranch.getCommitSha1();
            if (!commitSha1.equals("")){ // only for new repo without master commit option
                Commit lastComitInBranch = (Commit) currentObjects.get(commitSha1);
                data.setCommitMsg(lastComitInBranch.getCommitMessage());
            }
            branchDataList.add(data);
        }
        return branchDataList;
    }

    void addBranch(String newBranchName, String sha1, boolean isRemoteTracking)
            throws DataAlreadyExistsException, IOException, InvalidDataException {
        String errorMsg;
        if (rootPath == null) {
            errorMsg = "repository is not configured ";
            throw new InvalidDataException(errorMsg);
        }
        if (isBranchExist(newBranchName)) {
            errorMsg = "Branch already Exist!";
            throw new DataAlreadyExistsException(errorMsg);
        } else {
            addBranchToFileSystem(newBranchName, sha1);
            if(isRemoteTracking) {
                isRemoteTrackingBranchInLocalRepo.put(Paths.get(BRANCHES_PATH, newBranchName).toString(), true);
            }
        }
    }

    private void addBranchToFileSystem(String newBranchName, String commitSha1) throws IOException{
        // =============================
        // add to objects in .magit
        // =============================
        Path newPath = Paths.get(BRANCHES_PATH, newBranchName);
        MagitUtils.writeToFile(newPath, commitSha1);

        // =================================
        // add to system memory objects
        // =================================
        Branch newBranchObj = new Branch(newBranchName, commitSha1);
        currentBranchs.add(newBranchObj);
        if (newBranchObj.getName().getValue().equals(getCurrentBranchInRepo()) ||
                newBranchObj.getName().getValue().equals(currentBranch.getName().getValue())) {
            currentBranchesNames.add(newBranchObj.getName().getValue().concat(" (Head)"));
        }
        else {
            currentBranchesNames.add(newBranchObj.getName().getValue());
        }
    }

    void removeBranch(String branchName) throws InvalidDataException, FileErrorException {
        String errorMsg;
        if (!isBranchExist(branchName)) {
            errorMsg = "No such branch Exists!";
            throw new InvalidDataException(errorMsg);
        }
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
        if (branchObjToDelete != null){
            currentBranchesNames.remove(branchObjToDelete.getName().getValue());
        }
    }

    MagitStringResultObject getHistoryBranchData() throws Exception{
        MagitStringResultObject resultObject = new MagitStringResultObject();
        String currentCommitSha1 = currentBranch.getCommitSha1();

        while (currentCommitSha1 != null && !currentCommitSha1.equals("")) {
            try {
                Commit currentCommit = (Commit) currentObjects.get(currentCommitSha1);
                resultObject.setData(resultObject.getData().concat("=====================\n" +
                        currentCommit.exportCommitDataToString(currentCommitSha1) + "\n=====================\n\n"));

                currentCommitSha1 = currentCommit.getFirstPrecedingSha1();
            }
            catch (Exception e){
                resultObject.setErrorMSG("Something went wrong while trying to cast!");
                throw new Exception(resultObject.getErrorMSG());
            }
        }
        resultObject.setIsHasError(false);
        return resultObject;
    }

    private boolean isBranchExist(String branchName) {
        for(Branch currBranch : currentBranchs){
            if(currBranch.getName().getValue().equals(branchName)){
                return true;
            }
        }
        return false;
    }

    Branch getBranchByName(String branchName){
        for(Branch currBranch : currentBranchs){
            if(currBranch.getName().getValue().equals(branchName)){
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
                errorMsg = "There was an unhandled IOException! could not change head branch!";
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
            throws InvalidDataException, IOException, FileErrorException{
        String errorMsg;

        boolean changesExist = isWorkingCopyIsChanged().isChanged();

        if (changesExist && !ignoreChanges) {
            errorMsg = "Open changes were found in the working copy!";
            throw new DirectoryNotEmptyException(errorMsg);
        }

        changeHeadBranch(newBranchName);
        String lastCommitInBranchSha1 = currentBranch.getCommitSha1();
        if (!lastCommitInBranchSha1.equals("")) {
            currentCommit = (Commit)currentObjects.get(lastCommitInBranchSha1);
            loadWCFromCommitSha1(lastCommitInBranchSha1, null);
        }
        else{ // No commit was never done, meaning this still is an empty repo.
            deleteWC(getRootPathAsString(), false);
            currentCommit = null;
        }

        for(int i=0; i < currentBranchesNames.size(); i++){
            if(currentBranchesNames.get(i).contains("(Head)")) {
                String[] splitName = currentBranchesNames.get(i).split(" ");
                currentBranchesNames.set(i, splitName[0]);
            }
            if(currentBranchesNames.get(i).contains(newBranchName)){
                currentBranchesNames.set(i, newBranchName + " (Head)");
            }
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
        loadWCFromCommitSha1(commitSha1, null);
    }

    boolean isRemote(String sha1) {
        if(sha1OfRemoteBranchInRemoteRepo.isEmpty()) {
            String branch = sha1OfRemoteBranchInRemoteRepo.get(sha1);
            return branch != null;
        }
        return false;
    }

    // =========================== Commit =========================================
    private boolean isValidCommit(String sha1){
        GitObjectsBase obj = currentObjects.get(sha1);
        return (obj != null && obj.isCommit());
    }

    boolean createNewCommit(String userName, String commitMsg, String secondCommitSha1)
            throws InvalidDataException, IOException, FileErrorException{
        String errorMsg;
        boolean isChanged;
        FileDetails rootData;
        try {
            if (isWorkingCopyIsChanged().isChanged()) {
                isChanged = true;
                currentUser = userName;
                try {
                    Map <String, String> commitData = new HashMap<>();
                    if (currentCommit!= null){
                        getAllCurrentCommitDirWithSha1(getRootSha1(), getRootPathAsString(), commitData);
                    }
                    rootData = updateFilesInSystem(getRootPathAsString(), commitData);
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
                    newCommit.setFirstPrecedingSha1(currentBranch.getCommitSha1());
                }
                if(secondCommitSha1 != null) {
                    newCommit.setSecondPrecedingSha1(secondCommitSha1);
                }
                currentCommit = newCommit;
                currentObjects.put(commitSha1, newCommit);
                updateCommitInCurrentBranch(commitSha1);
                currentCommit.saveToMagitObjects(commitSha1, rootPath.getValue());
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
        File headFile = new File(MagitUtils.joinPaths(BRANCHES_PATH, currentBranch.getName().getValue()));
        Writer writer = new BufferedWriter(new FileWriter(headFile));
        writer.write(commitSha1);
        writer.flush();
        writer.close();
        currentBranch.setCommitSha1(commitSha1);
        Branch branchToAddCommitTo = getBranchByName(currentBranch.getName().getValue());
        if (branchToAddCommitTo != null) {
            branchToAddCommitTo.setCommitSha1(commitSha1);
        }
    }

    private WorkingCopyChanges isWorkingCopyIsChanged() throws IOException, InvalidDataException {
        return isGivenWorkingCopyIsChanged(rootPath.getValue());
    }

    private WorkingCopyChanges isGivenWorkingCopyIsChanged(String path) throws IOException, InvalidDataException {
        try {
            WorkingCopyChanges newChangesSet;
            boolean isChanged = false;

            Stream<Path> walk = walk(Paths.get(path));
            Set<String> WCSet = walk.filter(x -> !x.toAbsolutePath().toString().contains(".magit")).filter
                    (Files::isRegularFile).map(Path::toString).collect(Collectors.toSet());


            Map<String, String> lastCommitFiles = new HashMap<>();
            Commit curCommit = getCurrentCommitInGivenRepo(path);
            if ( curCommit!= null && !curCommit.isEmpty()) {
                getLastCommitFiles(getRootPathSha1OfGivenRepo(path), path, lastCommitFiles);
            }

            Set<String> existsPath = new HashSet<>(WCSet);
            existsPath.retainAll(lastCommitFiles.keySet());

            Set<String> newFiles = new HashSet<>(WCSet);
            newFiles.removeAll(lastCommitFiles.keySet());

            Set<String> deletedFiles = new HashSet<>(lastCommitFiles.keySet());
            deletedFiles.removeAll(WCSet);

            getChanged(existsPath, lastCommitFiles);

            if (!existsPath.isEmpty() || !newFiles.isEmpty() || !deletedFiles.isEmpty()) {
                isChanged = true;
            }

            newChangesSet = new WorkingCopyChanges(existsPath, deletedFiles, newFiles, isChanged);
            return newChangesSet;

        } catch (IOException e) {
            String errorMsg = "Unhandled IOException!\n Exception message: " + e.getMessage();
            throw new IOException(errorMsg);
        }
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

    private FileDetails updateFilesInSystem(String filePath, Map <String, String> commitData)
            throws IOException, FileErrorException{
        String errorMsg;
        try {
            if (Files.isRegularFile(Paths.get(filePath))) {
                String content = MagitUtils.readFileAsString(filePath);
                String currentFileSha1 = org.apache.commons.codec.digest.DigestUtils.sha1Hex(content);

                if (isObjectInRepo(currentFileSha1) && isFileNotChanged(currentFileSha1, filePath, commitData)) {
                    return getPrevData(currentFileSha1);
                }
                else {
                    Blob newBlob = new Blob();
                    newBlob.setFileContent(content);
                    currentObjects.put(currentFileSha1, newBlob);
                    try {
                        newBlob.saveToMagitObjects(currentFileSha1, getRootPathAsString());
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
                            newFolder.addFile(updateFilesInSystem(Paths.get(filePath, child).toString(), commitData));
                        }

                    String folderSha1 = newFolder.doSha1();

                    if (isObjectInRepo(folderSha1) && isFileNotChanged(folderSha1, filePath, commitData)) {
                        return getPrevData(folderSha1);
                    }
                    else {
                        currentObjects.put(folderSha1, newFolder);
                        try {
                            newFolder.saveToMagitObjects(folderSha1, getRootPathAsString());
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
                    FileDetails subdet = findParentObjByPath(child.getSha1(), destinationSha1);
                    if (subdet != null) {
                        return subdet;
                    }
                }
            }
            else {
                return det.get(0);
            }
        }
        return null;
    }

    List<String> getCurrentCommitFullFilesData(){
        List<String> commitFiles = new LinkedList<>();
        getAllCurrentCommitDir(getRootSha1(), getRootPathAsString()
                , commitFiles);
        return commitFiles;

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

    private void getAllCurrentCommitDirWithSha1(String rootSha1, String rootPath, Map<String, String> commitFiles) {
        GitObjectsBase f = currentObjects.get(rootSha1);
        if (f != null && f.isFolder()) {
            Folder b = (Folder) f;
            for (FileDetails child : b.getFilesList()) {
                String name = Paths.get(rootPath, child.getFileName()).toString();
                commitFiles.put(child.getSha1(), name);
                getAllCurrentCommitDirWithSha1(child.getSha1(), name, commitFiles);
            }
        }
    }

    private boolean isFileNotChanged(String sha1ToCheck, String pathToCheck, Map<String, String> commitFiles) {
        return commitFiles.containsKey(sha1ToCheck) && pathToCheck.equals(commitFiles.get(sha1ToCheck));
    }

    private void changeHeadCommit(String sha1) throws IOException{
        if (isValidCommit(sha1)) {
            Path headPath = Paths.get(BRANCHES_PATH, currentBranch.getName().getValue());
            MagitUtils.writeToFile(headPath, sha1);
            currentBranch.setCommitSha1(sha1);
        }
    }

    List<Commit.CommitData> currentCommits() {
        List<Commit.CommitData> commits = new LinkedList<>();
        for(String sha1 : currentObjects.keySet()){
            GitObjectsBase currentObj = currentObjects.get(sha1);
            if (currentObj.isCommit()){
                Commit curCommit = (Commit) currentObj;
                if(currentBranch.getCommitSha1().equals(curCommit.doSha1()))
                {
                    commits.add(Commit.getCommitData(sha1, curCommit, currentBranch.getName().getValue()));
                }
                else {
                    commits.add(Commit.getCommitData(sha1, curCommit, null));
                }
            }
        }

        commits.sort((o1, o2) -> {
            try {
                return -o1.getCommitDateAsDate().compareTo(o2.getCommitDateAsDate());
            }catch(ParseException e){
                e.getMessage();
            }
            return 0;
        });
        return commits;
    }

    // =========================== Merge =========================================

    String merge(Branch branchToMerge, List<MergeResult> mergeResultList)
            throws InvalidDataException, IOException, FileErrorException, DataAlreadyExistsException{
        try {
            boolean changesExist = isWorkingCopyIsChanged().isChanged();
            if (!changesExist) {
                AncestorFinder ancestorFinder = new AncestorFinder(this::sha1ToCommit);
                String ancestorSha1 = ancestorFinder.traceAncestor(currentBranch.getCommitSha1(), branchToMerge.getCommitSha1());
                Commit ancestor = (Commit) currentObjects.get(ancestorSha1);
                Commit firstCommit = (Commit) currentObjects.get(currentBranch.getCommitSha1());
                Commit secondCommit = (Commit) currentObjects.get(branchToMerge.getCommitSha1());

                String fastForwardMerge = findIfFFMerge(branchToMerge, ancestor.getSha1());

                if (fastForwardMerge == null) {
                    Map<String, String> firstCommitFiles = makeMap(firstCommit);
                    Map<String, String> secondCommitFiles = makeMap(secondCommit);
                    Map<String, String> ancestorCommitFiles = makeMap(ancestor);

                    Set<String> allCommitsFiles = new HashSet<>();
                    allCommitsFiles.addAll(firstCommitFiles.keySet());
                    allCommitsFiles.addAll(secondCommitFiles.keySet());
                    allCommitsFiles.addAll(ancestorCommitFiles.keySet());

                    for (String curr : allCommitsFiles) {
                        String fileSha1InFirstCommit = "";
                        String fileSha1InSecondCommit = "";
                        String fileSha1InAncestorCommit = "";

                        for (String file1 : firstCommitFiles.keySet()) {
                            if (curr.equals(file1)) {
                                fileSha1InFirstCommit = firstCommitFiles.get(file1);
                            }
                        }

                        for (String file2 : secondCommitFiles.keySet()) {
                            if (curr.equals(file2)) {
                                fileSha1InSecondCommit = secondCommitFiles.get(file2);
                            }
                        }

                        for (String file3 : ancestorCommitFiles.keySet()) {
                            if (curr.equals(file3)) {
                                fileSha1InAncestorCommit = ancestorCommitFiles.get(file3);
                            }
                        }

                        FileToMerge fileToMerge = new FileToMerge(curr, fileSha1InFirstCommit,
                                fileSha1InSecondCommit, fileSha1InAncestorCommit);

                        Optional<FindCurrentFileState> currMergeCase = findMergeCase(fileToMerge);


                        isFileDirectoryInCurrentBranch(curr);


                        if (currMergeCase.isPresent()) {
                            FindCurrentFileState state = currMergeCase.get();
                            MergeResult mergeResult = new MergeResult();
                            state.merge(curr, mergeResult, currentBranch.getName().getValue(),
                                    branchToMerge.getName().getValue(), OBJECTS_PATH,
                                    fileSha1InFirstCommit, fileSha1InSecondCommit, fileSha1InAncestorCommit);

                            String firstContent = "";
                            String secondContent = "";
                            String ancestorContent = "";

                            if (!fileSha1InFirstCommit.equals("")) {
                                firstContent = MagitUtils.unZipAndReadFile
                                        (MagitUtils.joinPaths(OBJECTS_PATH, fileSha1InFirstCommit));
                            }

                            if (!fileSha1InSecondCommit.equals("")) {
                                secondContent = MagitUtils.unZipAndReadFile
                                        (MagitUtils.joinPaths(OBJECTS_PATH, fileSha1InSecondCommit));
                            }

                            if (!fileSha1InAncestorCommit.equals("")) {
                                ancestorContent = MagitUtils.unZipAndReadFile
                                        (MagitUtils.joinPaths(OBJECTS_PATH, fileSha1InAncestorCommit));
                            }

                            mergeResult.setAncestorContent(ancestorContent);
                            mergeResult.setFirstContent(firstContent);
                            mergeResult.setSecondContent(secondContent);
                            mergeResult.setSecondSha1(fileSha1InSecondCommit);

                            mergeResultList.add(mergeResult);
                        }
                    }
                    return null;
                } else {
                    return fastForwardMerge;
                }
            }
            else {
                throw new DataAlreadyExistsException("WC has open changes!\nCommit first then merge");
            }
        } catch (InvalidDataException e) {
            String errorMsg = "Something went wrong while trying to merge two branches!\n" +
                    "Error message:" + e.getMessage();
            throw new InvalidDataException(errorMsg);
        }
    }

    private Commit sha1ToCommit(String sha1) {
        try { return (Commit) currentObjects.get(sha1); }
        catch (Exception e) { return null; }
    }

    private Map<String, String> makeMap(Commit currentCommit) throws InvalidDataException {
        Map<String, String> currentCommitFiles = new HashMap<>();
        try {
            getLastCommitFiles(currentCommit.getRootSha1(), rootPath.getValue(), currentCommitFiles);
            return currentCommitFiles;

        } catch (InvalidDataException e) {
            String errorMsg = "Something went wrong while trying to get the commit files!" +
                    "Error message: " + e.getMessage();
            throw new InvalidDataException(errorMsg);
        }
    }

    private Optional<FindCurrentFileState> findMergeCase(FileToMerge file) {
        boolean inFirst = true;
        boolean inSecond = true;
        boolean inAncestor = true;
        boolean firstSha1EqualsSeconds = false;
        boolean secondSha1EqualsAncestors = false;
        boolean firstSha1EqualsAncestors = false;

        String sha1InFirstCommit = file.getSha1InFirstCommit();
        String sha1InSecondCommit = file.getSha1InSecondCommit();
        String sha1InAncestorCommit = file.getSha1InAncestor();

        if(sha1InFirstCommit.equals("")) { inFirst = false; }
        if(sha1InSecondCommit.equals("")) { inSecond = false; }
        if(sha1InAncestorCommit.equals("")) { inAncestor = false; }

        if(sha1InFirstCommit.equals(sha1InSecondCommit)) { firstSha1EqualsSeconds = true;}
        if(sha1InSecondCommit.equals(sha1InAncestorCommit)) { secondSha1EqualsAncestors = true;}
        if(sha1InFirstCommit.equals(sha1InAncestorCommit)) { firstSha1EqualsAncestors = true;}


        boolean[] mergeCase = new boolean[6];
        mergeCase[0] = inFirst;
        mergeCase[1] = inSecond;
        mergeCase[2] = inAncestor;
        mergeCase[3] = firstSha1EqualsSeconds;
        mergeCase[4] = secondSha1EqualsAncestors;
        mergeCase[5] = firstSha1EqualsAncestors;

        return Arrays.stream(FindCurrentFileState.values()).
                filter(currentMergeCase -> currentMergeCase.isItMe(mergeCase[0], mergeCase[1],
                        mergeCase[2], mergeCase[3], mergeCase[4],
                        mergeCase[5])).findFirst();
    }

    private String findIfFFMerge(Branch branchToMerge, String ancestorSha1)
            throws InvalidDataException, IOException, FileErrorException{
        String mergeMsg;
        if(currentBranch.getCommitSha1().equals(ancestorSha1)) {
            resetCommitInBranch(branchToMerge.getCommitSha1(), false);
            mergeMsg = String.format("Changed branch %s commit to branch %s commit in a FF merge successfully!",
                    currentBranch.getName().getValue(), branchToMerge.getName().getValue());
            return mergeMsg;
        }
        else if(branchToMerge.getCommitSha1().equals(ancestorSha1)) {
            mergeMsg = String.format("Head branch %s contains branch %s. There is no data to merge!",
                    currentBranch.getName().getValue(), branchToMerge.getName().getValue());
            return mergeMsg;
        }
        return null;
    }

    private void isFileDirectoryInCurrentBranch(String filePath) throws IOException{
        File f = new File(filePath);
        if(!f.exists()) {
            if (f.mkdirs()) {
                if(f.delete()) {
                    f.createNewFile();
                }
            }
        }
    }

    // =========================== Clone =========================================

    void clone(String remote, String local, String repoName, String newRepoName)
            throws IOException, FileErrorException {
        List<String> headContent = new LinkedList<>();
        String content = "";
        localAndRemote.put(local, remote);

        cloneRepository(remote, local, repoName, newRepoName);

    }

    private void copyBranchsForClone(String remotePath, String localPath, String remoteName) throws IOException {
        File remoteFolder = new File(remotePath);
        File[] remoteFolderFiles = remoteFolder.listFiles();
        for (File currentFile : remoteFolderFiles) {
            File currentLocalFile = new File(MagitUtils.joinPaths(localPath, remoteName, currentFile.getName()));
            if (currentFile.isFile() && !currentFile.getName().contains("Head")) {
                MagitUtils.copyFile(currentFile, currentLocalFile);
            }
            else if(currentFile.getName().contains("Head")){
                String headName = MagitUtils.readFileAsString(currentFile.getAbsolutePath());
                MagitUtils.copyFile(new File(MagitUtils.joinPaths(remotePath, headName)),
                        new File (MagitUtils.joinPaths(localPath, headName)));
                MagitUtils.copyFile(currentFile,
                        new File (MagitUtils.joinPaths(localPath, currentFile.getName())));
                isRemoteTrackingBranchInLocalRepo.put(MagitUtils.joinPaths(localPath, headName), true);
            }
        }
    }
    private void cloneRepository(String remotePath, String localPath, String remoteName, String newRepoName) throws IOException {
        File remoteFolder = new File(remotePath);
        File[] remoteFolderFiles = remoteFolder.listFiles();

        // handle remote branchs
        if (remotePath.toLowerCase().equals(BRANCHES_PATH.toLowerCase())){
            File newRemoteFolder = new File(MagitUtils.joinPaths(localPath, remoteName));
            if (newRemoteFolder.mkdir()){
                copyBranchsForClone(remotePath, localPath, remoteName);
            }
        }
        else
        {
            for (File currentFile: remoteFolderFiles){
                File currentLocalFile = new File (MagitUtils.joinPaths(localPath, currentFile.getName()));

                if(currentFile.isFile()){
                    MagitUtils.copyFile(currentFile, currentLocalFile);
                }
                else {
                    if (currentLocalFile.mkdir()) {
                        cloneRepository(currentFile.getAbsolutePath(), currentLocalFile.getAbsolutePath(), remoteName, newRepoName);
                    }
                }
            }
        }
    }

    /*private void cloneRepository(String remote, String local, String repoName, String localUnchanged, List<String> headContent)
            throws IOException, FileErrorException {
        File remoteFolder = new File(remote);
        File[] remoteFolderFiles = remoteFolder.listFiles();
        if(remoteFolderFiles != null) {
            for (File remoteFolderFile : remoteFolderFiles) {
                if (remoteFolderFile.getAbsolutePath().contains(".magit")) {
                    if (remoteFolderFile.isDirectory()) {
                        String newPath;
                        if (remoteFolderFile.getAbsolutePath().contains("Branches")) {
                            newPath = Paths.get(local, remoteFolderFile.getName()).toString();
                            File file = new File(newPath);
                            if (file.mkdir()) {
                                newPath = Paths.get(newPath, repoName).toString();
                            }
                        } else {
                            newPath = Paths.get(local, remoteFolderFile.getName()).toString();
                        }
                        File newFile = new File(newPath);
                        if (newFile.mkdir()) {
                            for (File currentBranch : remoteFolderFile.listFiles()) {
                                if (currentBranch.isFile()) {
                                    cloneRepository(currentBranch.getAbsolutePath(),
                                            newPath, repoName, localUnchanged, headContent);
                                }
                            }
                        }
                    } else {
                        String newPath = Paths.get(local, remoteFolderFile.getName()).toString();
                        File newFile = new File(newPath);

                        if (newFile.createNewFile()) {
                            if (!newFile.getAbsolutePath().contains("Objects")) {
                                String content = new String
                                        (Files.readAllBytes(Paths.get(remoteFolderFile.getAbsolutePath())));
                                if (newFile.getAbsolutePath().contains("Head")) {
                                    headContent.add(content);
                                }
                                Writer writer = new BufferedWriter(new FileWriter(newFile));
                                writer.write(content);
                                writer.flush();
                                writer.close();
                            } else {
                                String content = MagitUtils.unZipAndReadFile(remoteFolderFile.getAbsolutePath());
                                Writer out1 = new BufferedWriter(
                                        new OutputStreamWriter(
                                                new FileOutputStream(newFile), StandardCharsets.UTF_8));
                                out1.write(content);
                                out1.flush();
                                out1.close();

                                MagitUtils.zipFile(newPath, newPath + ".zip");
                                if (!newFile.delete()) {
                                    String errorMsg = "Had an error while trying to delete a file!";
                                    throw new FileErrorException(errorMsg);
                                }
                                File newObj = new File(newPath + ".zip");
                                if (!newObj.renameTo(newFile)) {
                                    String errorMsg = "Had an error while trying to change a file name!";
                                    throw new FileErrorException(errorMsg);
                                }
                            }
                        }
                    }
                }
            }
        }
    }*/

    private String addRTBHeadBranchToRepo(String remoteWithBranchPath, String content, String localWithBranchPath)
            throws IOException{
        File f = new File(remoteWithBranchPath);
        File[] files = f.listFiles();
        if (files != null) {
            for (File curr : files) {
                if(curr.getName().equals(content)) {
                    File headBranch = new File(Paths.get(localWithBranchPath, curr.getName()).toString());
                    String headSha1 = MagitUtils.readFileAsString(curr.getAbsolutePath());
                    if (headBranch.createNewFile()) {
                        Writer out1 = new BufferedWriter(
                                new OutputStreamWriter(
                                        new FileOutputStream(headBranch), StandardCharsets.UTF_8));
                        out1.write(headSha1);
                        out1.flush();
                        out1.close();
                        return headSha1;
                    }
                }
            }
        }
        return null;
    }

    // =========================== Fetch =========================================
    void fetch(String remoteRepoName) throws IOException, FileErrorException {
        String remoteRepository = getRemoteRepoPath(rootPath.getValue());
        copyRemoteRepositoryBranchesToLocal(remoteRepository, rootPath.getValue(), remoteRepoName);
        copyRemoteRepositoryObjectsToLocal(remoteRepository, rootPath.getValue());
    }

    private void copyRemoteRepositoryBranchesToLocal(String remoteRepository, String localRepository,
                                                     String remoteRepoName)
            throws IOException {
        File remote = new File(Paths.get(MagitUtils.joinPaths(remoteRepository, ".magit\\Branches")).toString());
        String remoteBranchesPath = MagitUtils.joinPaths(localRepository, ".magit\\Branches", remoteRepoName);
        File local = new File(remoteBranchesPath);
        if (!local.exists()){
            local.mkdir();
        }
        File[] remoteFolderFiles = remote.listFiles();
        if (remoteFolderFiles != null) {
            for (File curr : remoteFolderFiles) {
                File newBranch = new File(Paths.get(local.getAbsolutePath(), curr.getName()).toString());
                if (curr.isFile()) {
                    if (!newBranch.exists()) {
                        if (newBranch.createNewFile()) {
                            String branchData = MagitUtils.readFileAsString(curr.getAbsolutePath());
                            MagitUtils.writeToFile(newBranch.getAbsolutePath(),
                                    branchData);
                            isRemoteTrackingBranchInLocalRepo.put(newBranch.getAbsolutePath(), true);
                            currentBranchesNames.add(curr.getName());
                            currentBranchs.add(new Branch(curr.getName(), branchData));
                        }
                    } else {
                        File remoteBranch = new File(Paths.get(remote.getAbsolutePath(), curr.getName()).toString());
                        String branchContent = MagitUtils.readFileAsString(remoteBranch.getAbsolutePath());
                        MagitUtils.writeToFile(newBranch.getAbsolutePath(), branchContent);
                    }
                }
            }
        }
    }

    private void copyRemoteRepositoryObjectsToLocal(String remoteRepository, String localRepository)
            throws IOException, FileErrorException{
        File remote = new File(Paths.get(MagitUtils.joinPaths(remoteRepository, ".magit\\Objects")).toString());
        File local = new File(Paths.get(MagitUtils.joinPaths(localRepository, ".magit\\Objects")).toString());

        File[] remoteFolderFiles = remote.listFiles();
        if (remoteFolderFiles != null) {
            for (File curr : remoteFolderFiles) {
                File newObjectFile = new File(Paths.get(local.getAbsolutePath(), curr.getName()).toString());
                if (!newObjectFile.exists()) {
                    String content = MagitUtils.unZipAndReadFile(curr.getAbsolutePath());
                    Writer out1 = new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(newObjectFile), StandardCharsets.UTF_8));
                    out1.write(content);
                    out1.flush();
                    out1.close();

                    MagitUtils.zipFile(newObjectFile.getAbsolutePath(), newObjectFile.getAbsolutePath() + ".zip");
                    if (!newObjectFile.delete()) {
                        String errorMsg = "Had an error while trying to delete a file!";
                        throw new FileErrorException(errorMsg);
                    }
                    File newObjFile = new File(newObjectFile.getAbsolutePath() + ".zip");
                    if (!newObjFile.renameTo(newObjectFile)) {
                        String errorMsg = "Had an error while trying to change a file name!";
                        throw new FileErrorException(errorMsg);
                    }
                    if(getRootPathAsString() != null) {
                        if (local.getAbsolutePath().contains(getRootPathAsString())) {
                            currentObjects.put(newObjectFile.getName(),
                                    GitObjectsBase.getGitObjectFromFile(curr.getAbsolutePath()));
                        }
                    }
                }
            }
        }
    }

    // =========================== Push =========================================

    void push(String remoteRepoName) throws IOException, InvalidDataException, FileErrorException {
        boolean isTracking = isRemoteTrackingBranchInLocalRepo.get(currentBranch.getName().getValue());
        String remoteRepo = getRemoteRepoPath(rootPath.getValue());
        if (remoteRepo != null && isTracking) {
            WorkingCopyChanges isWcHasOpenChanges = isWorkingCopyIsChanged();
            if (!isWcHasOpenChanges.isChanged()) {
                Branch head = getCurrentBranch();
                if (head != null) {
                    String headName = head.getName().getValue();
                    boolean isBranchRemoteTracking = isRemoteTrackingBranchInLocalRepo.get(headName);
                    if (isBranchRemoteTracking) {

                        // get the local tracked branch:
                        String rtHeadPath = MagitUtils.joinPaths(BRANCHES_PATH, remoteRepoName, headName);
                        String rtHeadContent = MagitUtils.readFileAsString(rtHeadPath);

                        // get the remote branch:
                        Branch rtTracked = getGivenBranchInGivenRepo(headName, remoteRepo);

                        //Makes sure local tracked and remote branch are synced
                        if (rtTracked != null && rtHeadContent.equals(rtTracked.getCommitSha1())) {
                            // update remote and tracked branch with changes of local.
                            MagitUtils.writeToFile(rtHeadPath, currentBranch.getCommitSha1());
                            MagitUtils.writeToFile(MagitUtils.joinPaths(remoteRepo, ".magit", "Branches", headName),
                                    currentBranch.getCommitSha1());
                            copyRemoteRepositoryObjectsToLocal(getRootPathAsString(), remoteRepo);
                            loadWCFromCommitSha1(currentBranch.getCommitSha1(), remoteRepo);
                        }
                        else {
                            String errorMsg = "Cannot push when remote branch and tracked branch are not synced.!";
                            throw new InvalidDataException(errorMsg);
                        }
                    } else {
                        String errorMsg = "Cannot push from a non-tracking branch!";
                        throw new InvalidDataException(errorMsg);
                    }
                }
            }
            else {
                String errorMsg = "Cannot push data to remote repository! Changes were found.\nCommit first!";
                throw new InvalidDataException(errorMsg);
            }
        }
    }

    // =========================== Pull =========================================

    void pull(String remoteRepoName) throws IOException, InvalidDataException, FileErrorException, DataAlreadyExistsException {

        String remoteRepo = getRemoteRepoPath(rootPath.getValue());
        if (remoteRepo != null) {
            WorkingCopyChanges isWcHasOpenChanges = isWorkingCopyIsChanged();
            if (!isWcHasOpenChanges.isChanged()) {
                //Gets the current head in local
                Branch head = getCurrentBranch();
                if (head != null) {
                    String headName = head.getName().getValue();
                    //Get the remote tracking branch the current head tracks
                    boolean isBranchRemoteTracking = isRemoteTrackingBranchInLocalRepo.get(headName);
                    if (isBranchRemoteTracking) {
                        String rtHeadPath = MagitUtils.joinPaths(BRANCHES_PATH, remoteRepoName, headName);
                        Branch rtTracked = getGivenBranchInGivenRepo(headName, remoteRepo);
                        String rtHeadContent = MagitUtils.readFileAsString(rtHeadPath);
                        // checks if current branch and local tracked branch are synced
                        if (rtTracked != null && rtHeadContent.equals(head.getCommitSha1())) {
                            MagitUtils.writeToFile(rtHeadPath, rtTracked.getCommitSha1());
                            copyRemoteRepositoryObjectsToLocal(remoteRepo, getRootPathAsString());
                            MagitUtils.writeToFile(MagitUtils.joinPaths(BRANCHES_PATH, headName),
                                    rtTracked.getCommitSha1());
                            head.setCommitSha1(rtTracked.getCommitSha1());
                            currentCommit = (Commit) currentObjects.get(rtTracked.getCommitSha1());
                            loadWCFromCommitSha1(head.getCommitSha1(), rootPath.getValue());
                        }
                    } else {
                        String errorMsg = "Cannot pull from a non-tracking branch!";
                        throw new InvalidDataException(errorMsg);
                    }
                }
            }
            else {
                String errorMsg = "Cannot pull data from remote repository! " +
                        "Changes were found.\nCommit first!";
                throw new InvalidDataException(errorMsg);
            }
        }
    }


}

