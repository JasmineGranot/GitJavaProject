
package XMLHandler;

import Parser.MagitRepository;
import Utils.MagitUtils;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.apache.commons.codec.digest.DigestUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class XMLHandler {
    private MagitRepository parsedRepo;
    private String magitRepositoryPath;

    public XMLHandler(BufferedReader data, String userPath) throws JAXBException {
        unMarshalXMLData(data);
        setMagitRepositoryPath(MagitUtils.joinPaths(userPath, parsedRepo.getName()));
    }

    public String getMagitRepositoryPath(){
        return magitRepositoryPath;
    }

    public void setMagitRepositoryPath(String path){
        magitRepositoryPath = path;
    }
    public MagitRepository getMagitRepository() {
        return parsedRepo;
    }

    private void unMarshalXMLData(BufferedReader data) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance("Parser");
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        parsedRepo = (MagitRepository) jaxbUnmarshaller.unmarshal(data);
    }

    public boolean isRepoValid(){
        XMLValidator validation = new XMLValidator(parsedRepo, magitRepositoryPath);
        XMLValidationResult validationResult = validation.StartChecking();
        return validationResult.isValid();
    }

}


