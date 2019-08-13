package XMLHandler;
import engine.InvalidDataException;
import engine.parser.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class XMLValidator {

    private String xmlPath;
    private MagitRepository currentRepo;

    public XMLValidator(MagitRepository xmlRepo, String xmlFilePath){
        currentRepo = xmlRepo;
        xmlPath = xmlFilePath;
    }

    public XMLValidationResult StartChecking () {
        XMLValidationResult validationResult = new XMLValidationResult();
        validationResult.setIsValid(true);
        try{
            checkFileExitsAndXml(xmlPath);
            checkDoubledId(currentRepo.getMagitBranches(),currentRepo.getMagitCommits(),
                    currentRepo.getMagitFolders(),currentRepo.getMagitBlobs());
            checkPointedId(currentRepo.getMagitFolders());
            checkIfCommitPointsToFolder(currentRepo.getMagitCommits());
            checkIfBranchPointsToCommit(currentRepo.getMagitBranches());
            checkIfHeadExists(currentRepo.getMagitBranches());
        }
        catch (InvalidDataException e) {
            validationResult.setIsValid(false);
            validationResult.setMessage(e.getMessage());
        }
        return validationResult;
    }

    // =====================
    // validation number 3.1
    // =====================
    private void checkFileExitsAndXml(String path) throws InvalidDataException{
        String errorMsg;
        File file = new File(path);
        if (!file.exists() || !path.endsWith(".xml")) {
            errorMsg = String.format("Xml File %s does not exist.", path);
            throw new InvalidDataException(errorMsg);
        }
    }

    // =====================
    // validation number 3.2
    // =====================
    private void checkDoubledId(MagitBranches branches, MagitCommits commits,
                                MagitFolders folders, MagitBlobs blobs)
            throws InvalidDataException {
        String errorMsg;
        Map<String, MagitSingleCommit> mapCommit = new HashMap<>();
        for (MagitSingleCommit curr : commits.getMagitSingleCommit()) {
            if (mapCommit.containsKey(curr.getId())) {
                errorMsg = "Fount two commits with the same ID";
                throw new InvalidDataException(errorMsg);
            }
            mapCommit.put(curr.getId(), curr);
        }

        Map<String, MagitSingleFolder> mapFolder = new HashMap<>();
        for (MagitSingleFolder curr : folders.getMagitSingleFolder()) {
            if (mapFolder.containsKey(curr.getId())) {
                errorMsg = "Found two folders with the same ID";
                throw new InvalidDataException(errorMsg);
            }
            mapFolder.put(curr.getId(), curr);
        }

        Map<String, MagitBlob> mapBlob = new HashMap<>();
        for (MagitBlob curr : blobs.getMagitBlob()) {
            if (mapBlob.containsKey(curr.getId())) {
                errorMsg = "Fount two blobs with the same ID";
                throw new InvalidDataException(errorMsg);
            }
            mapBlob.put(curr.getId(), curr);
        }

        if (!XMLUtils.isEmptyRepo(currentRepo)) {
            for (MagitSingleBranch curr : branches.getMagitSingleBranch()) {
                if (XMLUtils.getMagitSingleCommitByID(currentRepo,
                        curr.getPointedCommit().getId()) == null) {
                    errorMsg = "Branch points to commit that doesn't exists";
                    throw new InvalidDataException(errorMsg);
                }
            }
        }
    }

    // ===============================
    // validation number 3.3, 3.4, 3.5
    // ===============================
    private void checkPointedId(MagitFolders folderList) throws InvalidDataException
    {
        String errorMsg;
        for(MagitSingleFolder curr : folderList.getMagitSingleFolder()) {
            for(Item item : curr.getItems().getItem()) {
                if(item.getType().equals("folder")) {
                    MagitSingleFolder folder = XMLUtils.getMagitFolderByID(currentRepo, item.getId());
                    if(folder == null) {
                        errorMsg = "The ID of the pointed folder doesn't exist!";
                        throw new InvalidDataException(errorMsg);
                    }
                    if(folder.getId().equals(curr.getId())) {
                        errorMsg = "folder contains itself (id)";
                        throw new InvalidDataException(errorMsg);
                    }

                }
                else {
                    if(XMLUtils.getMagitBlobByID(currentRepo, item.getId()) == null) {
                        errorMsg = "The ID of the pointed blob doesn't exist";
                        throw new InvalidDataException(errorMsg);
                    }
                }
            }
        }
    }

    // ==========================
    // validation number 3.6, 3.7
    // ==========================
    private void checkIfCommitPointsToFolder(MagitCommits commitList) throws InvalidDataException {
        for (MagitSingleCommit curr : commitList.getMagitSingleCommit()) {
            String errorMsg;
            MagitSingleFolder folder = XMLUtils.getMagitFolderByID(currentRepo, curr.getRootFolder().getId());
            if (folder == null) {
                errorMsg = "Commit points to a folder that doesnt exist";
                throw new InvalidDataException(errorMsg);
            }
            if (!folder.isIsRoot()) {
                errorMsg = "Commit points to a folder that isn't RootFolder";
                throw new InvalidDataException(errorMsg);
            }
        }
    }

    // =====================
    // validation number 3.8
    // =====================
    private void checkIfBranchPointsToCommit(MagitBranches branchList) throws InvalidDataException {
        String errorMsg;
        if(!XMLUtils.isEmptyRepo(currentRepo)) {
            for (MagitSingleBranch curr : branchList.getMagitSingleBranch()) {
                if (XMLUtils.getMagitSingleCommitByID(currentRepo,
                        curr.getPointedCommit().getId()) == null) {
                    errorMsg = "Branch points to commit that doesn't exists";
                    throw new InvalidDataException(errorMsg);
                }
            }
        }
    }

    // =====================
    // validation number 3.9
    // =====================
    private void checkIfHeadExists(MagitBranches magitBranches) throws InvalidDataException {
        String errorMsg;
        for(MagitSingleBranch curr : magitBranches.getMagitSingleBranch()) {
            if(curr.getName().equals(magitBranches.getHead())) {
                return;
            }
        }
        errorMsg = "Branch Head doesn't exists";
        throw new InvalidDataException(errorMsg);

    }
}