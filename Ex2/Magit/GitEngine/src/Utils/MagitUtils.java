package Utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class MagitUtils {
    public final static String DELIMITER = "; ";
    private final static String DATE_PATTERN = "dd.MM.yyyy-hh:mm:ss:sss";
    private static Charset ENCODING = StandardCharsets.UTF_8;

    public static String readFileAsString(String filePath) throws IOException{
        String data = "";
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

    public static String unZipAndReadFile(String filePath) throws IOException{
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

    public static String joinPaths(String path, String fileName){
        return Paths.get(path, fileName).toString();
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
}