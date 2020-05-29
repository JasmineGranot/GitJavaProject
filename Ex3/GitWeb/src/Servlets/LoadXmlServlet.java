package Servlets;

import GitObjects.User;
import GitObjects.UserManager;
import UIUtils.ServletUtils;
import UIUtils.SessionUtils;
import Engine.Magit;
import Utils.MagitStringResultObject;
import com.google.gson.Gson;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(
        urlPatterns = "/LoadXml"
)

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class LoadXmlServlet extends HttpServlet {
    private final String SERVER_PATH = "c:\\magit-ex3";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        String msg;
        MagitStringResultObject res;

        File newServerFolder = new File(SERVER_PATH);
        if(!newServerFolder.exists()){
            newServerFolder.mkdir();
        }
        response.setContentType("text/html;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Magit magitManager = ServletUtils.getMagitObject(getServletContext());
        User currentUser = userManager.getUserByName(usernameFromSession);
        String content = request.getParameter("file");

        try {
            res = magitManager.validateXML(currentUser, content);
            if (!res.getIsHasError()) {
                res = magitManager.loadXMLForUser(currentUser, content);
                if (!res.getIsHasError()) {
                    msg = finishedLoadingXmlFile(res.getData());
                } else {
                    msg = finishedLoadingXmlFile(res.getErrorMSG());
                }
            } else {
                msg = res.getErrorMSG();
            }
            try (PrintWriter out = response.getWriter()) {
                out.println(msg);
                out.flush();
            }
        } catch(Exception e) {
            msg = e.getMessage();
            try (PrintWriter out = response.getWriter()) {
                out.println(msg);
                out.flush();
            }
            catch (IOException exc) {
                System.out.println(exc.getMessage());
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

    private String finishedLoadingXmlFile(String msg) {
        Gson gson = new Gson();
        return gson.toJson(msg);
    }

    @Override
    public String getServletInfo() {
        return "Servlet handling parsing and validating of xml file.";
    }
}

