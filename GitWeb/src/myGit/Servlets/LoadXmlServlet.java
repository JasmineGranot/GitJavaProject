package myGit.Servlets;

import GitObjects.User;
import GitObjects.UserManager;
import UIUtils.ServletUtils;
import UIUtils.SessionUtils;
import Engine.Magit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LoadXmlServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Magit myMagit = new Magit();
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        User currentUser = userManager.getUserByName(usernameFromSession);
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
        processRequest(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }


    @Override
    public String getServletInfo() {
        return "Short description";
    }
}

