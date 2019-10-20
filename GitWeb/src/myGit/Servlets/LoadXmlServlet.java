package myGit.Servlets;

import Exceptions.DataAlreadyExistsException;
import Exceptions.FileErrorException;
import GitObjects.User;
import GitObjects.UserManager;
import Parser.MagitRepository;
import UIUtils.ServletUtils;
import UIUtils.SessionUtils;
import Engine.Magit;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Scanner;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class LoadXmlServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, JAXBException, DataAlreadyExistsException, FileErrorException {

        response.setContentType("text/html;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        User currentUser = userManager.getUserByName(usernameFromSession);
        String content = request.getParameter("file");

        if(currentUser.validateXML(content)){
            currentUser.loadXMLForUser(content);
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

