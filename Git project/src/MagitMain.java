import parser.MagitRepository;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class MagitMain {

    public static void main(String[] args){
        UI uiConsole = new UI();
        try{
            uiConsole.runProgram();
        }
        catch(Exception e){
            System.out.println("had issues.....");
        }
    }

}