package Utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class MagitUtils {
    public final static String DELIMITER = "; ";
    public final static String DATE_PATTERN = "dd.MM.yyyy-hh:mm:ss:sss";
    public final static String OPEN_PULL_REQUEST = "Open";
    public final static String CLOSED_PULL_REQUEST = "Closed";
    private static Charset ENCODING = StandardCharsets.UTF_8;

    public static String readFileAsString(String filePath) throws IOException{
        String data;
        data = new String(Files.readAllBytes(Paths.get(filePath)));

        return data;
    }

    public static void writeToFile(Path filePath, String content) throws IOException {
        writeToFile(filePath.toString(), content);
    }

    public static void writeToFile(String filePath, String content) throws IOException {
        String errorMsg;
        try {
            Writer out = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(filePath), ENCODING));
            out.write(content);
            out.flush();
            out.close();

        } catch (IOException e) {
            errorMsg = "Something went wrong while trying to write a content to a file!";
            throw new IOException(errorMsg);
        }
    }

    public static String getTodayAsStr(){
        return new SimpleDateFormat(DATE_PATTERN).format(new Date());
    }
    public static Date getTodayAsDate(){
        return new Date();
    }
    public static String getDateAsString(Date date){
        return new SimpleDateFormat(DATE_PATTERN).format(date);
    }
    public static Date getStringAsDate(String dateString) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
            return formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();

    }

    public static void zipFile(String source, String dest){
        try {
            FileOutputStream fos = new FileOutputStream(dest);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(source);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            zipOut.close();
            fis.close();
            fos.close();

        }
        catch (IOException e){
            System.out.println("FILE STILL OPEN!!!!!!!");
            e.getMessage();
        }

    }

    public static String unZipAndReadFile(String filePath) throws IOException {
        int len;
        int INPUT_SIZE = 100;
        final byte[] buffer = new byte[INPUT_SIZE];

        ZipInputStream z = new ZipInputStream(new FileInputStream(filePath));
        z.getNextEntry();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        while ((len = z.read(buffer)) > 0) {
            byteStream.write(buffer, 0, len);
        }

        z.close();
        
        return new String(byteStream.toByteArray(), ENCODING);
    }

    public static String joinPaths(String... path){
        String res = "";
        for(String current : path) {
            res = res.concat(current);
            res = res.concat("\\");
        }
        res = res.substring(0, res.length() - 1);
        return res;
    }

    public static String getFileContent(FileInputStream fis, Charset encoding ) throws IOException
    {
        try( BufferedReader br =
                     new BufferedReader( new InputStreamReader(fis, encoding )))
        {
            String content = "";
            String line;
            while((line = br.readLine()) != null ) {
                content = content.concat(line);
                content = content.concat("\n");
            }
            return content;
        }
    }

    public static boolean isRepositoryExist(String newRepoPath) {
        File repo = new File(newRepoPath);
        return repo.exists();
    }

    static void writeFileToRepository(String filePath, String fileSha1, MergeResult res) {
        try {
            res.writeFileToRepository(filePath, fileSha1);
            res.setSucceeded(true);
            res.setHasConflict(false);
            res.setFileName(filePath);
            res.setFilePath(filePath);
        } catch (IOException e) {
            res.setSucceeded(false);
            String errorMsg = "An error occurred while trying to write the new file to the repository!\n" +
                    "Error message: " + e.getMessage();
            res.setErrorMsg(errorMsg);
        }
    }

    static void deleteFileFromRepository(String filePath, MergeResult res) {
        res.setHasConflict(false);
        res.setFileName(filePath);
        res.setFilePath(filePath);
        if(res.deleteFileFromRepository(filePath)) {
            res.setSucceeded(true);
        }
        else {
            res.setSucceeded(false);
        }
    }

    static void success(MergeResult res, String fileName) {
        res.setSucceeded(true);
        res.setHasConflict(false);
        res.setFileName(fileName);
        res.setFilePath(fileName);
    }

    static void conflict(MergeResult res, String fileName) {
        res.setSucceeded(false);
        res.setHasConflict(true);
        res.setFileName(fileName);
        res.setFilePath(fileName);
    }

    public static void copyFile(File source, File target) throws IOException {
        InputStream is = null;
        OutputStream os = null;

        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(target);
            byte[] buffer = new byte[2048];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
            if(os != null) {
                os.close();
            }
        }
    }
}