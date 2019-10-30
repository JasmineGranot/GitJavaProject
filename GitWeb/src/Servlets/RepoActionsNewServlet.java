package Servlets;

import Engine.Magit;
import Exceptions.DataAlreadyExistsException;
import Exceptions.InvalidDataException;
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
import java.util.List;

@WebServlet(
        urlPatterns = "/actionOnRepo"
)

public class RepoActionsNewServlet extends HttpServlet {

    protected void processGetRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        Magit myMagit = UIUtils.ServletUtils.getMagitObject(getServletContext());
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String usernameFromSession = UIUtils.SessionUtils.getUsername(request);
        User currUser = userManager.getUserByName(usernameFromSession);
        String repoName = UIUtils.SessionUtils.getCurrentRepository(request);
        Repository repo = userManager.getUserRepository(currUser, repoName);

        String action = request.getParameter("action");
        String json = "";
        switch (action){
            case ("getPullRequests"):
            {
                json = getOpenPullRequestsForUser(currUser, repo, myMagit);
                break;
            }
            case ("getHead"):
            {
                json = getHead(currUser, repo, myMagit);
                break;
            }
            case ("getCommits"):
            {
                json = getCommits(currUser, repo, myMagit);
                break;}
            case ("showWC"):
            {
                json = getWC(currUser, repo, myMagit);
                break;
            }
            case ("getBranches"):
            {
                json = getBranches(currUser, repo, myMagit);
                break;
            }

            case ("getRepo"):
            {
                json = getRepo(repo);
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

    private String getRepo(Repository repo) {
        Gson gson = new Gson();
        return gson.toJson(repo.getRepoName());
    }

    private String getBranches(User currUser, Repository repo, Magit myMagit) {
        Gson gson = new Gson();
        MagitStringResultObject branchObj = myMagit.getBranches(currUser, repo.getRepoName());
        return gson.toJson(branchObj.getDataList());
    }

    private String getCommits(User currUser, Repository repo, Magit myMagit) {
        Gson gson = new Gson();
        ResultList commitObj = myMagit.getCurrentCommits(currUser, repo.getRepoName());
        return gson.toJson(commitObj);
    }

    private String getWC(User currUser, Repository repo, Magit myMagit) {
        Gson gson = new Gson();
        ResultList filesObj = myMagit.getCurrentWC(currUser, repo.getRepoName());
        return gson.toJson(filesObj);
    }

    protected void processPostRequest(HttpServletRequest request, HttpServletResponse response) {
        Magit myMagit = UIUtils.ServletUtils.getMagitObject(getServletContext());
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        response.setContentType("application/json");
        String usernameFromSession = UIUtils.SessionUtils.getUsername(request);
        User currUser = userManager.getUserByName(usernameFromSession);
        String repoName = UIUtils.SessionUtils.getCurrentRepository(request);
        Repository repo = userManager.getUserRepository(currUser, repoName);
        String branchName = request.getParameter("branchName");

        String action = request.getParameter("action");
        String json = "";
        switch (action){
            case ("checkout"):
            {
                json = checkout(currUser, repo, branchName, myMagit);
                break;}
            case ("pull"):
            {
                json = pull(currUser, repo, myMagit);
                break;}
            case ("push"):
            {
                json = push(currUser, repo, myMagit);
                break;
            }
            case ("addBranch"):
            {
                String commitSha1 = request.getParameter("commitSha1");
                json = addBranch(currUser, repo, branchName, commitSha1, myMagit);
                break;
            }
            case ("deleteBranch"):
            {
                json = deleteBranch(currUser, repo, branchName, myMagit);
                break;
            }

            case ("createPullRequest"):
            {
                String branchTarget = request.getParameter("branchTarget");
                String msg = request.getParameter("prMsg");

                json = createPR(currUser, repo, branchName, branchTarget, msg, myMagit);
                break;
            }
            case ("setPullRequestInSession"):{
                String pr = request.getParameter("prObj");
                PullRequestObject prObj = new PullRequestObject(pr);
                UIUtils.SessionUtils.setPullRequest(request, prObj);
                break;
            }

            case("getCommits"):
            {
                String msg = request.getParameter("commitMsg");

                json = createNewCommit(currUser, repo, msg, myMagit);
                break;
            }
            case ("deleteNotification"):
            {
                String msg = request.getParameter("message");
                deleteNotification(currUser, msg);
            }

        }
        try (PrintWriter out = response.getWriter()) {
            out.println(json);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String checkout(User currUser, Repository repo, String branch, Magit myMagit) {
        Gson gson = new Gson();
        MagitStringResultObject resObj = myMagit.checkoutBranch(currUser, repo.getRepoName(), branch, false);
        return gson.toJson(resObj);
    }

    private String pull(User currUser, Repository repo, Magit myMagit) {
        Gson gson = new Gson();

        MagitStringResultObject resObj = null;
        resObj = myMagit.pull(currUser, repo.getRepoName());

        return gson.toJson(resObj);
    }

    private String push(User currUser, Repository repo, Magit myMagit) {
        Gson gson = new Gson();

        MagitStringResultObject resObj = null;
        resObj = myMagit.push(currUser, repo.getRepoName());

        return gson.toJson(resObj);
    }

    private String addBranch(User currUser, Repository repo, String branchName, String commitSha1, Magit myMagit) {
        Gson gson = new Gson();
        MagitStringResultObject resObj = null;
        if (commitSha1.equals("")) {
            MagitStringResultObject commitobj = myMagit.getHeadBranchCommitSha1(currUser, repo.getRepoName());
            if (!commitobj.getIsHasError()) {
                commitSha1 = commitobj.getData();
            }
        }
        resObj = myMagit.addNewBranch(currUser, repo.getRepoName(), branchName, commitSha1, false);


        return gson.toJson(resObj);

    }

    private String createPR(User currUser, Repository repo, String src, String target, String msg, Magit myMagit) {
        Gson gson = new Gson();

        MagitStringResultObject resObj =  myMagit.createPR(currUser, repo.getRepoName(), src, target, msg);

        return gson.toJson(resObj);
    }

    private String deleteBranch(User currUser, Repository repo, String branchToDelete, Magit myMagit) {
        Gson gson = new Gson();

        MagitStringResultObject resObj = null;
        resObj = myMagit.deleteBranch(currUser, repo.getRepoName(), branchToDelete);

        return gson.toJson(resObj);
    }

    private String createNewCommit(User currUser, Repository repo, String commitMsg, Magit myMagit) {
        Gson gson = new Gson();
        MagitStringResultObject resultObject = myMagit.createNewCommit(currUser, repo.getRepoName(), commitMsg, null);
        return gson.toJson(resultObject);
    }

    private void deleteNotification(User user, String msg) {
        user.deleteNotification(msg);
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        processGetRequest(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        processPostRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet handling parsing and validating of xml file.";
    }
}

