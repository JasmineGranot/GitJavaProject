package Servlets;

import Exceptions.FileErrorException;
import GitObjects.UserManager;
import UIUtils.ServletUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import java.io.File;

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
