package XMLHandler;

import engine.parser.*;

import java.util.List;
import java.util.stream.Collectors;

public class XMLUtils {

    public static MagitSingleCommit getMagitSingleCommitByID(MagitRepository repoFromXML, String commitId){
        List<MagitSingleCommit> commits = repoFromXML.getMagitCommits().getMagitSingleCommit().stream().
                filter(x-> x.getId().equals(commitId)).collect(Collectors.toList());
        if (!commits.isEmpty()){
            return commits.get(0);
        }
        else {
            return null;
        }
    }
    public static MagitSingleFolder getMagitFolderByID(MagitRepository repoFromXML, String folderId){
        List<MagitSingleFolder> folders = repoFromXML.getMagitFolders().getMagitSingleFolder().stream().
                filter(x-> x.getId().equals(folderId)).collect(Collectors.toList());
        if (!folders.isEmpty()){
            return folders.get(0);
        }
        return null;
    }
    public static MagitBlob getMagitBlobByID(MagitRepository repoFromXML, String fileId){
        List<MagitBlob> files = repoFromXML.getMagitBlobs().getMagitBlob().stream().
                filter(x-> x.getId().equals(fileId)).collect(Collectors.toList());
        if (!files.isEmpty()){
            return files.get(0);
        }
        return null;
    }
    public static boolean isEmptyRepo(MagitRepository repo){
        MagitBranches branchList = repo.getMagitBranches();
        if(branchList.getMagitSingleBranch().size() == 1) {
            MagitSingleBranch onlyBranch = branchList.getMagitSingleBranch().get(0);
            return onlyBranch.getName().equals(branchList.getHead()) &&
                    XMLUtils.getMagitSingleCommitByID(repo, onlyBranch.getPointedCommit().getId()) == null;
        }
        return false;
    }
}

