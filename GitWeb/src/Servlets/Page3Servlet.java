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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryNotEmptyException;

@WebServlet(
        urlPatterns = "/page3"
)

public class Page3Servlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        String usernameFromSession = UIUtils.SessionUtils.getUsername(request);
        UserManager userManager = UIUtils.ServletUtils.getUserManager(getServletContext());
        String action = request.getParameter("action");
        String repo = request.getParameter("repoName");
        response.setContentType("application/json");
        String json = "";
        switch (action){
            case ("setRepo"):
            {
                UIUtils.SessionUtils.setCurrentRepository(request, repo);
                break;
            }

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

    @Override
    public String getServletInfo() {
        return "Servlet handling parsing and validating of xml file.";
    }
}
