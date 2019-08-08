import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class MagitMain {
    public static final String FILE_NAME = "C:\\Users\\97250\\Downloads\\master.xml";

    public static void main(String[] args){
        fromXmlFileToObject();
        UI uiConsole = new UI();
        try{
            uiConsole.runProgram();
            System.out.println("Vered is on Fucking GIT!!!11");
        }
        catch(Exception e){
            System.out.println("had issues.....");
        }
    }

    private static void fromXmlFileToObject() {
        System.out.println("\nFrom File to Object");

        try {

            File file = new File(FILE_NAME);
            JAXBContext jaxbContext = JAXBContext.newInstance(Magit.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//            Customer customer = (Customer) jaxbUnmarshaller.unmarshal(file);
//            System.out.println(customer);

        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }
}