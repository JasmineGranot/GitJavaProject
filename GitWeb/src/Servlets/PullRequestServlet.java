package Servlets;

import Engine.Magit;
import GitObjects.PullRequestObject;
import GitObjects.Repository;
import GitObjects.User;
import GitObjects.UserManager;
import UIUtils.ServletUtils;
import Utils.MagitStringResultObject;
import Utils.ResultList;
import Utils.WorkingCopyChanges;
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
        if (pr == null){
            return ;
        }
        String action = request.getParameter("action");
        String json = "";
        switch (action){
            case ("approvePR"):
            {
                json = approvePR(currUser, repo, myMagit, pr);
                break;
            }
            case ("declinePR"):
            {
                String msg = request.getParameter("declineMsg");
                json = declinePR(currUser, repo, myMagit, pr, msg);
                break;
            }

            case ("getPRFiles"):
            {
                json = getFilesDelta(currUser, repo, myMagit, pr);
                break;
            }
            case ("getSrc"):
            {
                json = getSrcBranch(pr);
                break;
            }
            case ("getTarget"):
            {
                json = getTargetBranch(pr);
                break;
            }
            case ("getMsg"):
            {
                json = getPRMsg(pr);
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

    private String approvePR(User currUser, Repository repo, Magit myMagit, PullRequestObject pr) {
        Gson gson = new Gson();
        MagitStringResultObject prsObj = myMagit.approvePR(currUser, repo.getRepoName(), pr);
        return gson.toJson(prsObj);
    }

    private String declinePR(User currUser, Repository repo, Magit myMagit, PullRequestObject pr, String msg) {
        Gson gson = new Gson();
        MagitStringResultObject prsObj = myMagit.declinePR(currUser, repo.getRepoName(),pr,  msg);
        return gson.toJson(prsObj);
    }
    private String getFilesDelta(User currUser, Repository repo, Magit myMagit, PullRequestObject pr) {
        Gson gson = new Gson();
        WorkingCopyChanges prsObj = myMagit.getFilesChangesBetweenBranches(currUser, repo.getRepoName(),
                pr.getBaseToMergeInto(), pr.getTargetToMergeFrom());
        return gson.toJson(prsObj);
    }

    private String getSrcBranch(PullRequestObject pr) {
        Gson gson = new Gson();
        String prsObj = pr.getBaseToMergeInto();
        return gson.toJson(prsObj);
    }
    private String getTargetBranch(PullRequestObject pr) {
        Gson gson = new Gson();
        String prsObj = pr.getTargetToMergeFrom();
        return gson.toJson(prsObj);
    }
    private String getPRMsg(PullRequestObject pr) {
        Gson gson = new Gson();
        String prsObj = pr.getPrMsg();
        return gson.toJson(prsObj);
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

