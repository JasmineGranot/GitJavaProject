package Servlets;

import Engine.Magit;
import GitObjects.Repository;
import GitObjects.UserManager;
import UIUtils.ServletUtils;
import UIUtils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet (
            urlPatterns = "/Signup"
)

public class LoginServlet extends HttpServlet {
    private final String PAGE_2 = "Pages/page2/Page2.html";
    private final String SIGN_UP_URL = "Pages/loginPage/LoginPage.html";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Magit myMagit = ServletUtils.getMagitObject(getServletContext());
        response.setContentType("text/html;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        if (usernameFromSession == null) {
            String usernameFromParameter = request.getParameter("username");
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                response.sendRedirect(SIGN_UP_URL);
            } else {
                usernameFromParameter = usernameFromParameter.trim();
                synchronized (this) {
                    if (userManager.isUserExists(usernameFromParameter)) {
                        if(userManager.isUserOnline(usernameFromParameter)) {// isUserOnline
                            String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
                            request.setAttribute("loggedInAlready", errorMessage);
                            response.sendRedirect(SIGN_UP_URL);
                        }
                        else {
                            myMagit.loadUserData(userManager.getUserByName(usernameFromParameter));
                            request.getSession(true).setAttribute("username", usernameFromParameter);
                            userManager.getUserByName(usernameFromParameter).setOnline(true);
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
        }
    }
}
