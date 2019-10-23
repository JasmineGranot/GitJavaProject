package myGit.Servlets;

import Engine.Magit;
import GitObjects.Repository;
import GitObjects.User;
import GitObjects.UserManager;
import myGit.*;
import myGit.UIUtils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ForkRepositoryServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        String usernameFromSession = myGit.UIUtils.SessionUtils.getUsername(request);
        UserManager userManager = myGit.UIUtils.ServletUtils.getUserManager(getServletContext());
        Magit magitManager = ServletUtils.getMagitObject(getServletContext());
        String repositoryFromSession = myGit.UIUtils.SessionUtils.getCurrentRepository(request);

        User user = userManager.getUserByName(usernameFromSession);
        Repository repoToClone = user.getUserRepository(repositoryFromSession);
        if (repoToClone == null){
            return; //TODO add error
        }
        magitManager.cloneRemoteToLocal(user, repoToClone);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }
}
