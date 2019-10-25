package myGit.Servlets;
import Exceptions.DataAlreadyExistsException;
import Exceptions.FileErrorException;
import GitObjects.*;
import com.google.gson.Gson;
import myGit.UIUtils.ServletUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

public class RefreshDataServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws FileErrorException, IOException {

        response.setContentType("application/json");
        String usernameFromSession = myGit.UIUtils.SessionUtils.getUsername(request);
        String action = request.getParameter("action");
        String json = "";
        switch (action){
        case ("getUserRepoList"):
        {json = getCurrentUserRepositoriesList(usernameFromSession);
            break;}
        case ("getOtherUserRepoList"):
            String otherUser = request.getParameter("otherUserName") ;
        {json = getOtherUserRepositoriesList(otherUser);
            break;}
        case ("getUsersList"):
        {json = getUsersList(usernameFromSession);
            break;}
        case ("getNotificationForUser"):
        {json = getUserNotificationList(usernameFromSession);
            break;}

    }
        try (PrintWriter out = response.getWriter()) {
        out.println(json);
        out.flush();
    }
}

    private String getCurrentUserRepositoriesList(String usernameFromSession){
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Gson gson = new Gson();
        User currUser = userManager.getUserByName(usernameFromSession);
        List<RepositoryWebData> repos = currUser.getActiveRepositoriesWebData();
        return gson.toJson(repos);
    }

    private String getOtherUserRepositoriesList(String username){
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Gson gson = new Gson();
        User currUser = userManager.getUserByName(username);
        List<RepositoryWebData> repos = currUser.getActiveRepositoriesWebData();
        return gson.toJson(repos);
    }

    private String getUsersList(String usernameFromSession){
        Gson gson = new Gson();
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        List<String> users = userManager.getAllOnlineUsers();
        users.remove(usernameFromSession);
        return gson.toJson(users);
    }

    private String getUserNotificationList(String usernameFromSession){
        Gson gson = new Gson();
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        User currUser = userManager.getUserByName(usernameFromSession);
        List<NotificationObject> msgs = currUser.getUserNotifications();
        return gson.toJson(msgs);
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (FileErrorException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (FileErrorException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet handling parsing and validating of xml file.";
    }
}

