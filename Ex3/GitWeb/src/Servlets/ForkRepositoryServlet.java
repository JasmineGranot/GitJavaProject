package Servlets;

import Engine.Magit;
import GitObjects.Repository;
import GitObjects.User;
import GitObjects.UserManager;
import UIUtils.ServletUtils;
import Utils.MagitStringResultObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(
        urlPatterns = "/fork"
)

public class ForkRepositoryServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        String msg;
        MagitStringResultObject res;

        String usernameFromSession = UIUtils.SessionUtils.getUsername(request);
        UserManager userManager = UIUtils.ServletUtils.getUserManager(getServletContext());
        Magit magitManager = ServletUtils.getMagitObject(getServletContext());
        String repoToCloneName = request.getParameter("repositoryName");
        String userToCloneFromName = request.getParameter("otherUSer");
        User otherUser = userManager.getUserByName(userToCloneFromName);
        User currentUser = userManager.getUserByName(usernameFromSession);

        Repository repoToClone = otherUser.getUserRepository(repoToCloneName);
        if (repoToClone == null){
            msg = "Something went wrong while trying to clone!\nFound the repository you wanted to clone as NULL!";
        }
        else {
            res = magitManager.cloneRemoteToLocal(currentUser, repoToClone);
            if (!res.getIsHasError()) {
                msg = res.getData();
            } else {
                msg = res.getErrorMSG();
            }
        }

        try (PrintWriter out = response.getWriter()) {
            out.println(msg);
            out.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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
