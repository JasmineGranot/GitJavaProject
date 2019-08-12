package XMLHandler;
import engine.InvalidDataException;
import engine.parser.*;
import org.omg.CORBA.DynAnyPackage.Invalid;

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
            CheckDoubledId(currentRepo.getMagitBranches(),currentRepo.getMagitCommits(),
                    currentRepo.getMagitFolders(),currentRepo.getMagitBlobs());
            checkPointedId(currentRepo.getMagitFolders());
            CheckifCommitPointsToFolder(currentRepo.getMagitCommits());
            CheckIfBranchPointsToCommit(currentRepo.getMagitBranches());
            CheckifHeadExists(currentRepo.getMagitBranches());
        } catch (InvalidDataException e) {
            validationResult.setIsValid(false);
            validationResult.setMessage(e.getMessage());
        }
        return validationResult;
    }
    private void checkFileExitsAndXml(String path) throws InvalidDataException{ // valdation number 3.1
        File file = new File(path);
        if (!file.exists() || !path.endsWith(".xml")){
            throw new InvalidDataException(String.format("Xml File %s does not exist.", path));
        }
    }
    private void CheckDoubledId(MagitBranches branches, MagitCommits commits,MagitFolders folders,MagitBlobs blobs )
            throws InvalidDataException //3.2
    {
        Map<String, MagitSingleCommit> mapCommit = new HashMap<>();
        for (MagitSingleCommit curr : commits.getMagitSingleCommit()) {
            if (mapCommit.containsKey(curr.getId())) {
                throw new InvalidDataException("Two Commits With same ID");
            }
            mapCommit.put(curr.getId(), curr);
        }
        Map<String, MagitSingleFolder> mapFolder = new HashMap<>();
        for (MagitSingleFolder curr : folders.getMagitSingleFolder()) {
            if (mapFolder.containsKey(curr.getId())) {
                throw new InvalidDataException("Two folders With same ID");
            }
            mapFolder.put(curr.getId(), curr);
        }
        Map<String, MagitBlob> mapBlob = new HashMap<>();
        for (MagitBlob curr : blobs.getMagitBlob()) {
            if (mapBlob.containsKey(curr.getId())) {
                throw new InvalidDataException("Two blobs With same ID");
            }
            mapBlob.put(curr.getId(), curr);
        }
        if (!XMLUtils.isEmptyRepo(currentRepo)) {
            for (MagitSingleBranch curr : branches.getMagitSingleBranch()) {
                if (XMLUtils.getMagitSingleCommitByID(currentRepo, curr.getPointedCommit().getId()) == null) {
                    throw new InvalidDataException("Branch points to Commit that doesnt Exist");
                }
            }
        }
    }

    private void checkPointedId(MagitFolders folderList) throws InvalidDataException //3.3 //3.4 //3.5
    {
        for(MagitSingleFolder curr : folderList.getMagitSingleFolder())
        {
            for(Item item: curr.getItems().getItem())
            {
                if(item.getType().equals("folder"))
                {
                    MagitSingleFolder folder = XMLUtils.getMagitFolderByID(currentRepo, item.getId());
                    if(folder == null)
                    {
                        throw new InvalidDataException( "Pointed folder id doesnt exist");
                    }
                    if(folder.getId().equals(curr.getId()))
                    {
                        throw new InvalidDataException( "folder contain itself (id)");
                    }

                }
                else
                {
                    if(XMLUtils.getMagitBlobByID(currentRepo, item.getId())==null)
                    {
                        throw new InvalidDataException( "Pointed blob id doesnt exist");
                    }
                }
            }
        }

    }
    private void CheckifCommitPointsToFolder (MagitCommits commitList) throws InvalidDataException { //3.6 //3.7
        for (MagitSingleCommit curr : commitList.getMagitSingleCommit()) {
            MagitSingleFolder folder = XMLUtils.getMagitFolderByID(currentRepo, curr.getRootFolder().getId());
            if (folder == null) {
                throw new InvalidDataException("Commit points to Folder that doesnt exist");
            }
            if (!folder.isIsRoot())
            {
                throw new InvalidDataException("Commit points to Folder that isnt RootFolder");
            }
        }
    }
    private void CheckIfBranchPointsToCommit (MagitBranches branchList) throws InvalidDataException { //3.8
        if(!XMLUtils.isEmptyRepo(currentRepo)) {
            for (MagitSingleBranch curr : branchList.getMagitSingleBranch()) {
                if (XMLUtils.getMagitSingleCommitByID(currentRepo, curr.getPointedCommit().getId()) == null) {
                    throw new InvalidDataException("Branch points to Commit that doesnt Exist");
                }
            }
        }
    }


    private void CheckifHeadExists (MagitBranches magitBranches) throws InvalidDataException { //3.9
        for(MagitSingleBranch curr : magitBranches.getMagitSingleBranch())
        {
            if(curr.getName().equals(magitBranches.getHead()))
            {
                return;
            }
        }
        throw new InvalidDataException("Branch Head doesnt exist");

    }


}