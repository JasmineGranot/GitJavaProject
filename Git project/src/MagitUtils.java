import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

class MagitUtils {
    final static String DELIMITER = "; ";
    private final static String DATE_PATTERN = "dd.MM.yyyy-hh:mm:ss:sss";
    private static Charset ENCODING = StandardCharsets.UTF_8;

    static String readFileAsString(String filePath){
        String content = "";
        try {
            File f  = new File(filePath);
            Scanner reader = new Scanner(f);
            while(reader.hasNextLine())
            {
                content = content.concat(reader.nextLine());
            }
            reader.close();
            return content;
        }
        catch (Exception e){
            return content;
        }
    }

    static void writeToFile(Path filePath, String content) {
        writeToFile(filePath.toString(), content);
    }

    static void writeToFile(String filePath, String content){
//    	File newFile = new File(filePath);

        try {
            Writer out = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(filePath), ENCODING));
            out.write(content);
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String getTodayAsStr(){
        return new SimpleDateFormat(DATE_PATTERN).format(new Date());
    }

    static void zipFile(String source, String dest){
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

    static String unZipAndReadFile(String filePath) {
        try {
            ZipInputStream z = new ZipInputStream(new FileInputStream(filePath));
            z.getNextEntry();
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            int len;
            int INPUT_SIZE = 100;
            final byte[] buffer = new byte[INPUT_SIZE];
            while ((len = z.read(buffer)) > 0) {
                byteStream.write(buffer, 0, len);
            }
            z.close();
            return new String(byteStream.toByteArray(), ENCODING);
        } catch (IOException e) {
            return null;
        }
    }

    static String joinPaths(String path, String fileName){
        return Paths.get(path, fileName).toString();
    }
}