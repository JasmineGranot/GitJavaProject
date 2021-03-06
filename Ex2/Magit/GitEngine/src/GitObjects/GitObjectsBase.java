package GitObjects;

import Exceptions.FileErrorException;
import Utils.MagitUtils;

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

    public String doSha1() {
        return org.apache.commons.codec.digest.DigestUtils.sha1Hex(this.toString());
    }

    public void saveToMagitObjects(String sha1, String rootPath) throws IOException, FileErrorException {
        String errorMsg;
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
            if(!fileToZip.delete()){
                errorMsg = "Had an error while trying to delete a file!";
               throw new FileErrorException(errorMsg);
            }
            File newObj = new File(objPath.toString() + ".zip");
            if(!newObj.renameTo(fileToZip)){
                errorMsg = "Had an error while trying to change a file name!";
                throw new FileErrorException(errorMsg);
            }

        } catch (IOException e) {
            errorMsg = "Had an issue saving the new blob to objects!\n" +
                    "Error message: " + e.getMessage();
            throw new IOException(errorMsg);
        }
    }

    public void getDataFromFile(String fileSha1) throws IOException{
    }

    public void createFileFromObject(String destinationPath) throws IOException{
        MagitUtils.writeToFile(destinationPath, this.toString());
    }

    public static GitObjectsBase getGitObjectFromFile(String path) throws IOException {
        String contentStr = MagitUtils.unZipAndReadFile(path);
        String[] content = contentStr.split("\r\n");
        // only folder can have more then 1 line
        if (content.length > 1) {
            Folder newFolder = new Folder();
            newFolder.getDataFromFile(path);
            return newFolder;
        }
        else{
            String[] fields = content[0].split(MagitUtils.DELIMITER);
            if(fields.length == 5 || fields.length == 6) {
                if (fields[3].equals("Folder")) {
                    Folder newFolder = new Folder();
                    newFolder.getDataFromFile(path);
                    return newFolder;
                }
                if (fields[3].equals("File")) {
                    Blob newFile = new Blob();
                    newFile.getDataFromFile(path);
                    return newFile;
                }
                Commit newCommit = new Commit();
                newCommit.getDataFromFile(path);
                return newCommit;
            }
            else {
                Blob newFile = new Blob();
                newFile.getDataFromFile(path);
                return newFile;
            }
        }
    }
    public Commit convertToCommit(){
        if (this.isCommit()){
            return (Commit) this;
        }
        else{
            return null;
        }
    }
}
