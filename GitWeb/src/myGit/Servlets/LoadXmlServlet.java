package myGit.Servlets;

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
            throws ServletException, IOException, JAXBException {
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        User currentUser = userManager.getUserByName(usernameFromSession);

        StringBuilder fileContent = new StringBuilder();
        fileContent.append(request.getReader().read());

        System.out.println(fileContent.toString());
        //currentUser.validateXML(fileContent);
        System.out.println();
        //myMagit.loadRepositoryFromXML(currentUser, request.)


//        response.setContentType("text/html;charset=UTF-8");
//        if (usernameFromSession == null) {
//           String usernameFromParameter = request.getParameter("username");
//            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
//                response.sendRedirect(SIGN_UP_URL); // replace with error
//            } else {
//                usernameFromParameter = usernameFromParameter.trim();
//                synchronized (this) {
//                    if (userManager.isUserExists(usernameFromParameter)) {
//                        if(userManager.isUserOnline(usernameFromParameter)) {// isUserOnline
//                            String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
//                            request.setAttribute("loggedInAlready", errorMessage);
//                            //TODO: //getServletContext().getRequestDispatcher(LOGIN_ERROR_URL).forward(request, response);
//                        }
//                        else {
//                            response.sendRedirect(PAGE_2);
//                        }
//                    } else {
//                        userManager.addUser(usernameFromParameter);
//                        request.getSession(true).setAttribute("username", usernameFromParameter);
//                        System.out.println("On login, request URI is: " + request.getRequestURI());
//                        response.sendRedirect(PAGE_2);
//                    }
//                }
//            }
//        } else {
//            response.sendRedirect(PAGE_2);
//        }
//    }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }


    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}

