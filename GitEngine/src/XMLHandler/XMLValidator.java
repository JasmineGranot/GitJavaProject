package XMLHandler;

import Exceptions.InvalidDataException;
import Parser.*;
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
            checkDoubledId(currentRepo.getMagitBranches(),currentRepo.getMagitCommits(),
                    currentRepo.getMagitFolders(),currentRepo.getMagitBlobs());
            checkPointedId(currentRepo.getMagitFolders());
            checkIfCommitPointsToFolder(currentRepo.getMagitCommits());
            checkIfBranchPointsToCommit(currentRepo.getMagitBranches());
            checkIfHeadExists(currentRepo.getMagitBranches());
            checkIfRemoteRepoValid(currentRepo.getMagitRemoteReference());
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
//    private void checkFileExitsAndXml(String path) throws InvalidDataException{
//        String errorMsg;
//        if (!path.endsWith(".xml")) {
//            errorMsg = String.format("Xml File %s is not valid.", path);
//            throw new InvalidDataException(errorMsg);
//        }
//    }

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
                errorMsg = "Found two commits with the same ID in the XML file";
                throw new InvalidDataException(errorMsg);
            }
            mapCommit.put(curr.getId(), curr);
        }

        Map<String, MagitSingleFolder> mapFolder = new HashMap<>();
        for (MagitSingleFolder curr : folders.getMagitSingleFolder()) {
            if (mapFolder.containsKey(curr.getId())) {
                errorMsg = "Found two folders with the same ID in the XML file";
                throw new InvalidDataException(errorMsg);
            }
            mapFolder.put(curr.getId(), curr);
        }

        Map<String, MagitBlob> mapBlob = new HashMap<>();
        for (MagitBlob curr : blobs.getMagitBlob()) {
            if (mapBlob.containsKey(curr.getId())) {
                errorMsg = "Found two blobs with the same ID in the XML file";
                throw new InvalidDataException(errorMsg);
            }
            mapBlob.put(curr.getId(), curr);
        }

        if (!XMLUtils.isEmptyRepo(currentRepo)) {
            for (MagitSingleBranch curr : branches.getMagitSingleBranch()) {
                if (XMLUtils.getMagitSingleCommitByID(currentRepo,
                        curr.getPointedCommit().getId()) == null) {
                    errorMsg = String.format("Branch %s points to commit that doesn't exists", curr.getName());
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
                        errorMsg = String.format("The ID %s is pointing to a non existing folder (with ID: %s)",
                                curr.getId(), item.getId());
                        throw new InvalidDataException(errorMsg);
                    }
                    if(folder.getId().equals(curr.getId())) {
                        errorMsg = String.format("folder %s contains itself", curr.getName());
                        throw new InvalidDataException(errorMsg);
                    }
                }
                else {
                    if(XMLUtils.getMagitBlobByID(currentRepo, item.getId()) == null) {
                        errorMsg = String.format("The ID %s is pointing to a non existing file (with ID: %s)!",
                                curr.getId(), item.getId());
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
                errorMsg = String.format("Commit %s points to a folder (ID: %s) that doesnt exist",
                        curr.getId(), curr.getRootFolder().getId());
                throw new InvalidDataException(errorMsg);
            }
            if (!folder.isIsRoot()) {
                errorMsg = String.format("Commit %s points to a folder (ID: %s) that isn't RootFolder",
                        curr.getId(), curr.getRootFolder().getId());
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
                    errorMsg = String.format("Branch %s points to commit that doesn't exists", curr.getName());
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
        errorMsg = "Head branch doesn't exists";
        throw new InvalidDataException(errorMsg);

    }
    // ====================================================================================================================
    // check if Remote is ok
    // ====================================================================================================================
    private void checkIfRemoteRepoValid(MagitRepository.MagitRemoteReference magitRemoteReference)
            throws InvalidDataException {
        if (magitRemoteReference != null) {
            checkIfRepoInElementExists(magitRemoteReference);
            checkIfBranchIsTracking(currentRepo.getMagitBranches());
        }
    }
    // ====================================================================================================================
    // check if MagitRemoteReference element appears in the repository and if the location in it points to an existing repo
    // ====================================================================================================================
    private void checkIfRepoInElementExists(MagitRepository.MagitRemoteReference magitRemoteReference)
            throws InvalidDataException{
        if(magitRemoteReference.getName() != null &&
                magitRemoteReference.getLocation() != null){
            String location = magitRemoteReference.getLocation();
            File file = new File(location);
            if(!file.exists()) {
                String errorMsg = "There is no such repository in the magit remote reference location!";
                throw new InvalidDataException(errorMsg);
            }
        }
    }

    // =========================================================================================================
    // check if a branch is tracking = true, then if tracking-after points to a branch which is is-remote = true
    // =========================================================================================================
    private void checkIfBranchIsTracking(MagitBranches magitBranches) throws InvalidDataException {
        for(MagitSingleBranch curr : magitBranches.getMagitSingleBranch()) {
            if(curr.isTracking()){
                String trackingAfter = curr.getTrackingAfter();
                MagitSingleBranch magitSingleBranch = XMLUtils.getMagitSingleBranchByName(currentRepo, trackingAfter);
                if(!magitSingleBranch.isIsRemote()){
                    String errorMsg = String.format("Branch %s is tracking after branch %s but %s is not remote!",
                            curr.getName(), trackingAfter, trackingAfter);
                    throw new InvalidDataException(errorMsg);
                }
            }

        }
    }
}