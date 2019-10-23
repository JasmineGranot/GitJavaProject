package myGit.Servlets;

import Engine.Magit;
import Exceptions.FileErrorException;
import GitObjects.Folder;
import GitObjects.UserManager;
import myGit.UIUtils.ServletUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Console;
import java.io.File;
import java.io.IOException;

public class SystemServlet extends HttpServlet implements ServletContextListener {
    final String SERVER_PATH = "c:\\magit-ex3";
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        File newServerFolder = new File(SERVER_PATH);
        if(!newServerFolder.exists()){
            newServerFolder.mkdir();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        try {
            userManager.removeFolders();
            File serverFolder = new File(SERVER_PATH);
            serverFolder.delete();
        } catch (FileErrorException e) {
            e.printStackTrace();
        }


    }
}
