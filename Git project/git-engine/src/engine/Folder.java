package engine;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Folder extends GitObjectsBase {
    private List<FileDetails> filesList = new LinkedList<>();

    void addFile(FileDetails newFile)
    {
        filesList.add(newFile);
    }

    List<FileDetails> getFilesList(){
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

    public void orderByAlphaBet(){
        filesList.sort(FileDetails::compareTo);
    }

    @Override
    void getDataFromFile(String filePath) throws IOException {
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
    void createFileFromObject(String destinationPath){
        File newFolder = new File(destinationPath);
        newFolder.mkdir();
    }
}