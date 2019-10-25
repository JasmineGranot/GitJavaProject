package Servlets;

import Exceptions.DataAlreadyExistsException;
import Exceptions.FileErrorException;
import GitObjects.User;
import GitObjects.UserManager;
import UIUtils.ServletUtils;
import UIUtils.SessionUtils;
import Engine.Magit;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;

@WebServlet(
        urlPatterns = "/LoadXml"
)

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class LoadXmlServlet extends HttpServlet {
    private final String SERVER_PATH = "c:\\magit-ex3";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws JAXBException, DataAlreadyExistsException, FileErrorException {
        // To remove when system servlet is up
        File newServerFolder = new File(SERVER_PATH);
        if(!newServerFolder.exists()){
            newServerFolder.mkdir();
        }
        response.setContentType("text/html;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Magit magitManager = ServletUtils.getMagitObject(getServletContext());
        User currentUser = userManager.getUserByName(usernameFromSession);
        String content = request.getParameter("file");
        if(magitManager.validateXML(currentUser, content)){
            magitManager.loadXMLForUser(currentUser, content);
        }
        else{
            String msg = "ValidateXML failed."; //TODO
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (JAXBException | DataAlreadyExistsException | FileErrorException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (JAXBException | DataAlreadyExistsException | FileErrorException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet handling parsing and validating of xml file.";
    }
}

