import java.io.IOException;

public class Blob extends  GitObjectsBase{
    private String fileContent;

    void setFileContent(String content){
        fileContent = content;
    }

    @Override
    void getDataFromFile(String filePath) throws IOException {
        setFileContent(MagitUtils.unZipAndReadFile(filePath));
    }

    @Override
    public String toString() {
        return fileContent;
    }
}
