package Servlets;

import Engine.Magit;
import GitObjects.Repository;
import GitObjects.User;
import GitObjects.UserManager;
import UIUtils.ServletUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        urlPatterns = "/fork"
)

public class ForkRepositoryServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        String usernameFromSession = UIUtils.SessionUtils.getUsername(request);
        UserManager userManager = UIUtils.ServletUtils.getUserManager(getServletContext());
        Magit magitManager = ServletUtils.getMagitObject(getServletContext());
        String repoToCloneName = request.getParameter("repositoryName");
        String userToCloneFromName = request.getParameter("otherUSer");
        User otherUser = userManager.getUserByName(userToCloneFromName);
        User currentUser = userManager.getUserByName(usernameFromSession);

        Repository repoToClone = otherUser.getUserRepository(repoToCloneName);
        if (repoToClone == null){
            System.out.println("no cloning for you");
            return;
        }
        magitManager.cloneRemoteToLocal(currentUser, repoToClone);
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
