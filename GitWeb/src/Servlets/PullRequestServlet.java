package Servlets;

import Engine.Magit;
import GitObjects.PullRequestObject;
import GitObjects.Repository;
import GitObjects.User;
import GitObjects.UserManager;
import UIUtils.ServletUtils;
import Utils.MagitStringResultObject;
import Utils.ResultList;
import com.google.gson.Gson;
import sun.plugin.util.UIUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryNotEmptyException;

@WebServlet(
        urlPatterns = "/prActions"
)

public class PullRequestServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Magit myMagit = ServletUtils.getMagitObject(getServletContext());
        response.setContentType("application/json");

        String usernameFromSession = UIUtils.SessionUtils.getUsername(request);
        User currUser = userManager.getUserByName(usernameFromSession);

        String repoName = UIUtils.SessionUtils.getCurrentRepository(request);
        Repository repo = userManager.getUserRepository(currUser, repoName);

        PullRequestObject pr = UIUtils.SessionUtils.getPullRequest(request);

        String action = request.getParameter("action");
        String json = "";
        switch (action){
            case ("approvePR"):
            {
//                json = getOpenPullRequestsForUser(currUser, repo, myMagit);
                break;
            }
            case ("declinePR"):
            {
//                json = getHead(currUser, repo, myMagit);
                break;
            }

            case ("getPRFiles"):
            {
//                json = getHead(currUser, repo, myMagit);
                break;
            }
            case ("getSrc"):
            {
//                json = getHead(currUser, repo, myMagit);
                break;
            }
            case ("getTarget"):
            {
//                json = getHead(currUser, repo, myMagit);
                break;
            }
            case ("getMsg"):
            {
//                json = getHead(currUser, repo, myMagit);
                break;
            }

        }
        try (PrintWriter out = response.getWriter()) {
            out.println(json);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getOpenPullRequestsForUser(User currUser, Repository repo, Magit myMagit) {
        Gson gson = new Gson();
        ResultList<PullRequestObject> prsObj = myMagit.getPullRequests(currUser, repo.getRepoName());
        return gson.toJson(prsObj);
    }

    private String getHead(User currUser, Repository repo, Magit myMagit) {
        Gson gson = new Gson();
        MagitStringResultObject headObj = myMagit.getHeadBranchName(currUser, repo.getRepoName());
        return gson.toJson(headObj);
    }



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet handling parsing and validating of xml file.";
    }
}

