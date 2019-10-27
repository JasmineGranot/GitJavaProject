package Servlets;

import Engine.Magit;
import Exceptions.DataAlreadyExistsException;
import Exceptions.FileErrorException;
import Exceptions.InvalidDataException;
import GitObjects.*;
import UIUtils.ServletUtils;
import Utils.MagitStringResultObject;
import Utils.ResultList;
import com.google.gson.Gson;
import UIUtils.ServletUtils;
import UIUtils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryNotEmptyException;
import java.util.LinkedList;
import java.util.List;

@WebServlet(
        urlPatterns = "/actionServlet"
)

public class RepositoryActionServlet extends HttpServlet {

    private final Magit myMagit = UIUtils.ServletUtils.getMagitObject(getServletContext());
    private final UserManager userManager = ServletUtils.getUserManager(getServletContext());

    protected void processGetRequest(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("application/json");
        String usernameFromSession = UIUtils.SessionUtils.getUsername(request);
        User currUser = userManager.getUserByName(usernameFromSession);
        String repoName = request.getParameter("repository");
        Repository repo = userManager.getUserRepository(currUser, repoName);

        String action = request.getParameter("action");
        String json = "";
        switch (action){
            case ("getPullRequests"):
            {
                json = getOpenPullRequestsForUser(currUser, repo);
                break;
            }
            case ("getHead"):
            {
                json = getHead(currUser, repo);
                break;
            }
            case ("getCommits"):
            {
                json = getCommits(currUser, repo);
                break;}
            case ("showWC"):
            {
                json = getWC(currUser, repo);
                break;
            }
            case ("getBranches"):
            {
                json = getBranches(currUser, repo);
                break;
            }
            case ("setRepoInSession"):
            {
                String repositoryNameToLoad = request.getParameter("repoName");
                UIUtils.SessionUtils.setCurrentRepository(request, repositoryNameToLoad);
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

    private String getOpenPullRequestsForUser(User currUser, Repository repo) {
        Gson gson = new Gson();
        ResultList<PullRequestObject> prsObj = myMagit.getPullRequests(currUser, repo.getRepoName());
        return gson.toJson(prsObj);
    }

    private String getHead(User currUser, Repository repo) {
        Gson gson = new Gson();
        MagitStringResultObject headObj = myMagit.getHeadBranchName(currUser, repo.getRepoName());
        return gson.toJson(headObj);
    }

    private String getBranches(User currUser, Repository repo) {
        Gson gson = new Gson();
        MagitStringResultObject branchObj = myMagit.getBranches(currUser, repo.getRepoName());
        return gson.toJson(branchObj);
    }

    private String getCommits(User currUser, Repository repo) {
        Gson gson = new Gson();
        ResultList commitObj = myMagit.getCurrentCommits(currUser, repo.getRepoName());
        return gson.toJson(commitObj);
    }

    private String getWC(User currUser, Repository repo) {
        Gson gson = new Gson();
        ResultList filesObj = myMagit.getCurrentWC(currUser, repo.getRepoName());
        return gson.toJson(filesObj);
    }

    protected void processPostRequest(HttpServletRequest request, HttpServletResponse response) {

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
                json = checkout(currUser, repo, branchName);
                break;}
            case ("pull"):
            {
                json = pull(currUser, repo);
                break;}
            case ("push"):
            {
                json = push(currUser, repo);
                break;
            }
            case ("addBranch"):
            {
                String commitSha1 = request.getParameter("commitSh1");
                json = addBranch(currUser, repo, branchName, commitSha1);
                break;
            }
            case ("deleteBranch"):
            {
                json = deleteBranch(currUser, repo, branchName);
                break;
            }

            case ("createPullRequest"):
            {
                String branchTarget = request.getParameter("branchTarget");
                String msg = request.getParameter("prMsg");

                json = createPR(currUser, repo, branchName, branchTarget, msg);
                break;
            }
            case ("setRepoInSession"):
            {
                String repositoryNameToLoad = request.getParameter("repoName");
                UIUtils.SessionUtils.setCurrentRepository(request, repositoryNameToLoad);
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

    private String checkout(User currUser, Repository repo, String branch) {
        Gson gson = new Gson();

        MagitStringResultObject resObj = null;
        try {
            resObj = myMagit.checkoutBranch(currUser, repo.getRepoName(), branch, false);
        } catch (DirectoryNotEmptyException e) {
            e.printStackTrace();
        }
        return gson.toJson(resObj);
    }

    private String pull(User currUser, Repository repo) {
        Gson gson = new Gson();

        MagitStringResultObject resObj = myMagit.pull(currUser, repo.getRepoName());

        return gson.toJson(resObj);
    }

    private String push(User currUser, Repository repo) {
        Gson gson = new Gson();

        MagitStringResultObject resObj = myMagit.push(currUser, repo.getRepoName());

        return gson.toJson(resObj);
    }

    private String addBranch(User currUser, Repository repo, String branchName, String commitSha1) {
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

    private String createPR(User currUser, Repository repo, String src, String target, String msg) {
        Gson gson = new Gson();

        MagitStringResultObject resObj =  myMagit.createPR(currUser, repo.getRepoName(), src, target, msg);

        return gson.toJson(resObj);
    }

    private String deleteBranch(User currUser, Repository repo, String branchToDelete) {
        Gson gson = new Gson();

        MagitStringResultObject resObj = myMagit.deleteBranch(currUser, repo.getRepoName(), branchToDelete);

        return gson.toJson(resObj);
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

