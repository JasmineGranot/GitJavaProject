package myGit.Servlets;

import Engine.Magit;
import Exceptions.DataAlreadyExistsException;
import Exceptions.FileErrorException;
import GitObjects.RepositoryWebData;
import GitObjects.User;
import GitObjects.UserManager;
import com.google.gson.Gson;
import myGit.UIUtils.ServletUtils;
import myGit.UIUtils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class RepositoryActionServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws JAXBException, DataAlreadyExistsException, FileErrorException {

        response.setContentType("application/json");
        String usernameFromSession = myGit.UIUtils.SessionUtils.getUsername(request);
        String action = request.getParameter("action");
        String json = "";
        switch (action){
            case ("getPullRequests"):
            {json = getCurrentUserRepositoriesList(usernameFromSession);
                break;}
            case ("getHead"):{
                String otherUser = request.getParameter("otherUserName") ;
                break;}
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
            case ("getCommits"):
            {break;}
            case ("createPullRequest"):
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

    private String getCurrentUserRepositoriesList(String usernameFromSession){
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Gson gson = new Gson();
        User currUser = userManager.getUserByName(usernameFromSession);
        List<RepositoryWebData> repos = currUser.getActiveRepositoriesWebData();
        return gson.toJson(repos);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (JAXBException | DataAlreadyExistsException | FileErrorException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (JAXBException | DataAlreadyExistsException | FileErrorException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet handling parsing and validating of xml file.";
    }
}

