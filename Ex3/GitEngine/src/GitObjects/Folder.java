package GitObjects;

import Utils.MagitUtils;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Folder extends GitObjectsBase {
    private List<FileDetails> filesList = new LinkedList<>();

    public void addFile(FileDetails newFile)
    {
        filesList.add(newFile);
    }

    public List<FileDetails> getFilesList(){
        orderByAlphaBet();
        return filesList;
    }

    @Override
    public boolean isFolder() {
        return true;
    }

    @Override
    public String toString() {
        orderByAlphaBet();
        String content = "";
        for (FileDetails child : filesList){
            content = content.concat(child.toString());
            content = content.concat("\r\n");
        }
        return content;
    }

    private void orderByAlphaBet(){
        filesList.sort(FileDetails::compareTo);
    }

    @Override
    public void getDataFromFile(String filePath) throws IOException {
        String data = MagitUtils.unZipAndReadFile(filePath);
        String[] childsData = data.split("\r\n");

        for (String child : childsData) {
            String[] childDetails = child.split(MagitUtils.DELIMITER);
            if (child.length() > 0) {
                FileDetails newDetails = new FileDetails(childDetails[0], childDetails[1], childDetails[2],
                        childDetails[3], childDetails[4]);
                addFile(newDetails);
            }
        }
        orderByAlphaBet();
    }
    @Override
    public void createFileFromObject(String destinationPath){
        File newFolder = new File(destinationPath);
        newFolder.mkdir();
    }
}