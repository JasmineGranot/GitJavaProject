import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GitObjectsBase {

    public boolean isFolder(){
        return false;
    }

    public boolean isCommit(){
        return false;
    }

    String doSha1() {
        return org.apache.commons.codec.digest.DigestUtils.sha1Hex(this.toString());
    }

    void saveToObjects(String sha1, String rootPath) {
        Path objPath = Paths.get(rootPath, ".magit", "Objects", sha1);
        try {
            File fileToZip = new File(objPath.toString());
            Writer out1 = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(fileToZip), StandardCharsets.UTF_8));
            out1.write(this.toString());
            out1.flush();
            out1.close();
            MagitUtils.zipFile(objPath.toString(), objPath.toString() + ".zip");
            fileToZip.delete();
            File newObj = new File(objPath.toString() + ".zip");
            newObj.renameTo(fileToZip);

        } catch (IOException e) {
            System.out.println("had an issue with the files here......");
        }
    }

    void getDataFromFile(String fileSha1){}

    void createFileFromObject(String destinationPath){
        MagitUtils.writeToFile(destinationPath, this.toString());
    }
}
