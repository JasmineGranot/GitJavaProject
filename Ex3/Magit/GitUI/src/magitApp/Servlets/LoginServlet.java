package Servlets;

import GitObjects.UserManager;
import UIUtils.ServletUtils;
import UIUtils.SessionUtils;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class LoginServlet extends HttpServlet {
    private final String PAGE_2 = "../Windows/magit_test.html";
    private final String SIGN_UP_URL = "../Windows/index.html";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        if (usernameFromSession == null) {
           String usernameFromParameter = request.getParameter("username");
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                response.sendRedirect(SIGN_UP_URL); // replace with error
            } else {
                usernameFromParameter = usernameFromParameter.trim();
                synchronized (this) {
                    if (userManager.isUserExists(usernameFromParameter)) {
                        if(userManager.isUserOnline(usernameFromParameter)) {// isUserOnline
                            String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
                            request.setAttribute("loggedInAlready", errorMessage);
                            //TODO: //getServletContext().getRequestDispatcher(LOGIN_ERROR_URL).forward(request, response);
                        }
                        else {
                            response.sendRedirect(PAGE_2);
                        }
                    } else {
                        userManager.addUser(usernameFromParameter);
                        request.getSession(true).setAttribute("username", usernameFromParameter);
                        System.out.println("On login, request URI is: " + request.getRequestURI());
                        response.sendRedirect(PAGE_2);
                    }
                }
            }
        } else {
            response.sendRedirect(PAGE_2);
        }
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

