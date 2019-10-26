package Servlets;

import Engine.Magit;
import Exceptions.DataAlreadyExistsException;
import Exceptions.FileErrorException;
import GitObjects.*;
import UIUtils.ServletUtils;
import Utils.MagitStringResultObject;
import Utils.ResultList;
import com.google.gson.Gson;
import UIUtils.ServletUtils;
import UIUtils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class RepositoryActionServlet extends HttpServlet {

    private Magit myMagit = UIUtils.ServletUtils.getMagitObject(getServletContext());

    protected void processGetRequest(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("application/json");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String usernameFromSession = UIUtils.SessionUtils.getUsername(request);
        User currUser = userManager.getUserByName(usernameFromSession);
        String repoName = request.getParameter("repository");
        Repository repo = userManager.getUserRepository(currUser, repoName);

        String action = request.getParameter("action");
        String json = "";
        switch (action){
            case ("getPullRequests"):
            {json = getOpenPullRequestsForUser(currUser, repo);
                break;}
            case ("getHead"):{
                String otherUser = request.getParameter("otherUserName") ;
                break;}
            case ("getCommits"):
            {break;}
            case ("showWC"):
            {break;}
            case ("getBranches"):
            {break;}

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

    protected void processPostRequest(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("application/json");
        String usernameFromSession = UIUtils.SessionUtils.getUsername(request);
        String action = request.getParameter("action");
        String json = "";
        switch (action){
            case ("checkout"):
            {break;}
            case ("pull"):{
                break;}
            case ("push"):
            {break;}
            case ("addBranch"):
            {break;}
            case ("deleteBranch"):
            {break;}
            case ("createPullRequest"):
            {break;}



        }
        try (PrintWriter out = response.getWriter()) {
            out.println(json);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

