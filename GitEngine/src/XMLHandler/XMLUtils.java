package XMLHandler;
import Parser.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.util.List;
import java.util.stream.Collectors;

public class XMLUtils {

    MagitRepository magitRepo;

    public static MagitSingleCommit getMagitSingleCommitByID(MagitRepository repoFromXML, String commitId) {
        List<MagitSingleCommit> commits = repoFromXML.getMagitCommits().getMagitSingleCommit().stream().
                filter(x-> x.getId().equals(commitId)).collect(Collectors.toList());
        if (!commits.isEmpty()) {
            return commits.get(0);
        }
        return null;
    }

    public static MagitSingleBranch getMagitSingleBranchByName(MagitRepository repoFromXML, String branchName){
        List<MagitSingleBranch> branches = repoFromXML.getMagitBranches().getMagitSingleBranch().stream().
                filter(x-> x.getName().equals(branchName)).collect(Collectors.toList());
        if(!branches.isEmpty()){
            return branches.get(0);
        }
        return null;
    }

    public static MagitSingleFolder getMagitFolderByID(MagitRepository repoFromXML, String folderId){
        List<MagitSingleFolder> folders = repoFromXML.getMagitFolders().getMagitSingleFolder().stream().
                filter(x-> x.getId().equals(folderId)).collect(Collectors.toList());
        if (!folders.isEmpty()) {
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

//    private void unMarshalXMLData(BufferedReader data) throws JAXBException
//    {
//        JAXBContext context = JAXBContext.newInstance(MagitRepository.class);
//        Unmarshaller unmarshaller = context.createUnmarshaller();
//        M = (MagitRepository) unmarshaller.unmarshal(data);
//    }
}

