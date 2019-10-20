package myGit.Servlets;

import Engine.Magit;
import GitObjects.User;
import GitObjects.UserManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ForkRepositoryServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Magit myMagit = new Magit();
        String usernameFromSession = UIUtils.SessionUtils.getUsername(request);
        UserManager userManager = UIUtils.ServletUtils.getUserManager(getServletContext());
        User user = userManager.getUserByName(usernameFromSession);
        myMagit.cloneRemoteToLocal(user, "c:\\repo1");
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
}
